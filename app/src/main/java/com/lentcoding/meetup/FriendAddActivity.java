package com.lentcoding.meetup;

import android.content.Intent;
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

public class FriendAddActivity extends AppCompatActivity {

    String name, email, phone;
    EditText n, e, p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_home:
                Intent i1 = new Intent(FriendAddActivity.this, HomeActivity.class);
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

    public void onClick(View view) {
        DBAdapter db = new DBAdapter(this);

        if (n.getText().length() > 0) {
            name = n.getText().toString();
            email = e.getText().toString();
            phone = p.getText().toString();

            try {
                db.open();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            db.insertFriend(name, email, phone);
            db.close();

            Toast.makeText(getBaseContext(), "Friend added", Toast.LENGTH_SHORT).show();
            Intent i = new Intent("com.lentcoding.meetup.FriendsActivity");
            startActivity(i);
        } else {
            Intent i = new Intent("com.lentcoding.meetup.FriendsActivity");
            startActivity(i);
        }
    }

}
