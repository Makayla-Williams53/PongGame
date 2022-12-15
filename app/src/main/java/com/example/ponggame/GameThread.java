package com.example.ponggame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.view.SurfaceHolder;

import android.os.Handler;
import android.view.View;

//allows for multiple threads of code to be running concurrently
//also allows to manipulate the priority of each thread
public class GameThread extends Thread
{
    //variables for each of the states of the game 0:Ready, 1:Paused, 2:Running, 3:Win, 4:Lose
    public static final int STATE_READY = 0;
    public static final int STATE_PAUSED = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_WIN = 3;
    public static final int STATE_LOSE = 4;

    //variables for each part of the game
    private boolean aSensorOn;
    //custom colors and such
    private final Context aCtx;
    //allows editing of screen objects
    private final SurfaceHolder aSurfaceHolder;
    private final Handler aGameStatusHandler;
    //pong table object
    private final PongTable aPongTable;
    private final Handler aScoreHandler;

    //variables for the state of the game
    private boolean aRun = false;
    private int aGameState;
    private Object aRunLock;

    private static final int FPS = 60;

    //constructor
    public GameThread(Context aCtx, SurfaceHolder aSurfaceHolder,  PongTable aPongTable, Handler aGameStatusHandler, Handler aScoreHandler)
    {
        this.aCtx = aCtx;
        this.aSurfaceHolder = aSurfaceHolder;
        this.aGameStatusHandler = aGameStatusHandler;
        this.aPongTable = aPongTable;
        this.aScoreHandler = aScoreHandler;

        this.aRunLock = new Object();
    }//end GameThread constructor

    //runs the class using Runnable which Thread implements
    @Override
    public void run()
    {
        //creates a variable that contains seconds
        long aNextGameTick = SystemClock.uptimeMillis();
        int skipTicks = 1000/FPS;

        //while the gme is running
        while(aRun)
        {
            //create a null canvas
            Canvas c = null;

            //tries
            try
            {
                //c is a variable that holds a locked version of the surfaceHolder null rectangle
                //lockCanvas prevents there being multiple lines of code trying to edit the same canvas
                c = aSurfaceHolder.lockCanvas(null);
                //if the surfaceholder is not null
                if(c != null)
                {
                    //prevents this line of code from being accessed until the first one has ran completely through
                    synchronized (aSurfaceHolder)
                    {
                        //if the game is running
                        if(aGameState == STATE_RUNNING)
                        {
                            //update the pongTable
                            aPongTable.update(c);
                        }//end inner if
                        //prevents this line within the synchronized from being accessed until the first one has ran completely through
                        synchronized (aRunLock)
                        {
                            //if running is true
                            if(aRun)
                            {
                                //draw the canvas
                                aPongTable.draw(c);
                            }//end inner if
                        }//end inner synchronized
                    }//end synchronized
                }//end outer if
            }//end try
            //if this exception is thrown catch it
            catch(Exception e)
            {
                //and printout the stackTrace
                e.printStackTrace();
            }//end catch
            //after all of that do this
            finally
            {
                //if c is not null
                if(c != null)
                {
                    //unlock the canvas so that it can be accessed in other places
                    aSurfaceHolder.unlockCanvasAndPost(c);
                }//end if
            }//end finally

        //increase gameticks by the frames per second
        aNextGameTick += skipTicks;
        //set up a long variable the is the number of gameticks minute the number of seconds
        long sleepTime = aNextGameTick - SystemClock.uptimeMillis();
        //if the timer is greater than 0
        if(sleepTime > 0)
        {
            try
            {
                //stop the code for as long as sleepTimer is
                Thread.sleep(sleepTime);
            }//end try
            //if an interruptedException is thrown catch it
            catch (InterruptedException e) {
                e.printStackTrace();
            }//end catch
        }//end if
        }//end while
    }//end run

    //sets the state of the game
    public void setState(int state)
    {
        //prevents access to this code until the first call is completely done
        synchronized (aSurfaceHolder)
        {
            //set game state to a number
            aGameState = state;
            //set resources variable
            Resources res = aCtx.getResources();
            //tests on variable against multiple cases(similar to an if else?)
            switch(aGameState)
            {
                //if the switch(aGameState) is equal to the case(STATE_READY)
                case STATE_READY:
                    //set up for a new round
                    setUpNewRound();
                    //I'm sorry for the breaks I truly tried to find a way to not use them but I obviously failed miserably forgive me
                    break;
                //if the switch(aGameState) is equal to the case(STATE_RUNNING)
                case STATE_RUNNING:
                    //hide the status textView
                    hideStatus();
                    //again I am sorry
                    break;
                //if the switch(aGameState) is equal to the case(STATE_WIN)
                case STATE_WIN:
                    //set the status screen to "you win"
                    setStatusText(res.getString(R.string.mode_win));
                    //increase the players score
                    aPongTable.getPlayer().score++;
                    //set up for a new round
                    setUpNewRound();
                    //I know I know it's bad im sorry
                    break;
                //if the switch(aGameState) is equal to the case(STATE_LOSE)
                case STATE_LOSE:
                    //set the status screen to "you lose"
                    setStatusText(res.getString(R.string.mode_lose));
                    //increase opponents score
                    aPongTable.getOpponent().score++;
                    //set up for new round
                    setUpNewRound();
                    //so sorry
                    break;
                //if the switch(aGameState) is equal to the case(
                case STATE_PAUSED:
                    //set the status screen to "pause"
                    setStatusText(res.getString(R.string.mode_paused));
                    //I promise this should be the last one
                    break;
            }//end switch
        }//end synchronized
    }//end setState

    //set up the game for another round
    public void setUpNewRound()
    {
        //again prevents this line from being accessed multiple times at once
        synchronized(aSurfaceHolder)
        {
            //setUpTable call
            aPongTable.setUpTable();
        }//end synchronized
    }//end setUpNewRound

    //When the game is running
    public void setRunning(boolean running)
    {
        //prevents the line from being called numerous times at once and it getting confused over which one to do first
        synchronized(aRunLock)
        {
            //change the aRun variable to hold that it is in fact running
            aRun = running;
        }//end synchronized
    }//end setRunning

    //boolean getter to see if the sensors are on
    public boolean sensorsOn()
    {
        return aSensorOn;
    }//end sensorsOn

    //boolean getter to see if the game is running or not
    public boolean isBetweenRounds()
    {
        return aGameState != STATE_RUNNING;
    }//end isBetweenRounds

    //set the Status text view
    public void setStatusText(String text)
    {
        //creates a message holding what the Status Handler is holding
        Message msg = aGameStatusHandler.obtainMessage();
        //creates a bundle
        //a java property that holds specific data
        Bundle b = new Bundle();
        //inserts text and visibility to said bundle
        b.putString("text", text);
        b.putInt("visibility", View.VISIBLE);
        //sets the message the bundle
        msg.setData(b);
        //sends the message through the status handler
        aGameStatusHandler.sendMessage(msg);
    }//end setStatusText

    //hides teh status text view for when its running
    private void hideStatus()
    {
        //creates a message and sets it to invisible
        Message msg = aGameStatusHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("visibility", View.INVISIBLE);
        msg.setData(b);
        //then sends said message
        aGameStatusHandler.sendMessage(msg);
    }//end hideStatus

    //sets the score textviews
    public void setScoreText( String playerScore, String opponentScore)
    {
        //creates a message that holds the opponent and player scores
        Message msg = aScoreHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("player", playerScore);
        b.putString("opponent", opponentScore);
        msg.setData(b);
        //sends said message
        aGameStatusHandler.sendMessage(msg);
    }//end setScoreText

}//end GameThread






























