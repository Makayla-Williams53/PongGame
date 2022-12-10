package com.example.ponggame;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball
{
    public float circleX;
    public float circleY;
    public float velocityX;
    public float velocityY;
    private int radius;
    private Paint paint;

    public Ball(int radius, Paint paint)
    {
        this.circleX = circleX;
        this.circleY = circleY;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }//end Ball constructor

    public void draw(Canvas canvas)
    {
        canvas.drawCircle(circleX,circleY,radius,paint);
    }//end draw

    public void moveBall(Canvas canvas)
    {
        circleX += velocityX;
        circleY += velocityY;
        if(circleY<radius)
        {
            circleY = radius;
        }//end if
        else if(circleY + radius > canvas.getHeight())
        {
            circleY = canvas.getHeight() - radius - 1;
        }//end else if
    }//end moveBall

    public int getRadius()
    {
        return radius;
    }//end getRadius

    public String toString()
    {
        return "circleX = " + circleX + "circleY = " + circleY + "VelocityX = " + velocityX + "VelocityY" + velocityY;
    }//end toString

}//end Ball class
