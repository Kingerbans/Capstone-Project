package com.example.webrtcdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.webrtcdemo.Adapter.ViewPagerAdapter;
import com.example.webrtcdemo.Handler.SocketHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import io.socket.emitter.Emitter;

public class DashboardActivity extends AppCompatActivity {
    private static final String CALL = "call";
    private static final String CALLACCEPT = "call-accept";
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    RelativeLayout relativeLayout;
    ImageView btnAccept, btnReject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.viewPager);

        setupViewPager();

        relativeLayout = findViewById(R.id.callLayout);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuHome:
                        viewPager.setCurrentItem(0);
                        break;

                    case R.id.menuHistory:
                        viewPager.setCurrentItem(1);
                        break;

                    case R.id.menuUsers:
                        viewPager.setCurrentItem(2);
                        break;

                    case R.id.menuLogout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                        finish();
                        break;
                }
                return true;
            }
        });
        SocketHandler.getSocket().on(CALL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                relativeLayout.setVisibility(View.VISIBLE);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SocketHandler.getSocket().emit(CALLACCEPT);
                        startActivity(new Intent(DashboardActivity.this, CallActivity.class));
                    }
                });
            }
        });
    }

    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.menuHome).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.menuHistory).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.menuUsers).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}