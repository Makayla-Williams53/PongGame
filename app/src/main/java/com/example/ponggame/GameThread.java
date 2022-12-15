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
    //allows editing of screen obejcts
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
        long aNextGameTick = SystemClock.uptimeMillis();
        int skipTicks = 1000/FPS;

        while(aRun)
        {
            Canvas c = null;
            try
            {
                c = aSurfaceHolder.lockCanvas(null);
                if(c != null)
                {
                    synchronized (aSurfaceHolder)
                    {
                        if(aGameState == STATE_RUNNING)
                        {
                            aPongTable.update(c);
                        }//end inner if
                        synchronized (aRunLock)
                        {
                            if(aRun)
                            {
                                aPongTable.draw(c);
                            }//end inner if
                        }//end inner synchronized
                    }//end synchronized
                }//end outer if
            }//end try
            catch(Exception e)
            {
                e.printStackTrace();
            }//end catch
            finally
            {
                if(c != null)
                {
                    aSurfaceHolder.unlockCanvasAndPost(c);
                }//end if
            }//end finally

        aNextGameTick += skipTicks;
        long sleepTime = aNextGameTick - SystemClock.uptimeMillis();
        if(sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            }//end try
            catch (InterruptedException e) {

            }//end catch
        }
        }//end if
    }//end run

    public void setState(int state)
    {
        synchronized (aSurfaceHolder)
        {
            aGameState = state;
            Resources res = aCtx.getResources();
            switch(aGameState)
            {
                case STATE_READY:
                    setUpNewRound();
                    break;
                case STATE_RUNNING:
                    //hide the status
                    hideStatus();
                    break;
                case STATE_WIN:
                    setStatusText(res.getString(R.string.mode_win));
                    aPongTable.getPlayer().score++;
                    setUpNewRound();
                    break;
                case STATE_LOSE:
                    setStatusText(res.getString(R.string.mode_lose));
                    aPongTable.getOpponent().score++;
                    break;
                case STATE_PAUSED:
                    setStatusText(res.getString(R.string.mode_paused));
                    break;
            }//end switch
        }//end synchronized
    }//end setState

    public void setUpNewRound()
    {
        synchronized(aSurfaceHolder)
        {
            aPongTable.setUpTable();
        }//end synchronized
    }//end setUpNewRound

    public void setRunning(boolean running)
    {
        synchronized(aRunLock)
        {
            aRun = running;
        }//end synchronized
    }//end setRunning

    public boolean sensorsOn()
    {
        return aSensorOn;
    }

    public boolean isBetweenRounds()
    {
        return aGameState != STATE_RUNNING;
    }

    public void setStatusText(String text)
    {
        Message msg = aGameStatusHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("text", text);
        b.putInt("visibility", View.VISIBLE);
        msg.setData(b);
        aGameStatusHandler.sendMessage(msg);
    }

    private void hideStatus()
    {
        Message msg = aGameStatusHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("visibility", View.INVISIBLE);
        msg.setData(b);
        aGameStatusHandler.sendMessage(msg);
    }

    public void setScoreText( String playerScore, String opponentScore)
    {
        Message msg = aScoreHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("player", playerScore);
        b.putString("opponent", opponentScore);
        msg.setData(b);
        aGameStatusHandler.sendMessage(msg);
    }


}//end GameThread






























