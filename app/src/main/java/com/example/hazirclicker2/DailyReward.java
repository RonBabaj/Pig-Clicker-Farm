package com.example.hazirclicker2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import pl.droidsonroids.gif.GifImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyReward#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyReward extends Fragment {

    HelperDB hlp;
    SQLiteDatabase db;
    TextView textView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DailyReward() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyReward.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyReward newInstance(String param1, String param2) {
        DailyReward fragment = new DailyReward();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_daily_reward, container, false);
        GifImageButton gifImageButton = view.findViewById(R.id.chest);
        textView=view.findViewById(R.id.rewardText);
        gifImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hlp = new HelperDB(getContext());
                //a function to roll a random reward of oinkers, show the rolled reward on the textview of the fragment, and add the new value to the database
                int c=0;
                db=hlp.getWritableDatabase();
                //multiplies the amount given in the daily reward with the times the bonus for the daily reward has been upgraded
                int rewardnumber= (int)(Math.random()*1000)*(getTimesUpgraded(db,"Farmers")+1);
                //textView.setText("Your Reward Is: "+rewardnumber+" Oinkers");
                Toast.makeText(getContext(),"Your Reward Is: "+rewardnumber+" Oinkers",Toast.LENGTH_LONG).show();
                c=getOinkCount(c);
                int newTotal= c+rewardnumber;
                db.execSQL("UPDATE oinkcounter SET oinkers = " + newTotal);
                db.close();
                Intent intent = new Intent(getContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return view;
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
    public int getOinkCount(int c){
        Cursor cursor2 = db.rawQuery("SELECT oinkers FROM oinkcounter", (String[]) null);
        cursor2.moveToFirst();
        String smth = "";
        for (int i = 0; i < cursor2.getCount(); i++) {
            smth = cursor2.getString(0);
        }
        cursor2.close();
        c = Integer.parseInt(smth);
        return c;
    }
}