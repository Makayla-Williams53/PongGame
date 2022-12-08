package com.example.ponggame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Player
{
    private int paddleWidth;
    private int paddleHeight;
    public int score;
    private Paint paint;
    public RectF bounds;

    public Player(int paddleWidth, int paddleHeight, Paint paint)
    {
        this.paddleWidth = paddleWidth;
        this.paddleHeight = paddleHeight;
        this.paint = paint;
        score = 0;
        bounds = new RectF(0, 0, paddleWidth, paddleWidth);
    }//end Player constructor

    public void draw(Canvas canvas)
    {
        canvas.drawRoundRect(bounds, 5, 5, paint);
    }//end draw

    public int getPaddleWidth()
    {
        return paddleWidth;
    }//end getPaddleWidth

    public int getPaddleHeight() {
        return paddleHeight;
    }//end getPaddleHeight
}//end Player class
