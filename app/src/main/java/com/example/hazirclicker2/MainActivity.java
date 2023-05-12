package com.example.hazirclicker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;

import android.Manifest;
import android.animation.AnimatorSet;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    public int c = 0;
    AnimatorSet scaleDown = new AnimatorSet();
    AnimatorSet scaleUp = new AnimatorSet();
    AnimatorSet moveUp = new AnimatorSet();
    BottomNavigationView bottomNavigationView;
    private static final int ACCESSIBILITY_ENABLED = 1;
    String[] chars = {"Multiplier","Farmers","Bonus"};
    String skindex;
    Handler handler;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    String[] names= {"Male Pig", "Female Pig", "Muddy Pig", "Vampire Pig","Pig With Baby","Devil Pig","Punk Pig","Builder Pig","French Pig","Zombie Pig","Painter Pig","Rich Pig"};
    String counter;
    ContentValues cv = new ContentValues();
    BroadcastReceiver mReceiver;
    SQLiteDatabase db,db2;
    HelperDB hlp;
    Intent move;
    FragmentContainerView frag;

    private static final String CHANNEL_ID = "my_channel_01";
    private static final CharSequence CHANNEL_NAME = "My Channel";
    TextView oink;
    String oinknum;
    ImageButton pig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frag = findViewById(R.id.fragment_container_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("message in a bottle") != null) {
               frag.setVisibility(View.VISIBLE);
            }
        }
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
                       // onStop();
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
            /* dont forget to add all the initaiting functions to this section of the main activity */
            //initiates oinker count
            this.oink.setText("0 Oinkers");
            ContentValues cv = new ContentValues();
            cv.put("oinkers",0);
            db.insert("oinkcounter",null,cv);

            //initiates skins page
            ContentValues cv2=new ContentValues();
            int counter=1;
            for (int i=0;i < names.length;i++){
                cv2.put("skindex","pig"+counter);
                counter++;
                cv2.put("Name", names[i]);
                cv2.put("price",((i+1)*100)*i);
                //for default first female and male pig skin selections
                if(names[i].equals("Male Pig")){
                    cv2.put("isBought", "YES");
                    cv2.put("isEquipped", "YES");
                }
                else if(names[i].equals("Female Pig")){
                    cv2.put("isEquipped", "NO");
                    cv2.put("isBought", "YES");
                }
                else {
                    cv2.put("isEquipped", "NO");
                    cv2.put("isBought", "NO");
                }
                db.insert("Skins",null,cv2);
            }

            //initiates upgrades page
            for(int i=0;i<chars.length;i++){
                ContentValues cv3 = new ContentValues();
                cv3.put("upgrade",chars[i]);
                switch (chars[i]){
                    case "Multiplier":
                        cv3.put("price",100);
                        break;
                    case "Farmers":
                        cv3.put("price",200);
                        break;
                    case "Bonus":
                        cv3.put("price",300);
                        break;
                }
                cv3.put("timesUpgraded",0);
                db.insert("Upgrades",null,cv3);

            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BIND_ACCESSIBILITY_SERVICE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, so request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BIND_ACCESSIBILITY_SERVICE},
                        PERMISSION_REQUEST_CODE);
            }
            //initiates daily reward-alarm manager's repeating alarm
            initdailyreward();
        }
        else {
            Cursor cursor2 = this. db.rawQuery("SELECT oinkers FROM oinkcounter", (String[]) null);
            cursor2.moveToFirst();
            String smth = "";
            for (int i = 0; i < cursor2.getCount(); i++) {
                smth = cursor2.getString(0);
            }
            cursor2.close();
            this.c = Integer.parseInt(smth);
            this.oink.setText("" + this.c + " Oinkers");
        }
        //checks if handler has already been initialized
        if(rowsCountHandlers(db)==0){
                //initiates daily reward and farmers events in the case where the handler has stopped
                Log.d("daily reward","daily reward initiated successfully");
                //starts the recursive auto-clicking process
                initFarmers();
                Log.d("farmers","farmers initiated successfully");
                setHandlers(db,1);
        }
        this. db.close();
    }

    private void setHandlers(SQLiteDatabase db, int i) {

       ContentValues cv = new ContentValues();
       cv.put("handlerTimes",i);
       db.insert("Handlers",null,cv);

    }

    private void initFarmers() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HelperDB hlp = new HelperDB(getApplicationContext());
                SQLiteDatabase db = hlp.getWritableDatabase();
                int timesUpgraded = getTimesUpgraded(db,"Farmers");
                for(int i=0;i<timesUpgraded;i++){
                    pig.performClick();
                    try{
                    oink.setText(getOinkerCount(db));
                    Log.d("farmers2","clicked!");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                initFarmers();
            }
        }, 10000);
    }

    private int getOinkerCount(SQLiteDatabase db) {

        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT oinkers FROM oinkcounter", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        cursor.close();
        return Integer.parseInt(counter2);
    }

    private void initdailyreward() {
        Log.d("alarm started","alarm started");
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        Intent myIntent = new Intent(getApplicationContext(), DailyReceiver.class);
        int ALARM1_ID = 100;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Log.d("alarm set","alarm set");
    }

    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("AU", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == ACCESSIBILITY_ENABLED) {
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    public static void initnotif(Context context, String title, String message) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Claim Your Daily Reward");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("message in a bottle","message in a bottle");
        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.haziricon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent).setAutoCancel(true);

        Notification notification = builder.build();
        notificationManager.notify(100, notification);
    }
    /*private void initiateAlarms() {

        //initiates daily reward alarm
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MINUTE,00);
        calendar.set(Calendar.SECOND,00);
        Intent intent = new Intent(getApplicationContext(), DailyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,86400000,AlarmManager.INTERVAL_DAY,pendingIntent);
        Log.d("alarm registered","alarm has been registered successfully");

        //Initiates farmers boost auto-click alarm
        Intent intent2 = new Intent(getApplicationContext(), AutoClickReceiver.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getApplicationContext(),100,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager2.setRepeating(AlarmManager.RTC,10000,10000,pendingIntent2);
        Log.d("auto-click registered","auto-click alarm has been registered successfully");

    }*/

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
            this.oink.setText("" + this.c + " Oinkers");
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
            this.c = Integer.parseInt(smth) + (getTimesUpgraded(db,"Multiplier")+1);
            this.oink.setText("" + this.c + " Oinkers");
            this. db.execSQL("UPDATE oinkcounter SET oinkers = " + this.c);
        }
        this. db.close();
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

    public String rowsCount(SQLiteDatabase db) {
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT COUNT(*)oinkers FROM oinkcounter", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        return counter2;
    }
    public int rowsCountHandlers(SQLiteDatabase db) {
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT COUNT(*)handlerTimes FROM Handlers", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        return Integer.parseInt(counter2);
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
    private String rowsCountUpgrades(SQLiteDatabase db) {
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT COUNT(*)upgrade FROM Upgrades", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        return counter2;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        HelperDB hlp = new HelperDB(getApplicationContext());
        SQLiteDatabase db1 = hlp.getWritableDatabase();
        db1.execSQL("DELETE FROM Handlers");
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        HelperDB hlp = new HelperDB(getApplicationContext());
        SQLiteDatabase db1 = hlp.getWritableDatabase();
        db1.execSQL("DELETE FROM Handlers");

    }
}