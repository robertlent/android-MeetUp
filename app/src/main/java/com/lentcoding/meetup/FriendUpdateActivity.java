package com.lentcoding.meetup;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class FriendUpdateActivity extends AppCompatActivity {
    String name, email, phone;
    EditText n, e, p;
    int id;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_update);

        try {
            String destPath = this.getDatabasePath(DBAdapter.DATABASE_NAME).getParentFile().getAbsolutePath();
            File f = new File(destPath);
            if (!f.exists()) {
                f.mkdirs();
                f.createNewFile();

                CopyDB(getBaseContext().getAssets().open("databases/meetupDB.sqlite"), new FileOutputStream(destPath + "/meetupDB.sqlite"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        n = (EditText) findViewById(R.id.txtName);
        e = (EditText) findViewById(R.id.txtEmail);
        p = (EditText) findViewById(R.id.txtPhone);

        p.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        db = new DBAdapter(this);
        try {
            db.open();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        Intent i = this.getIntent();
        id = i.getIntExtra("id", 1);

        Cursor c;
        try {
            c = db.getFriend(id);
            if (c.moveToFirst()) {
                do {
                    name = c.getString(1);
                    email = c.getString(2);
                    phone = c.getString(3);
                } while (c.moveToNext());
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        db.close();

        n.setText(name);
        e.setText(email);
        p.setText(phone);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_home:
                Intent i1 = new Intent(FriendUpdateActivity.this, HomeActivity.class);
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

    public void CopyDB(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    public void onClick(View view) throws SQLException {
        name = n.getText().toString();
        email = e.getText().toString();
        phone = p.getText().toString();
        Intent i = this.getIntent();
        boolean fromDetails = i.getBooleanExtra("fromDetails", false);
        db.open();

        if (db.updateFriend(id, name, email, phone)) {
            Toast.makeText(getBaseContext(), "Friend updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Update failed", Toast.LENGTH_LONG).show();
        }
        db.close();

        if (!fromDetails) {
            Intent i1 = new Intent("com.lentcoding.meetup.FriendsActivity");
            startActivity(i1);
        } else {
            Intent i2 = new Intent("com.lentcoding.meetup.FriendDetailActivity");
            i2.putExtra("id", id);
            startActivity(i2);
        }
    }
}
