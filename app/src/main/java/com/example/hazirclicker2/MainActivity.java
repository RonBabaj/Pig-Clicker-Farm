package com.example.hazirclicker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    public int c = 0;
    AnimatorSet scaleDown = new AnimatorSet();
    AnimatorSet scaleUp = new AnimatorSet();
    AnimatorSet moveUp = new AnimatorSet();
    BottomNavigationView bottomNavigationView;
    String[] chars;
    String skindex;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    String counter;
    ContentValues cv = new ContentValues();
    BroadcastReceiver mReceiver;
    SQLiteDatabase db,db2;
    HelperDB hlp;
    Intent move;
    TextView oink;
    String oinknum;
    ImageButton pig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pig=findViewById(R.id.hazir);
        oink=findViewById(R.id.oink);
        hlp=new HelperDB(this);
        db = hlp.getWritableDatabase();
        skindex=getSkindex(db);
        setImageResource(skindex);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.house);
        bottomNavigationView.getMenu().findItem(R.id.house).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.house:
                        return true;
                    case R.id.wrench:
                        Intent intent = new Intent(MainActivity.this, Upgrades.class);
                        startActivity(intent);
                        return true;
                    case R.id.suit:
                        Intent i = new Intent(MainActivity.this, Skins.class);
                        startActivity(i);
                        return true;
                    case R.id.potion:
                        Intent in = new Intent(MainActivity.this, Boosts.class);
                        startActivity(in);
                        return true;
                    case R.id.wallpaper:
                        Intent inte = new Intent(MainActivity.this, Backgrounds.class);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                return true;
            case R.id.help:
                Intent inte = new Intent(MainActivity.this, help.class);
                startActivity(inte);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void setImageResource(String skindex){
        switch (skindex){
            case "pig1":
                pig.setImageResource(R.drawable.hazir);
                break;
            case "pig2":
                pig.setImageResource(R.drawable.hazira);
                break;
            case "pig3":
                pig.setImageResource(R.drawable.muddyhazir);
                break;
            case "pig4":
                pig.setImageResource(R.drawable.hazirpire);
                break;
            case "pig5":
                pig.setImageResource(R.drawable.babyzir);
                break;
            case "pig6":
                pig.setImageResource(R.drawable.hazir_devil);
                break;
            case "pig7":
                pig.setImageResource(R.drawable.edgyhazir);
                break;
            case "pig8":
                pig.setImageResource(R.drawable.hazir_builder);
                break;
            case "pig9":
                pig.setImageResource(R.drawable.french_hazir);
                break;
            case "pig10":
                pig.setImageResource(R.drawable.zombie_hazir);
                break;
            case "pig11":
                pig.setImageResource(R.drawable.hazirpainter);
                break;
            case "pig12":
                pig.setImageResource(R.drawable.formalhazir);
                break;
            default:
                pig.setImageResource(R.drawable.hazir);
        }

    }
    public String getSkindex(SQLiteDatabase db){
            String skindex = "";
            Cursor cursor = db.rawQuery("SELECT skindex FROM Skins WHERE isEquipped = 'YES'",(String[]) null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                skindex = cursor.getString(0);
            }
            cursor.close();
           return skindex;


    }
}