package com.example.piramidnull;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FragmentNotes extends Fragment {

    public FragmentNotes(){}

    TabLayout tabLayout;
    ViewPager2 nestedNotes;
    viewPagerAdapter viewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        tabLayout = view.findViewById(R.id.tab_indicator);
        nestedNotes = view.findViewById(R.id.nestedNotes);
        viewPagerAdapter = new viewPagerAdapter(this);
        nestedNotes.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, nestedNotes,
                (tab, position) -> {
                    switch (position){
                        case 0: tab.setText("Discovery "+ (position + 1));
                            break;

                        case 1: tab.setText("Discovery "+ (position + 1));
                            break;
                    }
                }
        ).attach();

        return view;
    }
}