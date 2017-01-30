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

public class MeetUpDetailActivity extends AppCompatActivity {
    String name, desc, friends, place, date, time;
    TextView n, d, f, p, dt, t;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_up_detail);

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
        d = (TextView) findViewById(R.id.txtDesc);
        f = (TextView) findViewById(R.id.txtFriends);
        p = (TextView) findViewById(R.id.txtPlace);
        dt = (TextView) findViewById(R.id.txtDate);
        t = (TextView) findViewById(R.id.txtTime);

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
            c = db.getMeetUp(id);
            if (c.moveToFirst()) {
                do {
                    name = c.getString(1);
                    desc = c.getString(2);
                    friends = c.getString(3);
                    place = c.getString(4);
                    date = c.getString(5);
                    time = c.getString(6);
                } while (c.moveToNext());
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        db.close();

        String labelName = getString(R.string.label_name, name),
                labelDesc = getString(R.string.label_description, desc),
                labelFriends = getString(R.string.label_friend, friends),
                labelPlace = getString(R.string.label_place, place),
                labelDate = getString(R.string.label_date, date),
                labelTime = getString(R.string.label_time, time);
        SpannableString spanName = new SpannableString(labelName),
                spanDesc = new SpannableString(labelDesc),
                spanFriends = new SpannableString(labelFriends),
                spanPlace = new SpannableString(labelPlace),
                spanDate = new SpannableString(labelDate),
                spanTime = new SpannableString(labelTime);

        spanName.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelName.indexOf("\t\t"), 0);
        spanDesc.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelDesc.indexOf("\t\t"), 0);
        spanFriends.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelFriends.indexOf("\t\t"), 0);
        spanPlace.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelPlace.indexOf("\t\t"), 0);
        spanDate.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelDate.indexOf("\t\t"), 0);
        spanTime.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelTime.indexOf("\t\t"), 0);

        n.setText(spanName);
        d.setText(spanDesc);
        f.setText(spanFriends);
        p.setText(spanPlace);
        dt.setText(spanDate);
        t.setText(spanTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meet_up_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i = this.getIntent();
        final int meetUpId = i.getIntExtra("id", 1);

        switch (id) {
            case R.id.action_update:
                Intent i1 = new Intent(MeetUpDetailActivity.this, MeetUpUpdateActivity.class);
                i1.putExtra("id", meetUpId);
                i1.putExtra("fromDetails", true);
                startActivity(i1);
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(MeetUpDetailActivity.this).setTitle("Delete Meet Up?").setMessage("Are you sure you want to delete this Meet Up?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface d, final int i) {
                                try {
                                    db.open();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                if (db.deleteMeetUp(meetUpId)) {
                                    Toast.makeText(MeetUpDetailActivity.this, "Meet Up has been deleted", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MeetUpDetailActivity.this, "Delete failed", Toast.LENGTH_LONG).show();
                                }
                                db.close();
                                startActivity(new Intent(MeetUpDetailActivity.this, MeetUpsActivity.class));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                return true;
            case R.id.action_home:
                Intent i3 = new Intent(MeetUpDetailActivity.this, HomeActivity.class);
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
