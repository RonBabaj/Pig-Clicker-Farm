package com.example.hazirclicker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Skins extends AppCompatActivity{
    public int c = 0;
    BottomNavigationView bottomNavigationView;
    String[] chars;
    String counter;
    String viewIDName;
    ContentValues cv = new ContentValues();
    SQLiteDatabase db;
    HelperDB hlp;
    Intent move;
    TextView oink;
    String[] names= {"Male Pig", "Female Pig", "Muddy Pig", "Vampire Pig","Pig With Baby","Devil Pig","Punk Pig","Builder Pig","French Pig","Zombie Pig","Painter Pig","Rich Pig"};
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skins);
        HelperDB hlp = new HelperDB(this);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.suit);
        bottomNavigationView.getMenu().findItem(R.id.suit).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.house:
                        Intent intent = new Intent(Skins.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.wrench:
                        Intent intn= new Intent(Skins.this, Upgrades.class);
                        startActivity(intn);
                        return true;
                    case R.id.suit:
                        return true;
                    case R.id.potion:
                        Intent in = new Intent(Skins.this, Boosts.class);
                        startActivity(in);
                        return true;
                    case R.id.wallpaper:
                        Intent inte = new Intent(Skins.this, Backgrounds.class);
                        startActivity(inte);
                        return true;
                }
                return false;
            }
        });
        String rowsCount = rowsCount(hlp.getWritableDatabase());
        db = hlp.getWritableDatabase();
        if (rowsCount.equals("0")) {
            ContentValues cv=new ContentValues();
            int counter=1;
            for (int i=0;i < names.length;i++){
                cv.put("skindex","pig"+counter);
                counter++;
                cv.put("Name", names[i]);
                cv.put("price",((i+1)*100)*i);
                cv.put("isEquipped", "NO");
                cv.put("isBought", "NO");
                db.insert("Skins",null,cv);
            }
            db.close();
        } else {
        this. db.close();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public void oink(View view) {
        hlp = new HelperDB(this);
        db = hlp.getWritableDatabase();
        int c = 0;
        int givenpigint= view.getId();
        viewIDName = getResources().getResourceEntryName(givenpigint);
        if(isBought(db)){
            popup_window cdd2 = new popup_window(Skins.this);
            cdd2.setSkindex(viewIDName);
            cdd2.show();
            cdd2.price.setText("Item Owned, Would You like To Equip?");
            db.close();
        }
        else {
            popup_window cdd = new popup_window(Skins.this);
            priceSetter(db, cdd);
            cdd.show();
            cdd.price.setText("This Item Costs " + cdd.cost + " Oinkers\nAre You Sure?");
            cdd.setSkindex(viewIDName);
            cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(final DialogInterface arg0) {
                    if (cdd.yesorno.equals("YES")) {
                        cdd.setSkindex(viewIDName);
                        int leftovercurrency = cdd.getLeftoverCurrency();
                        if (leftovercurrency >= 0) {
                            popup_window cdd2 = new popup_window(Skins.this);
                            cdd2.setSkindex(viewIDName);
                            cdd2.show();
                            cdd2.price.setText("Item Owned, Would You like To Equip?");
                            db.close();
                        }
                    }
                }
            });
        }
    }
    public void priceSetter(SQLiteDatabase db,popup_window cdd){
        String Price = "";
        Cursor cursor = db.rawQuery("SELECT price FROM Skins WHERE skindex = '"+viewIDName+"' ",(String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Price = cursor.getString(0);
        }
        cursor.close();
        cdd.setCost(Price);

    }
    public boolean isBought(SQLiteDatabase db){
        String counter = "";
        Cursor cursor = db.rawQuery("SELECT isBought FROM Skins WHERE Skindex = '"+viewIDName+"'",(String[]) null);
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++){
            counter = cursor.getString(0);
        }
        cursor.close();
        if(counter.equals("YES")){
            return true;
        }
        else{
            return false;
        }
    }
    public String rowsCount(SQLiteDatabase db) {
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT COUNT(*)Name FROM Skins", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        cursor.close();
        return counter2;
    }

}