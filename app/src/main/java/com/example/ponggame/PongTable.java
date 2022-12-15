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
    //creates a GameThread object
    private GameThread aGame;

    //creates textViews variables for the various textviews on the screen
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

    //final ints for the speeds of the paddle and ball
    public static float PADDLE_SPEED = 15.0f;
    public static float BALL_SPEED = 15.0f;

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
                    //this handler contain the message of visibility and text and hides the text and sets the text
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        aStatus.setVisibility(msg.getData().getInt("visibility"));
                        aStatus.setText(msg.getData().getString("text"));
                    }
                }, new Handler(){
                    //this handler contains the message of the player and opponent information is string form
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        aScorePlayer.setText(msg.getData().getString("player"));
                        aScoreOpponent.setText(msg.getData().getString("opponent"));
                    }
        });

        //sets a typedArray to contain the custom attributed given earlier
        TypedArray a = ctx.obtainStyledAttributes(attr, R.styleable.PongTable);
        ///sets the paddle dimensions and ball radius to the attributed made earlier
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
        aTableBoundPaint.setStrokeWidth(15.f);

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

        //sets the score text view to the string values of the player and opponent scores
        aGame.setScoreText(String.valueOf(aPlayer.score), String.valueOf(aOpponent.score));

        //calls the draw functions for the players and ball
        aPlayer.draw(canvas);
        aOpponent.draw(canvas);
        aBall.draw(canvas);
    }//end onDraw

    //Pong table constructors
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
        //sets the game to running and starts it
        aGame.setRunning(true);
        aGame.start();
    }//end surfaceCreated

    //updates the surface
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height)
    {
        //sets the table dimensions and starts a new round
        aTableWidth = width;
        aTableHeight = height;
        aGame.setUpNewRound();
    }//end surfaceChanged

    //end of the game
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        //creates a boolean called retry and sets it to true
        boolean retry = true;
        //stops the game
        aGame.setRunning(false);
        //whilst it is true
        while(retry)
        {
            //try this
            try
            {
                //
                aGame.join();
                //sets retry to false
                retry = false;
            }//end try
            //catch interruptedException and print out a stackTrace
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }//end catch
        }//end while
    }//end surfaceDestroyed

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

    //updates the screen based on who collided with what
    public void update(Canvas canvas)
    {
        //if the player hits the ball
        if(checkCollisionPlayer(aPlayer, aBall))
        {
            handleCollision(aPlayer, aBall);
        }//end if
        //if the opponent hits the ball
        else if(checkCollisionPlayer(aOpponent, aBall))
        {
            handleCollision(aOpponent, aBall);
        }//end first else if
        //if the ball hits the top or bottom wall
        else if(checkCollisionTopOrBottomWall())
        {
            aBall.velocityY = -aBall.velocityY;
        }//end second else if
        //if the ball hits the left wall
        else if(checkCollisionWithLeftWall())
        {
            aGame.setState(GameThread.STATE_LOSE);
            return;
        }//end third else if
        //if the ball hits the right wall
        else if(checkCollisionWithRightWall())
        {
            aGame.setState(GameThread.STATE_WIN);
            return;
        }//end forth else if

        //if the timer has been going longer than the AIProbability doAI
        if(new Random(System.currentTimeMillis()).nextFloat() < aAiMoveProvability)
        {
            doAI();
        }//end if
        aBall.moveBall(canvas);
    }//end update

    //checks if the paddle hits the ball
    private boolean checkCollisionPlayer(Player player, Ball ball)
    {
        //checks if the bound intercepts with the ball radius
        return player.bounds.intersect(ball.circleX - ball.getRadius(), ball.circleY - ball.getRadius(), ball.circleX + ball.getRadius(), ball.circleY + ball.getRadius());
    }//end checkCollision Player

    //sees if the ball hits the top or bottom wall
    private boolean checkCollisionTopOrBottomWall()
    {
        return ((aBall.circleY <= aBall.getRadius()) || (aBall.circleY + aBall.getRadius() >= aTableHeight - 1));
    }//end checkCollisionTopOrBottomWall

    //sees if the ball hit the left wall
    private boolean checkCollisionWithLeftWall()
    {
        return aBall.circleX <= aBall.getRadius();
    }//end checkCollisionWithLeftWall

    //sees if the ball hit the right wall
    private boolean checkCollisionWithRightWall()
    {
        return aBall.circleX + aBall.getRadius() >= aTableWidth - 1;
    }//end checkCollisionWithRightWall

    //deals with the collisions between the paddle and ball
    private void handleCollision(Player player, Ball ball)
    {
        //increases the ball and makes it go in the alternate direction
        ball.velocityX = - ball.velocityX * 1.05f;
        //if it is the player paddle
        if(player == aPlayer)
        {
            //set the ball x to the next to the player paddle right bound
            ball.circleX = aPlayer.bounds.right + ball.getRadius();
        }//end if
        //if it is the opponent paddle
        else if(player == aOpponent)
        {
            //set the ball x to the next to the opponent paddle left bound
            ball.circleX = aOpponent.bounds.left -ball.getRadius();
            //increases the opponent paddle speed
            PADDLE_SPEED = PADDLE_SPEED * 1.03f;
        }//end else if
    }//end handleCollision

    //touch screen events
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //if the sensors are not on
        if(!aGame.sensorsOn())
        {
            //tests a variable against multiple conditions
            switch(event.getAction())
            {
                //if the switch(event) is equal to the case(ACTION_DOWN)
                case MotionEvent.ACTION_DOWN:
                    //tests of the game is between rounds
                    if(aGame.isBetweenRounds())
                    {
                        //sets the game to running
                        aGame.setState(GameThread.STATE_RUNNING);
                    }//end if
                    else
                    {
                        //tests if the user is touching the paddle
                        if(isTouchOnPaddle(event, aPlayer))
                        {
                            //set moving to true and change the variable that holds the y
                            aMoving = true;
                            aLastTouchY = event.getY();
                        }//end inner if
                    }//end else
                    //so sorry about the break but I truly couldn't find a way around using them
                    break;
                //if the switch(aGameState) is equal to the case(ACTION_MOVE)
                case MotionEvent.ACTION_MOVE:
                    //if the paddle is moving
                    if(aMoving) {
                        //change the player y
                        float y = event.getY();
                        float dy = y - aLastTouchY;
                        aLastTouchY = y;
                        movePlayerPaddle(dy, aPlayer);
                    }//end if
                    //forgive me with all the breaks
                    break;
                //if the switch(aGameState) is equal to the case(ACTION_UP)
                case MotionEvent.ACTION_UP:
                    //set moving to false
                    aMoving = false;
                    //again so sorry
                    break;
            }//end switch
        }//end if
        else
        {
            //if the user is moving touching the paddle
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                //if the game is between rounds
                if(aGame.isBetweenRounds())
                {
                    //set the game to running
                    aGame.setState(GameThread.STATE_RUNNING);
                }//end inner if
            }//end outer if
        }//end else
        return true;
    }//end onTouchEvent

    //sees if the player is touching the paddle test
    public boolean isTouchOnPaddle(MotionEvent event, Player aPlayer)
    {
        //tests to see if the bounds of aPlayer contains the passed in object same x and y
        return aPlayer.bounds.contains(event.getX(), event.getY());
    }//end isTouchOnPaddle

    //getter
    public GameThread getGame()
    {
        return aGame;
    }//end getGame

    //function moves player paddle up and down
    public void movePlayerPaddle(float y, Player player)
    {
        //prevents this line from being ran multiple times at once
        synchronized (aHolder)
        {
            movePlayer(player, player.bounds.left, player.bounds.top + y);
        }//end synchronized
    }//end movePlayerPaddle

    //sets the player bounds
    public synchronized void movePlayer(Player player, float left, float top)
    {
        //sets the left bound to at least two in
        if(left < 2)
        {
            left = 2;
        }//end if
        //but is at least is two away from the right
        else if(left + player.getPaddleWidth() >= aTableWidth - 2)
        {
            left = aTableWidth - player.getPaddleWidth() - 2;
        }//end else
        //sets the top bound to be at least 0
        if(top < 0)
        {
            top = 0;
        }//end if
        //but at least is one away from the bottom
        else if(top + player.getPaddleHeight() >= aTableHeight)
        {
            top = aTableHeight - player.getPaddleHeight() - 1;
        }//end else if

        //offsets the player bounds by left and top but keeps the width and height the same
        player.bounds.offsetTo(left, top);
    }//end movePlayer

    //set up places the parts
    public void setUpTable()
    {
        placeBall();
        placePlayers();
    }//end setUpTable

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
    }//en// d placeBall

    //getters
    public Player getPlayer()
    {
        return aPlayer;
    }//end getPlayer
    public Player getOpponent()
    {
        return aOpponent;
    }//end getOpponent
    public Ball getBall()
    {
        return aBall;
    }//end getBall

    //setters
    public void setScorePlayer(TextView view)
    {
        aScorePlayer = view;
    }//end setScorePlayer
    public void setScoreOpponent(TextView view)
    {
        aScoreOpponent = view;
    }//end setScoreOpponent
    public void setStatusView(TextView view)
    {
        aStatus = view;
    }//end setStatusView

}//end PongTable class