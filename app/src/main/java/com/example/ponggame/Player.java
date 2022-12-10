package com.example.ponggame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Player
{
    //instance variables
    private int paddleWidth;
    private int paddleHeight;
    public int score;
    private Paint paint;
    //This is a class that defines a rectangle plane based on certain coordinates
    public RectF bounds;

    //constructor
    public Player(int paddleWidth, int paddleHeight, Paint paint)
    {
        this.paddleWidth = paddleWidth;
        this.paddleHeight = paddleHeight;
        this.paint = paint;
        score = 0;
        //sets the bounds rectangle to start in the top left and be the width of the parameters passed in
        bounds = new RectF(0, 0, paddleWidth, paddleHeight);
    }//end Player constructor

    //draws bounds
    public void draw(Canvas canvas)
    {
        canvas.drawRoundRect(bounds, 5, 5, paint);
    }//end draw

    //getters
    public int getPaddleWidth()
    {
        return paddleWidth;
    }//end getPaddleWidth

    public int getPaddleHeight()
    {
        return paddleHeight;
    }//end getPaddleHeight

    //toString
    public String toString()
    {
        return "Width = " + paddleWidth + "\nHeight" + paddleHeight + "\nScore = " + score + "\nTop = " +
                bounds.top + "\nLeft = " + bounds.left;
    }//end toString
}//end Player class
