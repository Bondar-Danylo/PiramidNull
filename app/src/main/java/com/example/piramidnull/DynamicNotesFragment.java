package com.example.piramidnull;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DynamicNotesFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView fieldNoteNu;
    private TextView dateTextView;
    private int position;

    public static DynamicNotesFragment newInstance(int position) {
        DynamicNotesFragment fragment = new DynamicNotesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_dynamic_notes, container, false);
        initViews(view);
        setupContent();
        return (view);
    }

    private void initViews(View view) {
        titleTextView = view.findViewById(R.id.titleTextView);
        contentTextView = view.findViewById(R.id.contentTextView);
        fieldNoteNu = view.findViewById(R.id.fieldNoteNu);
        dateTextView = view.findViewById(R.id.dateTextView);
    }

    private void setupContent() {
        ContentData contentData = getContentForPosition(position);

        titleTextView.setText(contentData.getTitle());
        contentTextView.setText(contentData.getContent());
        fieldNoteNu.setText(contentData.getNumber());
        dateTextView.setText(contentData.getDate());
    }

    private ContentData getContentForPosition(int position){
        switch (position){
            case 0:
                return new ContentData(
                        "Pyramids",
                        "The pyramids of Egypt are ancient, monumental structures built as tombs for pharaohs and important figures. The most famous, the Great Pyramid of Giza, is one of the Seven Wonders of the Ancient World and was constructed around 2600 BCE.",
                        "Field Note #"+ (position+1),
                        "22-09-33"
                );
            case 1:
                return new ContentData(
                        "Mummies",
                        "Some say ancient Egyptian mummies weren’t just preserved for the afterlife — they were encoded with star maps and DNA keys, meant to guide alien civilizations back to Earth once humanity was ready to unlock cosmic knowledge.",
                        "Field Note #"+ (position+1),
                        "02-00-222"
                );
            case 2:
                return new ContentData(
                        "Mysterious Object",
                        "Archaeologists recently uncovered a mysterious object buried near a forgotten pyramid—an obsidian orb that pulses faintly with energy. Legends call it the “Eye of Ra-9,” said to be a device gifted by sky beings.",
                        "Field Note #"+ (position+1),
                        "19-02-22"
                );
            case 3:
                return new ContentData(
                        "Tomb",
                        "Deep beneath the sands, some ancient tombs are rumored to contain “gravity wells” — chambers where time moves slower, designed to preserve the pharaoh’s soul until the stars align. Explorers say compasses spin wildly near these tombs...",
                        "Field Note #"+ (position+1),
                        "09-98-63"
                );
            default:
                return new ContentData(
                        "Pyramids",
                        "The pyramids of Egypt are ancient, monumental structures built as tombs for pharaohs and important figures. The most famous, the Great Pyramid of Giza, is one of the Seven Wonders of the Ancient World and was constructed around 2600 BCE.",
                        "Field Note #"+ (position+1),
                        "22-09-33"
                );
        }
    }




private static class ContentData {
    private String title;
    private String content;
    private String number;
    private String date;
    public ContentData(String title, String content, String number, String date) {
        this.title = title;
        this.content = content;
        this.number = number;
        this.date = date;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getNumber() {return number;}
    public String getDate(){return date;}

}
}