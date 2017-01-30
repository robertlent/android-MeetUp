package com.lentcoding.meetup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Calendar;

public class MeetUpAddActivity extends AppCompatActivity {

    String name, desc, friends, place, date, time;
    EditText n, d, f, p, dt, t;
    Calendar currentDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_up_add);

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
        d = (EditText) findViewById(R.id.txtDesc);
        f = (EditText) findViewById(R.id.txtFriends);
        p = (EditText) findViewById(R.id.txtPlace);
        dt = (EditText) findViewById(R.id.txtDate);
        t = (EditText) findViewById(R.id.txtTime);

        dt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = currentDateTime.get(Calendar.YEAR);
                int month = currentDateTime.get(Calendar.MONTH);
                int day = currentDateTime.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(MeetUpAddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dt.setText(((month + 1) < 10 ? "0" + (month + 1) : month) + "/" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/" + year);
                    }
                }, year, month, day);
                datePicker.setTitle("Select Date");
                datePicker.show();
            }
        });

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int hour = currentDateTime.get(Calendar.HOUR);
                final int min = currentDateTime.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(MeetUpAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        t.setText((hourOfDay > 12 ? hourOfDay - 12 : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute) + " " + (hourOfDay < 13 ? "AM" : "PM"));
                    }
                }, hour, min, false);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_meet_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_home:
                Intent i1 = new Intent(MeetUpAddActivity.this, HomeActivity.class);
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
        DBAdapter db = new DBAdapter(this);

        if (n.getText().length() > 0) {
            name = n.getText().toString();
            desc = d.getText().toString();
            friends = f.getText().toString();
            place = p.getText().toString();
            date = dt.getText().toString();
            time = t.getText().toString();

            db.open();
            db.insertMeetUp(name, desc, friends, place, date, time);
            db.close();

            Toast.makeText(getBaseContext(), "Meet Up added", Toast.LENGTH_SHORT).show();
            Intent i = new Intent("com.lentcoding.meetup.MeetUpsActivity");
            startActivity(i);
        } else {
            Intent i = new Intent("com.lentcoding.meetup.MeetUpsActivity");
            startActivity(i);
        }
    }
}
