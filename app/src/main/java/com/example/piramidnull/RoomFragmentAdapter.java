package com.example.piramidnull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class RoomFragmentAdapter extends FragmentStateAdapter {
    private final List<Room> roomList;

    public RoomFragmentAdapter(@NonNull FragmentActivity activity, List<Room> roomList) {
        super(activity);
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Room room = roomList.get(position);
        return RoomFragment.newInstance(room.getTitle(), room.getImageRes());
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}