package com.example.piramidnull;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RoomFragment extends Fragment {
    public static RoomFragment newInstance(String title, int imageRes) {
        RoomFragment fragment = new RoomFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("imageRes", imageRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        TextView title = view.findViewById(R.id.roomTitle);
        ImageView image = view.findViewById(R.id.roomImage);

        Bundle args = getArguments();
        if (args != null) {
            title.setText(args.getString("title"));
            image.setImageResource(args.getInt("imageRes"));
        }

        return view;
    }
}
