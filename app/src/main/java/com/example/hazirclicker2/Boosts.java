package com.example.hazirclicker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.database.sqlite.SQLiteDatabaseKt;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.w3c.dom.Text;

public class Boosts extends MainActivity{
    public int c = 0;
    BottomNavigationView bottomNavigationView;
    String[] chars = {"X2","Coffee","Stocks"};
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
        oink = findViewById(R.id.oink);
        hlp = new HelperDB(this);
        db = hlp.getWritableDatabase();
        initCoffeeText(db);
        initInvestText(db);
        initX2Text(db);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.potion);
        bottomNavigationView.getMenu().findItem(R.id.potion).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.house:
                        intent = new Intent(Boosts.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.wrench:
                        intent = new Intent(Boosts.this, Upgrades.class);
                        startActivity(intent);
                        return true;
                    case R.id.suit:
                       intent = new Intent(Boosts.this, Skins.class);
                        startActivity(intent);
                        return true;
                    case R.id.potion:
                        return true;
                    case R.id.wallpaper:
                        intent = new Intent(Boosts.this, Backgrounds.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
        db.close();
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
                Intent inte = new Intent(Boosts.this, help.class);
                startActivity(inte);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void Activate(View view) {
        SQLiteDatabase db = hlp.getWritableDatabase();
        if(isBoostActivated(db,chars[0])||isBoostActivated(db,chars[1])){
            Toast.makeText(Boosts.this,"Cant Activate More Than one Boost at A Time!",Toast.LENGTH_LONG).show();
        }
        else {
            //checks if boosts are bought, if not, the function is ended.
            switch (view.getId()){
                case R.id.ActivateX2:
                    if(getTimesBought(db,chars[0])==0){
                        Toast.makeText(Boosts.this,"No Owned Boosts! Activation Failed!",Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case R.id.ActivateCoffee:
                    if(getTimesBought(db,chars[1])==0){
                        Toast.makeText(Boosts.this,"No Owned Boosts! Activation Failed!",Toast.LENGTH_LONG).show();
                        return;
                    }
            }
            popup_window cdd = new popup_window(Boosts.this);
            switch (view.getId()) {
                case R.id.ActivateX2:
                    cdd.setBoostType(chars[0]);
                    break;
                case R.id.ActivateCoffee:
                    cdd.setBoostType(chars[1]);
                    break;
            }
            cdd.show();
            cdd.price.setText("Are You Sure You Want To Activate This Boost?");
            Log.d("HERE", "I launched prompt");
            cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    SQLiteDatabase db = hlp.getWritableDatabase();
                    if (cdd.yesorno.equals("YES")) {
                        //updates the upgrade number
                        switch (view.getId()) {
                            case R.id.ActivateX2:
                                initX2Text(db);
                                break;
                            case R.id.ActivateCoffee:
                                initCoffeeText(db);
                                break;
                        }
                    }
                }
            });
        }
    }
    private boolean isBoostActivated(SQLiteDatabase db, String boostType) {
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT isActivated FROM Boosts WHERE boostType= '"+boostType+"'", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        cursor.close();
        if(counter2.equals("YES"))
            return true;
        else
            return false;
    }
    public void Buy(View view){
       SQLiteDatabase db = hlp.getWritableDatabase();
       popup_window cdd = new popup_window(Boosts.this);
      switch (view.getId()){
          case R.id.BuyX2:
              cdd.setCost(getPrice(db,chars[0]));
              cdd.setBoostType(chars[0]);
              break;
          case R.id.BuyCoffee:
              cdd.setCost(getPrice(db,chars[1]));
              cdd.setBoostType(chars[1]);
              break;
          case R.id.getInvest:
              cdd.setCost(getPrice(db,chars[2]));
              cdd.setBoostType(chars[2]);
              break;
      }
        cdd.show();
        cdd.price.setText("This Boost Costs "+cdd.getCost()+ " Oinkers\nAre You Sure?");
        cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                SQLiteDatabase db = hlp.getWritableDatabase();
                if(cdd.yesorno.equals("YES")){
                    //updates the upgrade number
                    switch (view.getId()){
                        case R.id.BuyX2:
                            initX2Text(db);
                            break;
                        case R.id.BuyCoffee:
                            initCoffeeText(db);
                            break;
                        case R.id.getInvest:
                            initInvestText(db);
                            break;
                    }
                }
            }
        });
       db.close();
    }

    private void initInvestText(SQLiteDatabase db) {
        TextView textView = findViewById(R.id.StockBoosts);
        textView.setText("Stocks Owned: "+getTimesBought(db,chars[2]));
    }

    private void initCoffeeText(SQLiteDatabase db) {
        TextView textView = findViewById(R.id.coffeeBoosts);
        textView.setText("Boosts Owned: "+getTimesBought(db,chars[1]));
    }

    private void initX2Text(SQLiteDatabase db) {

        TextView textView = findViewById(R.id.X2Boosts);
        textView.setText("Boosts Owned: "+getTimesBought(db,chars[0]));
    }

    private String getPrice(SQLiteDatabase db,String boostType) {

        String string = "";
        Cursor cursor = db.rawQuery("SELECT price FROM Boosts WHERE boostType = '"+boostType+"'",(String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            string = cursor.getString(0);
        }
        cursor.close();
        return string;
    }
    private int getTimesBought(SQLiteDatabase db,String boostType) {
        String string = "";
        Cursor cursor = db.rawQuery("SELECT timesBought FROM Boosts WHERE boostType = '"+boostType+"'",(String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            string = cursor.getString(0);
        }
        cursor.close();
        if(string!="")
            return Integer.parseInt(string);
        else{
            return 0;
        }
    }
}