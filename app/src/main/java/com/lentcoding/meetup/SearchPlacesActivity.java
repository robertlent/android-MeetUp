package com.lentcoding.meetup;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SearchPlacesActivity extends AppCompatActivity {
    private EditText searchKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_places);

        searchKeyword = (EditText) findViewById(R.id.search_keyword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_places, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_home:
                Intent i1 = new Intent(SearchPlacesActivity.this, HomeActivity.class);
                startActivity(i1);
                return true;
            case R.id.action_exit:
                Intent i2 = new Intent(Intent.ACTION_MAIN);
                i2.addCategory(Intent.CATEGORY_HOME);
                i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i2.putExtra("LOGOUT", true);
                startActivity(i2);
                return true;
        }
        return false;
    }

    public void onClick(View view) {
        try {
            Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
            String keyword = searchKeyword.getText().toString();
            i.putExtra(SearchManager.QUERY, keyword);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
