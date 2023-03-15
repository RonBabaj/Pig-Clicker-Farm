package com.example.hazirclicker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class help extends AppCompatActivity {
BottomNavigationView bottomNavigationView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().findItem(R.id.suit).setCheckable(false);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.house:
                        Intent intent = new Intent(help.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.wrench:
                        Intent intn= new Intent(help.this, Upgrades.class);
                        startActivity(intn);
                        return true;
                    case R.id.suit:
                        return true;
                    case R.id.potion:
                        Intent in = new Intent(help.this, Boosts.class);
                        startActivity(in);
                        return true;
                    case R.id.wallpaper:
                        Intent inte = new Intent(help.this, Backgrounds.class);
                        startActivity(inte);
                        return true;
                }
                return false;
            }
        });
    }
}