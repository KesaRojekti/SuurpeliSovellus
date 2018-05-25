package com.example.emilpeli.saannot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // find list view. Basic stuff right here
        ListView listview;
        listview = (ListView) findViewById(R.id.listView);

        // generate some data. basics naming. puts them in horizontal line
        String[] phones = new String[]{
                "Lääkintämiehet", "PST", "Rivimiehet", "Päällystö", "Tuomarit", "Kielletty pelaaminen",
                "Huivi", "Osuma", "Liput"
        };

        // add data to ArrayList. resembles the java exercise i did couple of months ago
        final ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, phones);


        // This was the first that displayed all logos the same. adapter and the layout is from rowlayout. text is from textView. Remember this one!!!!!!!
        // add data to ArrayAdapter (default Android ListView style/layout)
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.textView, list);

        // create custom adapter for every logo there is a picture. good to remember
        PhoneArrayAdapter adapter = new PhoneArrayAdapter(this, list);

        // set data to listView with adapter
        listview.setAdapter(adapter);

        // item listener
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get list row data (now String as a phone name)
                String phone = list.get(position);
                // create an explicit intent
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                // add data to intent
                intent.putExtra("phone",phone);
                // start a new activity
                startActivity(intent);
            }
        });

    }
}
