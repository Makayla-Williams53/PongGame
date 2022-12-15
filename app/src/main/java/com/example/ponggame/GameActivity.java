package com.example.ponggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GameActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }//end onCreate

    //onClick for the button
    public void startGame(View view)
    {
        //starts the game activity
        Intent intent = new Intent(GameActivity.this, PongActivity.class);
        startActivity(intent);
    }//end startGame
}//end GameActivity class

//followed a tutorial from Vijay Kumar
//https://www.youtube.com/playlist?list=PLYaEKGt6xfkvFBWYVeYiA6Xc-WSWXIPaH
//followed the tutorial but added certain aspects(splash screen) and commented on what each thing did and how it worked
