package com.lentcoding.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HomeActivity extends AppCompatActivity {
    ListView homeOptionsList;
    ArrayAdapter<String> listAdapter;
    String[] homeOptions = {"Manage Friends", "Manage Meet Ups", "Manage Places", "Search Places"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeOptionsList = (ListView) findViewById(R.id.lstHomeOptions);
        listAdapter = new ArrayAdapter<>(HomeActivity.this, R.layout.list, homeOptions);
        homeOptionsList.setAdapter(listAdapter);
        homeOptionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent i0 = new Intent(HomeActivity.this, FriendsActivity.class);
                        startActivity(i0);
                        break;
                    case 1:
                        Intent i1 = new Intent(HomeActivity.this, MeetUpsActivity.class);
                        startActivity(i1);
                        break;
                    case 2:
                        Intent i2 = new Intent(HomeActivity.this, PlacesActivity.class);
                        startActivity(i2);
                        break;
                    case 3:
                        Intent i3 = new Intent(HomeActivity.this, SearchPlacesActivity.class);
                        startActivity(i3);
                        break;

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_exit:
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("LOGOUT", true);
                startActivity(i);
                return true;
        }

        return false;
    }
}
