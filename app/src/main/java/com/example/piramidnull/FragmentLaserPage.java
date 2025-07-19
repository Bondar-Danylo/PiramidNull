package com.example.piramidnull;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class FragmentLaserPage extends Fragment {

    private static final String ARG_POSITION = "position";
    TextView hintNum;
    TextView hintText;
    ImageView hintImg;
    int position;
    LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            position = getArguments().getInt(ARG_POSITION);
        }

    }

    public static FragmentLaserPage newInstance(int position){
        FragmentLaserPage fragmentLaserPage = new FragmentLaserPage();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragmentLaserPage.setArguments(args);
        return fragmentLaserPage;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_laser_page, container ,false);
        allComponents(view);
        setupContent();
        return (view);
    }

    private void allComponents(View view){
        hintText = view.findViewById(R.id.hintText);
        hintImg = view.findViewById(R.id.hintImg);
        hintNum = view.findViewById(R.id.hintNum);
    }

    private void setupContent(){
        allContent content = getContentForPosition(position);
        hintText.setText(content.getHint());
        hintNum.setText(content.getNumber());
        hintImg.setImageResource(content.getImage());
    }

    private static class allContent{
        String number;
        String hint;
        Integer image;

        public allContent(String number, String hint, Integer image){
            this.number = number;
            this.hint = hint;
            this.image = image;
        }

        public String getNumber(){ return number; }
        public String getHint(){ return hint; }
        public Integer getImage(){ return image; }
    }


    private allContent getContentForPosition(int position){
        switch (position){
            case 0:
                return new allContent(
                        "Tip 1",
                        "'Align the laser with the jar to reach your final goal.'",
                        R.drawable.artefact_bird
                );
            case 1:
                return new allContent(
                        "Tip 2",
                        "'Use the jar to align the laser and complete your objective.'",
                        R.drawable.artefact_eye
                );
            case 2:
                return new allContent(
                        "Tip 3",
                        "'To achieve your goal, use the jar to guide the laser.'",
                        R.drawable.artefact_triangle
                );
            default:
                return new allContent(
                        "Tip 1",
                        "'Align the laser with the jar to reach your final goal.'",
                        R.drawable.artefact_bird
                );
        }
    }


}