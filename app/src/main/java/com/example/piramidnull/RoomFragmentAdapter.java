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

    // Optional: If you want smoother animations and more stable item handling
    @Override
    public long getItemId(int position) {
        // Return a unique ID for each item
        return roomList.get(position).getTitle().hashCode();
    }

    @Override
    public boolean containsItem(long itemId) {
        for (Room room : roomList) {
            if (room.getTitle().hashCode() == itemId) {
                return true;
            }
        }
        return false;
    }
}
