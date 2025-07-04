package com.example.piramidnull;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class MainPage extends AppCompatActivity {
    GridLayout cardGrid;
    GridLayout slotGrid;
    int[] cardDrawables = {
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6,
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6,
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6,
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3
    };


    HashMap<View, ViewGroup> originalParents = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        cardGrid = findViewById(R.id.cardGrid);
        slotGrid = findViewById(R.id.slotGrid);

        addCards();
        addSlots();

        findViewById(android.R.id.content).setOnDragListener(new DragHandler());
    }

    private void addCards() {
        for (int i = 0; i < cardDrawables.length; i++) {
            View card = LayoutInflater.from(this).inflate(R.layout.card_item, cardGrid, false);
            ImageView icon = card.findViewById(R.id.card_icon);
            icon.setImageResource(cardDrawables[i]);
            card.setOnTouchListener(new CardTouchListener());
            originalParents.put(card, cardGrid);
            cardGrid.addView(card);
        }
    }

    private void addSlots() {
        for (int i = 0; i < 3; i++) {
            View slot = LayoutInflater.from(this).inflate(R.layout.slot_item, slotGrid, false);
            slotGrid.addView(slot);
        }
    }

    private class CardTouchListener implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
                view.startDragAndDrop(data, shadow, view, 0);
                return true;
            }
            return false;
        }
    }

    private class DragHandler implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final View dragged = (View) event.getLocalState();

            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    if (dragged == null) return false;

                    boolean droppedInSlot = false;

                    for (int i = 0; i < slotGrid.getChildCount(); i++) {
                        View slot = slotGrid.getChildAt(i);
                        int[] location = new int[2];
                        slot.getLocationOnScreen(location);

                        float x = event.getX() + ((View) v).getLeft();
                        float y = event.getY() + ((View) v).getTop();

                        if (x >= location[0] && x <= location[0] + slot.getWidth() &&
                                y >= location[1] && y <= location[1] + slot.getHeight()) {

                            if (slot instanceof ViewGroup) {
                                ViewGroup vg = (ViewGroup) slot;

                                if (vg.getChildCount() > 0) {
                                    View oldCard = vg.getChildAt(0);
                                    vg.removeView(oldCard);
                                    cardGrid.addView(oldCard);
                                }

                                ViewGroup parent = (ViewGroup) dragged.getParent();
                                if (parent != null) parent.removeView(dragged);
                                vg.addView(dragged);
                                droppedInSlot = true;
                                break;
                            }
                        }
                    }

                    if (!droppedInSlot) {
                        ViewGroup parent = (ViewGroup) dragged.getParent();
                        if (parent != null) parent.removeView(dragged);
                        ViewGroup original = originalParents.get(dragged);
                        if (original != null) original.addView(dragged);
                    }

                    return true;
            }
            return true;
        }

    }
}
