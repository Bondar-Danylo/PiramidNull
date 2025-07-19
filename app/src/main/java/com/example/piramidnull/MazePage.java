package com.example.piramidnull;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    //animation for instructions
    ImageView pointer;
    ImageView redmark;

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
        LinearLayout guideBubble3 = (LinearLayout)findViewById(R.id.guideBubble3);
        Button continueBtn = (Button) findViewById(R.id.continueBtn);
        Button nextBtn = (Button) findViewById(R.id.nextBtn);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideBubble.setVisibility(View.GONE);
                guideBubble2.setVisibility(View.VISIBLE);

                //icons for the instructions of the maze
                pointer = (ImageView) findViewById(R.id.pointerIcon);
                redmark = (ImageView) findViewById(R.id.redmarkIcon);
                ImageView check = (ImageView) findViewById(R.id.checkIcon);
                ImageView placedRedmark = (ImageView) findViewById(R.id.placedRedmark);
                ImageView pointer2 = (ImageView) findViewById(R.id.pointerIcon2);
                ImageView placedCheck = (ImageView) findViewById(R.id.placedCheck);


                Animation animation = AnimationUtils.loadAnimation(MazePage.this, R.anim.instruction_maze_animation);
                redmark.startAnimation(animation);

                placedRedmark.setVisibility(View.GONE);
                placedCheck.setVisibility(View.GONE);

                //pointers animation
                Animation animation2 = AnimationUtils.loadAnimation(MazePage.this, R.anim.pointer_animation);
                Animation animation3 = AnimationUtils.loadAnimation(MazePage.this, R.anim.pointer_animation);

                pointer.startAnimation(animation2);

                animation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        pointer2.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        pointer.setVisibility(View.GONE);
                        pointer.clearAnimation();

//
                        pointer2.startAnimation(animation3);
                        placedRedmark.setVisibility(View.VISIBLE);
                        check.startAnimation(animation3);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                animation3.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        pointer2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        pointer2.setVisibility(View.GONE);
                        pointer2.clearAnimation();
                        placedCheck.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

        //last bubble
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideBubble2.setVisibility(View.GONE);
                guideBubble3.setVisibility(View.VISIBLE);
            }
        });

        //guidebot
        guideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(guideBubble.getVisibility()==View.GONE && guideBubble2.getVisibility()==View.GONE && guideBubble3.getVisibility()==View.GONE){
                    guideBubble.setVisibility(View.VISIBLE);
                }
                else if(guideBubble3.getVisibility()==View.VISIBLE){
                    guideBubble3.setVisibility(View.GONE);
                }

//                if(guideBubble.getVisibility()==View.GONE){
//                    guideBubble.setVisibility(View.VISIBLE);
//                }
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