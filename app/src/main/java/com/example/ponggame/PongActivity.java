package com.example.ponggame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class PongActivity extends AppCompatActivity
{

    private GameThread aGameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pong);

        final PongTable table = (PongTable) findViewById(R.id.pongTable);
        table.setScoreOpponent((TextView) findViewById(R.id.tvScoreOpponent));
        table.setScorePlayer((TextView) findViewById(R.id.tvScorePlayer));
        table.setStatusView((TextView) findViewById(R.id.tvGameStatus));

        aGameThread = table.getGame();

    }//end onCreate
}//end PongActivity class