package com.example.piramidnull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.piramidnull.FragmentNote1;
import com.example.piramidnull.FragmentNote2;

public class viewPagerAdapter extends FragmentStateAdapter {

    public viewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentNote1();
            case 1:
                return new FragmentNote2();
            default:
                return new FragmentNote1();
        }
    }

        @Override
        public int getItemCount() {
            return 2;
        }
}
