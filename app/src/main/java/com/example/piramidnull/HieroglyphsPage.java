package com.example.piramidnull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.HashMap;

public class HieroglyphsPage extends AppCompatActivity {
    GridLayout cardGrid;
    GridLayout slotGrid;

    int[] cardDrawables = {
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6,
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6,
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3
    };

    HashMap<View, ViewGroup> originalParents = new HashMap<>();
    HashMap<View, Integer> cardIds = new HashMap<>();
    HashMap<View, Integer> slotIds = new HashMap<>();
    HashMap<Integer, Integer> correctAssignments = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hieroglyphs_page);

        cardGrid = findViewById(R.id.cardGrid);
        slotGrid = findViewById(R.id.slotGrid);

        correctAssignments.put(2, 0); // 3rd image in 1st slot
        correctAssignments.put(4, 1); // 5th image in 2nd slot
        correctAssignments.put(6, 2); // 7th image in 3rd slot

        addCards();
        addSlots();

        findViewById(android.R.id.content).setOnDragListener(new DragHandler());

        // Back button
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HieroglyphsPage.this, Main.class);
                startActivity(intent);
            }
        });

        //Popup
        ImageView popupBtn = findViewById(R.id.popupBtn);
        popupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(HieroglyphsPage.this);
                dialog.setContentView(R.layout.hieroglyphs_popup);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                ImageView closeBtn = dialog.findViewById(R.id.close_popup);

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    private void addCards() {
        for (int i = 0; i < cardDrawables.length; i++) {
            View card = LayoutInflater.from(this).inflate(R.layout.card_item, cardGrid, false);
            ImageView icon = card.findViewById(R.id.card_icon);
            icon.setImageResource(cardDrawables[i]);
            card.setOnTouchListener(new CardTouchListener());

            card.setTag(i);
            cardIds.put(card, i);
            originalParents.put(card, cardGrid);
            cardGrid.addView(card);
        }
    }

    private void addSlots() {
        for (int i = 0; i < 3; i++) {
            View slot = LayoutInflater.from(this).inflate(R.layout.slot_item, slotGrid, false);
            slot.setTag(i);
            slotIds.put(slot, i);
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

                                    // Make slot default color
                                    Drawable bg = slot.getBackground().mutate();
                                    if (bg instanceof VectorDrawableCompat || bg instanceof VectorDrawable) {
                                        bg.setColorFilter(ContextCompat.getColor(HieroglyphsPage.this, R.color.blue_border), PorterDuff.Mode.SRC_IN);
                                    }
                                }

                                ViewGroup parent = (ViewGroup) dragged.getParent();
                                if (parent != null) parent.removeView(dragged);
                                vg.addView(dragged);
                                droppedInSlot = true;

                                int cardId = cardIds.get(dragged);
                                int slotId = (int) slot.getTag();



                                Drawable bg = slot.getBackground().mutate();
                                if (bg instanceof VectorDrawableCompat || bg instanceof VectorDrawable) {
                                    if (correctAssignments.containsKey(cardId) && correctAssignments.get(cardId) == slotId) {
                                        // Correct
                                        bg.setColorFilter(ContextCompat.getColor(HieroglyphsPage.this, R.color.green), PorterDuff.Mode.SRC_IN);
                                    } else {
                                        // Wrong
                                        bg.setColorFilter(ContextCompat.getColor(HieroglyphsPage.this, R.color.red), PorterDuff.Mode.SRC_IN);
                                    }
                                }

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

                    resetEmptySlotsColor();

                    return true;
            }
            return true;
        }
    }

    private void resetEmptySlotsColor() {
        for (int i = 0; i < slotGrid.getChildCount(); i++) {
            View slot = slotGrid.getChildAt(i);
            if (slot instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) slot;
                if (vg.getChildCount() == 0) {
                    Drawable bg = slot.getBackground().mutate();
                    if (bg instanceof VectorDrawableCompat || bg instanceof VectorDrawable) {
                        bg.setColorFilter(ContextCompat.getColor(HieroglyphsPage.this, R.color.blue_border), PorterDuff.Mode.SRC_IN);
                    }
                }
            }
        }
    }
}
