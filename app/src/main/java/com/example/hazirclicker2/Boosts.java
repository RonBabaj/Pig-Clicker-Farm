package com.example.hazirclicker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Boosts extends MainActivity{
    public int c = 0;
    BottomNavigationView bottomNavigationView;
    String[] chars;
    String counter;
    ContentValues cv = new ContentValues();
    SQLiteDatabase db;
    HelperDB hlp;
    Intent move;
    TextView oink;
    String oinknum;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boosts);
        oink=findViewById(R.id.oink);
        hlp=new HelperDB(this);
        db = hlp.getWritableDatabase();
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.house);
        bottomNavigationView.getMenu().findItem(R.id.house).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.house:
                        Intent intent = new Intent(Boosts.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.wrench:
                        Intent intn= new Intent(Boosts.this, Upgrades.class);
                        startActivity(intn);
                        return true;
                    case R.id.suit:
                        Intent in = new Intent(Boosts.this, Skins.class);
                        startActivity(in);
                        return true;
                    case R.id.potion:
                        return true;
                    case R.id.wallpaper:
                        Intent inte = new Intent(Boosts.this, Backgrounds.class);
                        startActivity(inte);
                        return true;
                }
                return false;
            }
        });
        String rowsCount = rowsCount(hlp.getWritableDatabase());
        this.counter = rowsCount;
        if (rowsCount.equals("0")) {
            this.oink.setText("0 oinkers");
        } else {
            Cursor cursor2 = this. db.rawQuery("SELECT oinkers FROM oinkcounter", (String[]) null);
            cursor2.moveToFirst();
            String smth = "";
            for (int i = 0; i < cursor2.getCount(); i++) {
                smth = cursor2.getString(0);
            }
            cursor2.close();
            this.c = Integer.parseInt(smth);
            this.oink.setText("" + this.c + " oinkers");
        }
        this. db.close();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public void oink(View view) {
        SQLiteDatabase writableDatabase = this.hlp.getWritableDatabase();
        this. db = writableDatabase;
        String rowsCount = rowsCount(writableDatabase);
        this.counter = rowsCount;
        if (rowsCount.equals("0")) {
            this.c++;
            this.oink.setText("" + this.c + " oinkers");
            this.cv.put(HelperDB.OINKERS, Integer.valueOf(this.c));
            this. db.insert(HelperDB.OINKER_TABLE, (String) null, this.cv);
        } else {
            Cursor cursor2 = this. db.query(HelperDB.OINKER_TABLE, (String[]) null, (String) null, (String[]) null, (String) null, (String) null, (String) null);
            cursor2.moveToFirst();
            String smth = "";
            for (int i = 0; i < cursor2.getCount(); i++) {
                smth = cursor2.getString(0);
            }
            cursor2.close();
            this.c = Integer.parseInt(smth) + 1;
            this.oink.setText("" + this.c + " oinkers");
            this. db.execSQL("UPDATE oinkcounter SET oinkers = " + this.c);
        }
        this. db.close();
    }
    public String rowsCount(SQLiteDatabase db) {
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT COUNT(*)oinkers FROM oinkcounter", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        return counter2;
    }
}