package com.example.webrtcdemo.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.webrtcdemo.Fragments.HistoryFragment;
import com.example.webrtcdemo.Fragments.HomeFragment;
import com.example.webrtcdemo.Fragments.UserFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private String id;
    private static final String SOCKETID = "socketId";

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Bundle bundle = new Bundle();
                bundle.putString(SOCKETID, id);
                HomeFragment honeFragment = new HomeFragment();
                honeFragment.setArguments(bundle);
                return honeFragment;
            case 1:
                return new HistoryFragment();
            case 2:
                return new UserFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}


