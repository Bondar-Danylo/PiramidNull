
package com.example.piramidnull;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class FragmentObjects extends Fragment {

    List<ObjectData> objectList = Arrays.asList(
            new ObjectData("Portal", "OB12-1", "Gold", "55 x 100 cm", R.drawable.avatar1, "Once upon a time ...", "Tomb", "Ancient Egypt", "10-10-10", "Unknown"),
            new ObjectData("Mirror", "OB13-2", "Bronze", "45 x 80 cm", R.drawable.redmark, "Once upon a time ...", "Egypt", "London", "30-30-20", "unknown")
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_objects, container, false);

        LinearLayout allObjects = view.findViewById(R.id.allObjects);

            for (ObjectData obj : objectList) {
                View objectView = inflater.inflate(R.layout.object_box, allObjects, false);

                TextView name = objectView.findViewById(R.id.objectName);
                TextView num = objectView.findViewById(R.id.objectNum);
                TextView material = objectView.findViewById(R.id.objectMaterial);
                TextView size = objectView.findViewById(R.id.objectSize);
                ImageView img = objectView.findViewById(R.id.objectImg);

                objectView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ObjectPage.class);
                        intent.putExtra("object_name", obj.title);
                        intent.putExtra("object_number", obj.objectNumber);
                        intent.putExtra("object_material", obj.material);
                        intent.putExtra("object_size", obj.size);
                        intent.putExtra("object_image", obj.imageResId);
                        intent.putExtra("object_story", obj.story);
                        intent.putExtra("object_found",obj.found);
                        intent.putExtra("object_origin",obj.origin);
                        intent.putExtra("object_date",obj.date);
                        intent.putExtra("object_function",obj.function);
                        startActivity(intent);
                    }
                });

//                allObjects.addView(objectView);

                name.setText(obj.title);
                num.setText(obj.objectNumber);
                material.setText(obj.material);
                size.setText(obj.size);
                img.setImageResource(obj.imageResId);

                allObjects.addView(objectView);
            }

            return view;
    }

    public class ObjectData {
        String title;
        String objectNumber;
        String material;
        String size;
        int imageResId;
        String story;
        String found;
        String origin;
        String date;
        String function;

        public ObjectData(String title, String objectNumber, String material, String size, int imageResId, String story, String found, String origin, String date, String function) {
            this.title = title;
            this.objectNumber = objectNumber;
            this.material = material;
            this.size = size;
            this.imageResId = imageResId;
            this.story = story;
            this.found = found;
            this.origin = origin;
            this.date = date;
            this.function = function;
        }
    }
}