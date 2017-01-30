package com.lentcoding.meetup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class FriendDetailActivity extends AppCompatActivity {
    TextView n, e, p;
    String name, email, phone;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);

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

        n = (TextView) findViewById(R.id.txtName);
        e = (TextView) findViewById(R.id.txtEmail);
        p = (TextView) findViewById(R.id.txtPhone);

        db = new DBAdapter(this);
        try {
            db.open();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        Intent i = this.getIntent();
        int id = i.getIntExtra("id", 1);

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

        String labelName = getString(R.string.label_name, name),
                labelEmail = getString(R.string.label_email, email),
                labelPhone = getString(R.string.label_phone, phone);
        SpannableString spanName = new SpannableString(labelName),
                spanEmail = new SpannableString(labelEmail),
                spanPhone = new SpannableString(labelPhone);

        spanName.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelName.indexOf("\t\t"), 0);
        spanEmail.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelEmail.indexOf("\t\t"), 0);
        spanPhone.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelPhone.indexOf("\t\t"), 0);

        n.setText(spanName);
        e.setText(spanEmail);
        p.setText(spanPhone);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i = this.getIntent();
        final int friendId = i.getIntExtra("id", 1);

        switch (id) {
            case R.id.action_update:
                Intent i1 = new Intent(FriendDetailActivity.this, FriendUpdateActivity.class);
                i1.putExtra("id", friendId);
                i1.putExtra("fromDetails", true);
                startActivity(i1);
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(FriendDetailActivity.this).setTitle("Delete Friend?").setMessage("Are you sure you want to delete this friend?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface d, final int i) {
                                try {
                                    db.open();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                if (db.deleteFriend(friendId)) {
                                    Toast.makeText(FriendDetailActivity.this, "Friend has been deleted", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(FriendDetailActivity.this, "Delete failed", Toast.LENGTH_LONG).show();
                                }
                                db.close();
                                startActivity(new Intent(FriendDetailActivity.this, FriendsActivity.class));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                return true;
            case R.id.action_home:
                Intent i3 = new Intent(FriendDetailActivity.this, HomeActivity.class);
                startActivity(i3);
                return true;
            case R.id.action_exit:
                Intent i4 = new Intent(Intent.ACTION_MAIN);
                i4.addCategory(Intent.CATEGORY_HOME);
                i4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i4.putExtra("LOGOUT", true);
                startActivity(i4);
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
}
