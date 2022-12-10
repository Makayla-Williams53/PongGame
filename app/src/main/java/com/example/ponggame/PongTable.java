package com.example.ponggame;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


public class PongTable extends SurfaceView implements SurfaceHolder.Callback
{
    public static final String TAG = PongTable.class.getSimpleName();
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
    public void setUpPongTable(Context ctx, AttributeSet attr)
    {
        //refers to the custom colors and things I have made
        aContext = ctx;
        aHolder = getHolder();
        //allows the use of lifecycle stages(called callbacks) they are onCreate(), onStart(), onResume(), onPause(), on Stop(), and onDestroy()
        aHolder.addCallback(this);

        //Game Thread or Game Loop Initialized
        TypedArray a = ctx.obtainStyledAttributes(attr,R.styleable.PongTable);
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
        aOpponent = new Player(paddleWidth, paddleHeight, playerPaint);

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
        aNetPaint.setPathEffect(new DashPathEffect(new float[]{5,5}, 0));

        //Draw Bounds
        aTableBoundPaint = new Paint();
        aTableBoundPaint.setAntiAlias(true);
        aTableBoundPaint.setColor(Color.BLACK);
        aTableBoundPaint.setStyle(Paint.Style.STROKE);
        aTableBoundPaint.setStrokeWidth(15.0f);

        //set up AI
        //change later
        aAiMoveProvability = 0.8f;
    }//end initPongTable


    //All the MANDATORY functions that came with those implementations and extends
    public PongTable(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }//end PongTable

    public PongTable(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }//end PongTable

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {

    }//end surfaceCreated

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {

    }//end surfaceChanged

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {

    }//end surfaceDestroyed
}//end PongTable class
