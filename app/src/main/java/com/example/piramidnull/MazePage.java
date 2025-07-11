package com.example.piramidnull;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MazePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maze_page);

        //Back to the main Page
        ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MazePage.this, MainPage.class);
                startActivity(intent);
            }
        });

        //Guide Bot
        ImageView guideBtn = (ImageView)findViewById(R.id.guideBtn);
        LinearLayout guideBubble = (LinearLayout) findViewById(R.id.guideBubble);
        LinearLayout guideBubble2 = (LinearLayout)findViewById(R.id.guideBubble2);
        Button continueBtn = (Button) findViewById(R.id.continueBtn);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideBubble.setVisibility(View.GONE);
                guideBubble2.setVisibility(View.VISIBLE);
            }
        });

        guideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(guideBubble2.getVisibility()==View.VISIBLE){
                    guideBubble2.setVisibility(View.GONE);
                }
                else if(guideBubble.getVisibility()==View.VISIBLE){
                    guideBubble2.setVisibility(View.VISIBLE);
                    guideBubble.setVisibility(View.GONE);
                }
                else{
                    guideBubble.setVisibility(View.VISIBLE);
                }
            }
        });


        FrameLayout mapOutlines = findViewById(R.id.mapOutlines);

        ImageView undoBtn = (ImageView)findViewById(R.id.undoBtn);
        ImageView redBtn = (ImageView) findViewById(R.id.redBtn);
        ImageView greenBtn = (ImageView) findViewById(R.id.greenBtn);
        ImageView hintBtn = (ImageView) findViewById(R.id.hintBtn);

        redBtn.setTag(R.drawable.redmark);
        greenBtn.setTag(R.drawable.check);

        setDragTouchListener(redBtn);
        setDragTouchListener(greenBtn);

        //remove icons one by one
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the last added marker
                int childCount = mapOutlines.getChildCount();
                if (childCount > 0) {
                    mapOutlines.removeViewAt(childCount - 1);
                }
            }
        });

        //hint button and pop up box
        hintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                View popupHint = inflater.inflate(R.layout.popup_hint_maze, null);

                final Dialog dialog = new Dialog(MazePage.this);
                dialog.setContentView(popupHint);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                ImageView closeHintBtn = popupHint.findViewById(R.id.closeHintBtn);
                closeHintBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        //Drag and drop icons
        mapOutlines.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()){
                    case DragEvent.ACTION_DROP:
                        int x = (int) event.getX();
                        int y = (int) event.getY();

                        Integer imageResourcesObj = (Integer) event.getLocalState();
                        int imageResources = imageResourcesObj.intValue();

                        ImageView newImage = new ImageView(MazePage.this);
                        newImage.setImageResource(imageResources);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100,100);
                        params.leftMargin = x-50;
                        params.topMargin = y-50;
                        newImage.setLayoutParams(params);

                        mapOutlines.addView(newImage);
                        break;
                }
                return true;
            }
        });
    }

    private void setDragTouchListener(final ImageView sourceImage){
        sourceImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    int imageResourcesID = (int) v.getTag();
                    ClipData.Item item = new ClipData.Item(String.valueOf(imageResourcesID));
                    String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                    ClipData dragData =  new ClipData("image",mimeTypes, item);

                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

                    v.startDragAndDrop(dragData, shadowBuilder, Integer.valueOf(imageResourcesID), 0);
                    return true;
                }
                return false;
            }
        });
    }
}