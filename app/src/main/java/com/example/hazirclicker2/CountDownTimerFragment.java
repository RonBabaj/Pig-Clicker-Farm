package com.example.hazirclicker2;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CountDownTimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CountDownTimerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String[] chars = new String[]{"X2", "Coffee", "Stocks"};
    TextView counter;

    CountDownTimer countDownTimer;

    HelperDB hlp;
    SQLiteDatabase db;

    public CountDownTimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CountDownTimer.
     */
    // TODO: Rename and change types and number of parameters
    public static CountDownTimerFragment newInstance(String param1, String param2) {
        CountDownTimerFragment fragment = new CountDownTimerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_count_down_timer, container, false);
        hlp = new HelperDB(getContext());
        db = hlp.getWritableDatabase();
        counter = view.findViewById(R.id.timecounter);
        //checks if the ticker is equal to zero for Coffee or not and initializes the timer accordingly
        if(isBoostActivated(db,chars[1])) {
            switch (getTicker(db, chars[1])) {
                case 0:
                    initCountTimer(300000, chars[1]);
                    break;
                default:
                    initCountTimer(getTicker(db, chars[1]), chars[1]);
            }
        }
        if(isBoostActivated(db,chars[0])) {
            //checks if the ticker is equal to zero for X2 or not and initializes the timer accordingly
            switch (getTicker(db, chars[0])) {
                case 0:
                    initCountTimer(300000, chars[0]);
                    break;
                default:
                    initCountTimer(getTicker(db, chars[0]), chars[0]);
            }
        }
        return view;
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

    private void initCountTimer(int millisinfuture,String boostType) {
        countDownTimer = new CountDownTimer(millisinfuture, 1000) {

            public void onTick(long millisUntilFinished) {
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                setTickerTimeLeft(db,(int)millisUntilFinished,boostType);

                counter.setText(""+seconds+"");
            }

            public void onFinish() {
                db.execSQL("UPDATE Boosts SET isActivated = 'NO' WHERE boostType = '"+boostType+"'");
                setTickerTimeLeft(db,0,boostType);
            }
        }.start();
    }

    private void setTickerTimeLeft(SQLiteDatabase db, int millisUntilFinished,String boostType) {
        db.execSQL("UPDATE Boosts SET ticker = '"+millisUntilFinished+"' WHERE boostType = '"+boostType+"'");


    }

    public int getTicker(SQLiteDatabase db,String boostType){
        String counter2 = "";
        Cursor cursor = db.rawQuery("SELECT ticker FROM Boosts WHERE boostType = '"+boostType+"'", (String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            counter2 = cursor.getString(0);
        }
        cursor.close();
        return Integer.parseInt(counter2);
    }
    private int getBoostType(SQLiteDatabase db) {
        String string = "";
        Cursor cursor = db.rawQuery("SELECT boostType FROM Boosts WHERE isActivated = 'YES'",(String[]) null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            string = cursor.getString(0);
        }
        cursor.close();
        if(string!="")
            return Integer.parseInt(string);
        else
            return 0;
    }
}