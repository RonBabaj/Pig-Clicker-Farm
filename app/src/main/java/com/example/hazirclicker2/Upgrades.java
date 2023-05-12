package com.example.hazirclicker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Upgrades extends AppCompatActivity{
    public int c = 0;
    BottomNavigationView bottomNavigationView;
    String[] chars = {"Multiplier","Farmers","Bonus"};
    String counter;
    ContentValues cv = new ContentValues();
    SQLiteDatabase db;
    HelperDB hlp;
    Intent move;
    CountDownTimer countDownTimer;
    TextView bonusText,farmerText,multiText,piggytext ;
    EditText piggyedit;
    String oinknum;
    int minutes,insertedvalue;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrades);
        piggyedit = findViewById(R.id.piggyedit);
        piggytext = findViewById(R.id.piggytimer);
        bonusText = findViewById(R.id.bonustext);
        farmerText = findViewById(R.id.farmertext);
        multiText = findViewById(R.id.multitext);
        hlp=new HelperDB(this);
        db = hlp.getWritableDatabase();
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.wrench);
        bottomNavigationView.getMenu().findItem(R.id.wrench).setChecked(true);
        //sets intents for different activities when clicked
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.house:
                        intent = new Intent(Upgrades.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.wrench:
                        return true;
                    case R.id.suit:
                        intent = new Intent(Upgrades.this, Skins.class);
                        startActivity(intent);
                        return true;
                    case R.id.potion:
                        intent = new Intent(Upgrades.this, Boosts.class);
                        startActivity(intent);
                        return true;
                    case R.id.wallpaper:
                       intent = new Intent(Upgrades.this, Backgrounds.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
        //initialises database on creation of activity
        hlp = new HelperDB(this);
        db=hlp.getWritableDatabase();
        if(rowsCount(db).equals("0")){
           for(int i=0;i<chars.length;i++){
               ContentValues cv = new ContentValues();
               cv.put("upgrade",chars[i]);
               switch (chars[i]){
                   case "Multiplier":
                       cv.put("price",100);
                       break;
                   case "Farmers":
                       cv.put("price",200);
                       break;
                   case "Bonus":
                       cv.put("price",300);
                       break;
               }
               cv.put("timesUpgraded",0);
               db.insert("Upgrades",null,cv);
           }
        }
        else{
            //updates the TextViews
            initbonus();
            initfarmer();
            initmulti();
        }
        //
        if(!(rowsCountBank(db).equals("0"))) {
            insertedvalue = getInsertedValue();
            int timeleft = timeLeft()+1;
            piggytext.setVisibility(View.VISIBLE);
            findViewById(R.id.bankbutton).setVisibility(View.GONE);
            piggyedit.setVisibility(View.GONE);
            countDownTimer = new CountDownTimer(timeleft*60000, 1000) {

                public void onTick(long millisUntilFinished) {
                    minutes = (int) millisUntilFinished / 60000;
                    piggytext.setText("time left to earn interest: \n" + minutes + " minutes");
                    setTimeLeft(minutes);
                    setInsertedValue(insertedvalue);

                }

                public void onFinish() {
                    onEarnedInterest(insertedvalue);
                    piggytext.setVisibility(View.GONE);
                    findViewById(R.id.bankbutton).setVisibility(View.VISIBLE);
                    piggyedit.setVisibility(View.VISIBLE);
                    db.execSQL("DELETE FROM PiggyBank");
                }
            }.start();

        }
    }
    //inflates appbar menu(top menu)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public void oink(View view) {

    }
    //checks if the table is empty by counting the number of rows in the db
    public String rowsCount(SQLiteDatabase db) {
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT COUNT(*)upgrade FROM Upgrades", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        return counter2;
    }
    //fires when any of the "upgrade" buttons are clicked
    public void Upgrade(View view) {
        db = hlp.getWritableDatabase();
        //initiates popup window and continues if conformation from popupwindow is positive
        popup_window cdd = new popup_window(Upgrades.this);
        switch (view.getId()){
            case R.id.multibutton:
                cdd.setCost(getPrice(db,chars[0]));
                cdd.setUpgradeType(chars[0]);
                break;
            case R.id.farmerbutton:
                cdd.setCost(getPrice(db,chars[1]));
                cdd.setUpgradeType(chars[1]);
                break;
            case R.id.bonusbutton:
                cdd.setCost(getPrice(db,chars[2]));
                cdd.setUpgradeType(chars[2]);
                break;
        }
        cdd.show();
        cdd.price.setText("This Upgrade Costs " + cdd.getCost() + " Oinkers\nAre You Sure?");
        cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(cdd.yesorno.equals("YES")){
                    //updates the upgrade number
                    switch (view.getId()){
                        case R.id.multibutton:
                            initmulti();
                            break;
                        case R.id.farmerbutton:
                            initfarmer();
                            break;
                        case R.id.bonusbutton:
                            initbonus();
                            break;
                    }
                }
            }
        });

    }
    //updates the Interest-gathering process of the piggy bank
    public void initBank(View view) {
        //if table does have an empty set
        if(rowsCountBank(db).equals("0")) {

            //initiates table
            ContentValues contentValues = new ContentValues();
            contentValues.put("oinkers", "0");
            contentValues.put("ticker", "0");
            db.insert("PiggyBank", null, contentValues);

            String insertedstring = piggyedit.getText().toString();
            if (insertedstring != null) {
                insertedvalue = Integer.parseInt(insertedstring);
                if (insertedvalue > 0 && insertedvalue <= getOinkers()) {
                    setOinkers(getOinkers() - (insertedvalue));
                    piggytext.setVisibility(View.VISIBLE);
                    findViewById(R.id.bankbutton).setVisibility(View.GONE);
                    piggyedit.setVisibility(View.GONE);
                    countDownTimer = new CountDownTimer(300000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            minutes = (int) millisUntilFinished / 60000;
                            setTimeLeft(minutes);
                            setInsertedValue(insertedvalue);
                            piggytext.setText("time left to earn interest: \n" + minutes + " minutes");
                        }

                        public void onFinish() {
                            onEarnedInterest(insertedvalue);
                            piggytext.setVisibility(View.GONE);
                            findViewById(R.id.bankbutton).setVisibility(View.VISIBLE);
                            piggyedit.setVisibility(View.VISIBLE);
                            db.execSQL("DELETE FROM PiggyBank");
                        }
                    }.start();
                } else {
                    if (insertedvalue > getOinkers()) {
                        Toast.makeText(this, "entered value can't be more than current account balance!!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "entered value can't be 0 or less!", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(this, "entered value has to be a valid number!", Toast.LENGTH_LONG).show();
            }

        }

    }

    private int getInsertedValue() {
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT oinkers FROM PiggyBank", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        cursor.close();
        return Integer.parseInt(counter2);
    }

    private void setTimeLeft(int minutes) {
        db.execSQL("UPDATE PiggyBank SET ticker = " + minutes);
    }
    public void setInsertedValue(int insertedValue){
        db.execSQL("UPDATE PiggyBank SET oinkers = " + insertedValue);
    }

    private String rowsCountBank(SQLiteDatabase db) {
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT COUNT(*)oinkers FROM PiggyBank", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        return counter2;
    }

    //triggers when the countdown timer has finished and notifies the user of the earned interest as well as updates it in the db
    private void onEarnedInterest(int enteredValue) {
        int interest = enteredValue *2;
        Toast.makeText(this, "Interest Earned Successfully!\nYou Gained Back "+interest+" Oinkers!",Toast.LENGTH_LONG).show();
        int total = interest+getOinkers();
        setOinkers(total);

    }

    private void setOinkers(int total) {

        db.execSQL("UPDATE oinkcounter SET oinkers = " + total);
    }

    private int getOinkers() {
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT oinkers FROM oinkcounter", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        cursor.close();
        if(counter2!=null)
        return Integer.parseInt(counter2);
        else{
            return 0;
        }
    }

    //updates the daily reward bonuses upgrades
    private void initbonus() {
        bonusText.setText("Bonus: "+getTimesUpgraded(db,chars[2]));
    }

    //updates the farmers upgrade process
    private void initfarmer() {
        farmerText.setText("Farmers: "+getTimesUpgraded(db,chars[1]));
    }
    //updates the click multiplier upgrade process
    private void initmulti() {
        multiText.setText("Multiplier: "+getTimesUpgraded(db,chars[0]));
    }
    private String getPrice(SQLiteDatabase db,String UpgradeType){
        String counter="";
        Cursor cursor = db.rawQuery("SELECT price FROM Upgrades WHERE upgrade = '"+UpgradeType+"'",(String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter = cursor.getString(0);
        }
        cursor.close();
        return counter;
    }
    public int timeLeft(){
        String counter="";
        Cursor cursor = db.rawQuery("SELECT ticker FROM PiggyBank",(String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter = cursor.getString(0);
        }
        cursor.close();
        return Integer.parseInt(counter);
    }
    public int getTimesUpgraded(SQLiteDatabase db,String UpgradeType){
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT timesUpgraded FROM Upgrades WHERE upgrade= '"+UpgradeType+"'", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        cursor.close();
        return Integer.parseInt(counter2);
    }

    protected void onDestroy() {
        super.onDestroy();
        setTimeLeft(minutes);
        setInsertedValue(insertedvalue);
    }
}