package com.example.piramidnull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class LaserAdapter extends FragmentStateAdapter {
    int count;

    public LaserAdapter(@NonNull FragmentActivity fragmentActivity, int count) {
        super(fragmentActivity);
        this.count = count;
    }

    @NonNull
    @Override
    public Fragment createFragment (int position){
        return FragmentLaserPage.newInstance(position);
    }

    public int getCount() {
        return count;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
