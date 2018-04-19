package com.example.danilo.daniloportfolio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.danilo.daniloportfolio.controllers.Adapter;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TextView contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new Adapter(getSupportFragmentManager()));
        contact = findViewById(R.id.getintouch);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dial = "tel:" + "9142084825";
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
            }
        });

        TabLayout tabLayout = findViewById(R.id.tablayoutid);
        tabLayout.setupWithViewPager(viewPager);
    }
}
