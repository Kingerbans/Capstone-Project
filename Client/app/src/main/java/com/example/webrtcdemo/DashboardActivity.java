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

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class DashboardActivity extends AppCompatActivity {
    private static final String CALL = "call";
    private static final String SOCKETID = "socketId";
    private static final String CHECKCALLER = "checkCaller";
    private static final String FULLNAME = "fullName";
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    ImageView btnAccept, btnReject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.viewPager);

        setupViewPager();
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

        viewPagerAdapter.setId(getIntent().getExtras().getString(SOCKETID));

        SocketHandler.getSocket().on(CALL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String fullName = (String) args[0];
                Intent intent = new Intent(DashboardActivity.this, CallActivity.class);
                intent.putExtra(CHECKCALLER, false);
                intent.putExtra(FULLNAME, fullName);
                startActivity(intent);
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