package com.example.piramidnull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class viewPagerAdapter extends FragmentStateAdapter {

    public viewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DynamicNotesFragment.newInstance(position);
    }

        @Override
        public int getItemCount() {
            return 4;
        }
}
