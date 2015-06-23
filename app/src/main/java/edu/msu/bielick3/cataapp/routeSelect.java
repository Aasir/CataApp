package edu.msu.bielick3.cataapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class routeSelect extends Activity {
    private String route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_select);

        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setNumColumns(4);
        String[] items = getResources().getStringArray(R.array.routes_array);
        gridview.setAdapter(new ButtonAdapter(this, items));
        gridview.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                route = (String)((TextView) v).getText();
            }
        });

    }

    public class ButtonAdapter extends BaseAdapter {
        private Context context;
        private final String[] textViewValues;

        public ButtonAdapter(Context context, String[] textViewValues) {
            this.context = context;
            this.textViewValues = textViewValues;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View gridView;

            if (convertView == null) {

                gridView = new View(context);

                gridView = inflater.inflate(R.layout.item, null);

                TextView textView = (TextView) gridView
                        .findViewById(R.id.grid_item_label);
                textView.setText(textViewValues[position]);
            } else {
                gridView = (View) convertView;
            }

            return gridView;
        }

        @Override
        public int getCount() {
            return textViewValues.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

    public void cancelSelect(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void returnRoute(View view) {
        Intent intent = new Intent();
        intent.putExtra("route", route);
        setResult(RESULT_OK,intent);
        finish();
    }


}
