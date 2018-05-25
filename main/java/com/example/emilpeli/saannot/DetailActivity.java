package com.example.emilpeli.saannot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import  com.example.emilpeli.saannot.R;

public class DetailActivity extends Activity {

    /*
     *
     *This is the DetailActivity class that controls the info screen when you press the button
     *
     *
     */


    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // draw activity detail layout
        setContentView(R.layout.activity_detail);

        // get intent which has used to open this activity
        Intent intent = getIntent();

        // get data from intent
        Bundle bundle = intent.getExtras();

        // get phone name
        String phone = bundle.getString("phone");

        // update text and image views to show data
        TextView textView = (TextView) findViewById(R.id.phoneTextView);
        textView.setText(phone);
        ImageView imageView = (ImageView) findViewById(R.id.phoneImageView);



        // show phone image. images should be saved to drawable folder


        switch (phone) {
            case "Lääkintämiehet":
                imageView.setImageResource(R.drawable.hands);

                break;
            case "PST":
                imageView.setImageResource(R.drawable.balls);
                break;
            case "Rivimiehet":
                imageView.setImageResource(R.drawable.mask);
                break;
            case "Päällystö":
                imageView.setImageResource(R.drawable.map);
                break;
            case "Tuomarit":
                imageView.setImageResource(R.drawable.guns);
                break;
            case "Kielletty pelaaminen":
                imageView.setImageResource(R.drawable.vest);
                break;
            case "Huivi":
                imageView.setImageResource(R.drawable.hands);
                break;
            case "Osuma":
                imageView.setImageResource(R.drawable.balls);
                break;
            case "Liput":
                imageView.setImageResource(R.drawable.mask);
                break;
        }

    }
    public void backButtonPressed(View view) {
        // finish and close activity
        finish();
    }

}
