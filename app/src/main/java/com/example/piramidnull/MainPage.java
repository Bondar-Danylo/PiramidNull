package com.example.piramidnull;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import java.util.HashMap;

public class MainPage extends AppCompatActivity {
    GridLayout cardGrid;
    GridLayout slotGrid;

    HashMap<View, ViewGroup> originalParents = new HashMap<>();
    HashMap<View, PlaceholderData> placeholders = new HashMap<>();

    int[] cardDrawables = {
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6,
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6,
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6,
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        cardGrid = findViewById(R.id.cardGrid);
        slotGrid = findViewById(R.id.slotGrid);

        addCards();
        addSlots();
    }

    private void addCards() {
        for (int i = 0; i < cardDrawables.length; i++) {
            int drawableId = cardDrawables[i];
            View card = LayoutInflater.from(this).inflate(R.layout.card_item, cardGrid, false);
            ImageView icon = card.findViewById(R.id.card_icon);
            icon.setImageResource(drawableId);

            card.setTag(i);
            card.setOnTouchListener(new CardTouchListener());

            cardGrid.addView(card);
            originalParents.put(card, cardGrid);
        }
    }

    private void addSlots() {
        for (int i = 0; i < 3; i++) {
            View slot = LayoutInflater.from(this).inflate(R.layout.slot_item, slotGrid, false);
            slot.setOnDragListener(new SlotDragListener());
            slotGrid.addView(slot);
        }
    }

    private static class PlaceholderData {
        ViewGroup parent;
        int index;
        ViewGroup.LayoutParams layoutParams;
        View placeholderView;

        PlaceholderData(ViewGroup parent, int index, ViewGroup.LayoutParams layoutParams, View placeholderView) {
            this.parent = parent;
            this.index = index;
            this.layoutParams = layoutParams;
            this.placeholderView = placeholderView;
        }
    }

    private class CardTouchListener implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDragAndDrop(data, shadowBuilder, view, 0);

                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent == null) return false;

                int index = parent.indexOfChild(view);
                ViewGroup.LayoutParams originalParams = view.getLayoutParams();

                View placeholder = new View(MainPage.this);
                placeholder.setLayoutParams(originalParams);
                placeholder.setBackgroundColor(0x22CCCCCC);

                parent.removeView(view);
                parent.addView(placeholder, index);

                placeholders.put(view, new PlaceholderData(parent, index, originalParams, placeholder));

                return true;
            }
            return false;
        }
    }

    private class SlotDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View target, DragEvent event) {
            View dragged = (View) event.getLocalState();
            Log.d("DragDebug", "Event: " + event.getAction() + " on target: " + target.getClass().getSimpleName());

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DROP:
                    if (dragged == null) return false;

                    if (!(target instanceof ViewGroup)) return false;
                    ViewGroup targetGroup = (ViewGroup) target;



                    // Удаляем существующую карточку из слота
                    View existing = targetGroup.getChildAt(0);

                    if (existing != null) {
                        ViewGroup parentOfExisting = (ViewGroup) existing.getParent();
                        if (parentOfExisting != null) {
                            parentOfExisting.removeView(existing); // 🔥 удалить из текущего родителя
                        }

                        PlaceholderData existingPlaceholderData = placeholders.get(existing);
                        if (existingPlaceholderData != null) {
                            ViewGroup placeholderParent = existingPlaceholderData.parent;
                            int index = existingPlaceholderData.index;
                            ViewGroup.LayoutParams layoutParams = existingPlaceholderData.layoutParams;
                            View placeholderView = existingPlaceholderData.placeholderView;

                            if (placeholderParent != null && placeholderView != null) {
                                placeholderParent.removeView(placeholderView);
                            }

                            existing.setLayoutParams(layoutParams);
                            if (placeholderParent != null) {
                                placeholderParent.addView(existing, index);
                            } else {
                                cardGrid.addView(existing);
                            }

                            placeholders.remove(existing);
                        } else {
                            ViewGroup original = originalParents.getOrDefault(existing, cardGrid);
                            original.addView(existing); // здесь тоже безопасно после removeView
                        }
                    }



                    targetGroup.addView(dragged);
                    dragged.setVisibility(View.VISIBLE);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    if (dragged == null) return false;

                    if (!event.getResult()) {
                        PlaceholderData restore = placeholders.remove(dragged);
                        if (restore != null) {
                            // Удаляем плейсхолдер
                            if (restore.placeholderView.getParent() != null) {
                                restore.parent.removeView(restore.placeholderView);
                            }

                            // Удаляем перетаскиваемую карточку из текущего родителя (если осталась где-то)
                            if (dragged.getParent() != null) {
                                ((ViewGroup) dragged.getParent()).removeView(dragged);
                            }

                            dragged.setLayoutParams(restore.layoutParams);

                            // Если вернули из слота — всегда возвращаем в cardGrid
                            if (restore.parent == slotGrid) {
                                cardGrid.addView(dragged); // просто в конец
                            } else {
                                restore.parent.addView(dragged, restore.index); // вернуть в оригинальную позицию
                            }

                            Log.d("DragDebug", "Restored card at index " + restore.index);
                        }
                    }
                    return true;

            }

            return true;
        }
    }
}