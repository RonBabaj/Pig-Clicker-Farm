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

public class Upgrades extends MainActivity{
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
        setContentView(R.layout.activity_upgrades);
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
                        Intent intent = new Intent(Upgrades.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.wrench:
                        return true;
                    case R.id.suit:
                        Intent iny = new Intent(Upgrades.this, Skins.class);
                        startActivity(iny);
                        return true;
                    case R.id.potion:
                        Intent in = new Intent(Upgrades.this, Boosts.class);
                        startActivity(in);
                        return true;
                    case R.id.wallpaper:
                        Intent inte = new Intent(Upgrades.this, Backgrounds.class);
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
        db.execSQL("delete from Skins");
        db.execSQL("delete from oinkcounter");
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