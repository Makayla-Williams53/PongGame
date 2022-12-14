package com.example.ponggame;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball
{
    //instance variables
    public float circleX;
    public float circleY;
    public float velocityX;
    public float velocityY;
    private int radius;
    //Paint is a class that allows to draw geometrics and text and such
    private Paint paint;

    //constructor
    public Ball(int radius, Paint paint)
    {
        this.paint = paint;
        this.radius = radius;
        this.velocityX = PongTable.BALL_SPEED;
        this.velocityY = PongTable.BALL_SPEED;

    }//end Ball constructor


    //draws the ball
    public void draw(Canvas canvas)
    {
        canvas.drawCircle(circleX,circleY,radius,paint);
    }//end draw

    //moves the ball
    public void moveBall(Canvas canvas)
    {
        //has circleX/circleY move at the velocity rates
        circleX += velocityX;
        circleY += velocityY;
        if(circleY<radius)
        {
            circleY = radius;
        }//end if
        //if it is too close to the top of the screen that it'll be off frame it moves down
        else if(circleY + radius >= canvas.getHeight())
        {
            circleY = canvas.getHeight() - radius - 1;
        }//end else if
    }//end moveBall

    //getter
    public int getRadius()
    {
        return radius;
    }//end getRadius

    //toString
    public String toString()
    {
        return "circleX = " + circleX + "circleY = " + circleY + "VelocityX = " + velocityX + "VelocityY" + velocityY;
    }//end toString

}//end Ball class
