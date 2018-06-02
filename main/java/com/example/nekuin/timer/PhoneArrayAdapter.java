package com.example.nekuin.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.nekuin.timer.R;

public class PhoneArrayAdapter extends ArrayAdapter<String> {

    // application context
    private Context context;

    // phone data (names)
    private ArrayList<String> phones;

    // get application context and phones data to adapter
    public PhoneArrayAdapter(Context context, ArrayList<String> phones) {
        super(context, R.layout.rowlayout, R.id.textView, phones);
        this.context = context;
        this.phones = phones;
    }

    // populate every row in ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get row
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);


        // show phone name
        TextView textView = (TextView) rowView.findViewById(R.id.textView);
        textView.setText(phones.get(position));


        // show phone icon/image. Every image should be in drawable folder so it can be found there with R.drawable
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        switch (phones.get(position)) {
            case "Lääkintämiehet": imageView.setImageResource(R.drawable.hands); break;
            case "PST": imageView.setImageResource(R.drawable.balls); break;
            case "Rivimiehet": imageView.setImageResource(R.drawable.mask); break;
            case "Päällystö": imageView.setImageResource(R.drawable.map); break;
            case "Tuomarit": imageView.setImageResource(R.drawable.guns); break;
            case "Kielletty pelaaminen": imageView.setImageResource(R.drawable.vest); break;
            case "Huivi": imageView.setImageResource(R.drawable.hands); break;
            case "Osuma": imageView.setImageResource(R.drawable.balls); break;
            case "Liput": imageView.setImageResource(R.drawable.mask); break;
        }
        // return row view
        return rowView;
    }

}
