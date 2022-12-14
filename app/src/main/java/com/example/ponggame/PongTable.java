package com.example.ponggame;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Random;


public class PongTable extends SurfaceView implements SurfaceHolder.Callback
{
    public static final String TAG = PongTable.class.getSimpleName();


    private GameThread aGame;

    private TextView aStatus;
    private TextView aScorePlayer;
    private TextView aScoreOpponent;

    //creates variables for all the moving objects
    private Player aPlayer;
    private Player aOpponent;
    private Ball aBall;
    //object for the line down the middle to separate the 2 sides
    private Paint aNetPaint;
    //sets up the bounds on the sides of the screens
    private Paint aTableBoundPaint;
    private int aTableWidth;
    private int aTableHeight;
    //refers to the custom colors and such I have made
    private Context aContext;

    //allows the editing of images and drawings on the screens
    SurfaceHolder aHolder;

    public static float PADDLE_SPEED = 15.0f;
    public static float BALL_SPEED = 15.0f;

    //change these later for none Ai movement
    //the variables for the Ai
    private float aAiMoveProvability;
    private boolean aMoving = false;
    private float aLastTouchY;

    //function to set up all the attributes for the PongTable
    public void initializeTable(Context ctx, AttributeSet attr)
    {
        //refers to the custom colors and things I have made
        aContext = ctx;
        aHolder = getHolder();
        //allows the use of lifecycle stages(called callbacks) they are onCreate(), onStart(), onResume(), onPause(), on Stop(), and onDestroy()
        aHolder.addCallback(this);

        //Game Thread initialize
        aGame = new GameThread(this.getContext(), aHolder, this,
                new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        aStatus.setVisibility(msg.getData().getInt("visibility"));
                        aStatus.setText(msg.getData().getString("Text"));
                    }
                }, new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        aScorePlayer.setText(msg.getData().getString("player"));
                        aScoreOpponent.setText(msg.getData().getString("opponent"));
                    }
        });

        //Game Thread or Game Loop Initialized
        TypedArray a = ctx.obtainStyledAttributes(attr, R.styleable.PongTable);
        int paddleHeight = a.getInteger(R.styleable.PongTable_paddleHeight, 340);
        int paddleWidth = a.getInteger(R.styleable.PongTable_paddleWidth, 100);
        int ballRadius = a.getInteger(R.styleable.PongTable_ballRadius, 25);

        //set player
        Paint playerPaint = new Paint();
        playerPaint.setAntiAlias(true);
        playerPaint.setColor(ContextCompat.getColor(aContext, R.color.player_color));
        aPlayer = new Player(paddleWidth, paddleHeight, playerPaint);

        //set opponent
        Paint opponentPaint = new Paint();
        opponentPaint.setAntiAlias(true);
        opponentPaint.setColor(ContextCompat.getColor(aContext, R.color.opponent_color));
        aOpponent = new Player(paddleWidth, paddleHeight, opponentPaint);

        //set ball
        Paint ballPaint = new Paint();
        ballPaint.setAntiAlias(true);
        ballPaint.setColor(ContextCompat.getColor(aContext, R.color.ball_color));
        aBall = new Ball(ballRadius, ballPaint);

        //Middle line
        aNetPaint = new Paint();
        aNetPaint.setAntiAlias(true);
        aNetPaint.setColor(Color.WHITE);
        //set opacity
        aNetPaint.setAlpha(80);
        //line style
        aNetPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        aNetPaint.setStrokeWidth(10.f);
        //sets the style or effect that is on the path of the object
        //dashed line
        aNetPaint.setPathEffect(new DashPathEffect(new float[]{5,5}, 0));

        //Draw Bounds
        aTableBoundPaint = new Paint();
        aTableBoundPaint.setAntiAlias(true);
        aTableBoundPaint.setColor(ContextCompat.getColor(aContext, R.color.table_color));
        aTableBoundPaint.setStyle(Paint.Style.STROKE);
        aTableBoundPaint.setStrokeWidth(15.0f);

        //set up AI
        //change later
        aAiMoveProvability = 0.8f;

    }//end initPongTable

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        //draws the background color
        canvas.drawColor(ContextCompat.getColor(aContext, R.color.table_color));
        //draws the bounds rectangle
        canvas.drawRect(0,0,aTableHeight, aTableWidth, aTableBoundPaint);

        //int for the center of the screen
        int mid = aTableWidth/2;
        //draws the center line
        canvas.drawLine(mid,1, mid, aTableHeight - 1, aNetPaint);

        //calls the draw functions for the players and ball
        aPlayer.draw(canvas);
        aOpponent.draw(canvas);
        aBall.draw(canvas);
    }//end onDraw

    //POng table constructors
    public PongTable(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeTable(context, attrs);
    }//end PongTable

    public PongTable(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initializeTable(context, attrs);
    }//end PongTable

    //creates the surface
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        aGame.setRunning(true);
        aGame.start();
    }//end surfaceCreated

    //updates the surface
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height)
    {
        aTableWidth = width;
        aTableHeight = height;
        aGame.setUpNewRound();
    }//end surfaceChanged

    //destroys the surface
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        boolean retry = true;
        aGame.setRunning(false);
        while(retry)
        {
            try
            {
                aGame.join();
                retry = false;
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }//end surfaceDestroyed

    //touch screen events
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(!aGame.sensorsOn())
        {
            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    if(aGame.isBetweenRounds())
                    {
                        aGame.setState(GameThread.STATE_RUNNING);
                    }
                    else
                    {
                        if(isTouchOnPaddle(event,aPlayer))
                        {
                            aMoving = true;
                            aLastTouchY = event.getY();
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(aMoving)
                    {
                        float y = event.getY();
                        float dy = y - aLastTouchY;
                        aLastTouchY = y;
                        movePlayerPaddle(dy, aPlayer);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    aMoving = false;
                    break;
            }//end switch
        }//end if
        else
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                if(aGame.isBetweenRounds())
                {
                    aGame.setState(GameThread.STATE_RUNNING);
                }
            }
        }//end else
        return true;
    }//end onTouchEvent

    public GameThread getGame()
    {
        return aGame;
    }

    //function moves player paddle up and down
    public void movePlayerPaddle(float y, Player player)
    {
        synchronized (aHolder)
        {
            movePlayer(player, player.bounds.left, player.bounds.top + y);
        }//end synchronized
    }//end movePlayerPaddle

    //collision test
    public boolean isTouchOnPaddle(MotionEvent event, Player player)
    {
        //tests to see if the bounds of aPlayer contains the passed in object same x and y
        return aPlayer.bounds.contains(event.getX(), event.getY());
    }//end isTouchOnPaddle

    //sets the player bounds
    public synchronized void movePlayer(Player player, float left, float top)
    {
        //sets the left bound to at least two in
        if(left < 2)
        {
            left = 2;
        }
        //but is at least is two away from the right
        else if(left + player.getPaddleWidth() >= aTableWidth - 2)
        {
            left = aTableWidth - player.getPaddleWidth() - 2;
        }

        //sts the top bound to be at least 0
        if(top < 0)
        {
            top = 0;
        }
        //but at least is one away from the bottom
        else if(top + player.getPaddleHeight() >= aTableHeight)
        {
            top = aTableHeight - player.getPaddleHeight() - 1;
        }

        //offsets the player bounds by left and top but keeps the width and height the same
        player.bounds.offsetTo(left, top);
    }

    //makes opponent paddle move
    //change later
    private void doAI()
    {
        //if the top of the opponent paddle is higher than the ball
        if(aOpponent.bounds.top > aBall.circleY)
        {
            //have the paddle move down
            movePlayer(aOpponent, aOpponent.bounds.left, aOpponent.bounds.top - PADDLE_SPEED);
        }//end if
        //if the top of the paddle is lower than the ball
        else if(aOpponent.bounds.top + aOpponent.getPaddleHeight() < aBall.circleY)
        {
            //have the paddle move up
            movePlayer(aOpponent, aOpponent.bounds.left, aOpponent.bounds.top + PADDLE_SPEED);
        }//end else if
    }//end doAi

    public void update(Canvas canvas)
    {
        //collision detection code with conditionals
        if(new Random(System.currentTimeMillis()).nextFloat() < aAiMoveProvability)
        {
            doAI();
        }
        aBall.moveBall(canvas);
    }//end update

    public void setUpTable()
    {
        placeBall();
        placePlayers();
    }

    //places the player paddles for the start of the game
    private void placePlayers()
    {
        aPlayer.bounds.offsetTo(2, (aTableHeight - aPlayer.getPaddleHeight()) / 2);
        aOpponent.bounds.offsetTo((aTableWidth-aOpponent.getPaddleWidth() - 2), (aTableHeight - aOpponent.getPaddleHeight()) / 2);
    }//end placePlayers

    //places the ball and starts it moving for the start of game
    private void placeBall()
    {
        aBall.circleX = aTableWidth/2;
        aBall.circleY = aTableHeight/2;
        aBall.velocityY = (aBall.velocityY/Math.abs(aBall.velocityY)) * BALL_SPEED;
        aBall.velocityX = (aBall.velocityX/Math.abs(aBall.velocityX)) * BALL_SPEED;
    }//end placeBall

}//end PongTable class