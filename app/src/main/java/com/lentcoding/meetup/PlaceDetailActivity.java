package com.lentcoding.meetup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Html;
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

public class PlaceDetailActivity extends AppCompatActivity {

    TextView n, a, p, pt;
    String name, address, phone, placeType;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

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
        a = (TextView) findViewById(R.id.txtAddress);
        p = (TextView) findViewById(R.id.txtPhone);
        pt = (TextView) findViewById(R.id.txtPlaceType);

        p.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

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
            c = db.getPlace(id);
            if (c.moveToFirst()) {
                do {
                    name = c.getString(1);
                    address = c.getString(2);
                    phone = c.getString(3);
                    placeType = c.getString(4);
                } while (c.moveToNext());
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        db.close();

        String labelName = getString(R.string.label_name, name),
                labelAddress = getString(R.string.label_address, address),
                labelPhone = getString(R.string.label_phone, phone),
                labelPlaceType = getString(R.string.label_place_type, placeType);
        SpannableString spanName = new SpannableString(labelName),
                spanAddress = new SpannableString(labelAddress),
                spanPhone = new SpannableString(labelPhone),
                spanPlaceType = new SpannableString(labelPlaceType);

        spanName.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelName.indexOf("\t\t"), 0);
        spanAddress.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelAddress.indexOf("\t\t"), 0);
        spanPhone.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelPhone.indexOf("\t\t"), 0);
        spanPlaceType.setSpan(new ForegroundColorSpan(Color.BLACK), 0, labelPlaceType.indexOf("\t\t"), 0);

        n.setText(spanName);
        a.setText(spanAddress);
        p.setText(spanPhone);
        pt.setText(spanPlaceType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_place_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i = this.getIntent();
        final int placeId = i.getIntExtra("id", 1);

        switch (id) {
            case R.id.action_update:
                Intent i1 = new Intent(PlaceDetailActivity.this, PlaceUpdateActivity.class);
                i1.putExtra("id", placeId);
                i1.putExtra("fromDetails", true);
                startActivity(i1);
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(PlaceDetailActivity.this).setTitle("Delete Place?").setMessage("Are you sure you want to delete this Place?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface d, final int i) {
                                try {
                                    db.open();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                if (db.deletePlace(placeId)) {
                                    Toast.makeText(PlaceDetailActivity.this, "Place has been deleted", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(PlaceDetailActivity.this, "Delete failed", Toast.LENGTH_LONG).show();
                                }
                                db.close();
                                startActivity(new Intent(PlaceDetailActivity.this, PlacesActivity.class));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                return true;
            case R.id.action_home:
                Intent i3 = new Intent(PlaceDetailActivity.this, HomeActivity.class);
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
