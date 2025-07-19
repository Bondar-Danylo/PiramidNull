
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
            new ObjectData("Scarab", "OB12-1", "Symbolic Trail Artifact", "10 x 20 x 30cm", R.drawable.artefact_bird, "The Scarab is a sacred emblem representing rebirth and protection in ancient Egyptian culture. This artifact, however, is unlike any found in traditional tombs — forged from an unknown metallic alloy that gleams with a reflective sheen, it bears non-Egyptian geometrical seams and a stylized depiction of Thoth, the god of wisdom.\n" +
                    "Legend says it was created by a secret sect of priests who were tasked with safeguarding forbidden knowledge — knowledge not of this Earth. Hidden deep within the riddle chamber, the Scarab is protected by a complex rotating glyph puzzle, which only reveals itself to those who understand the language of the ancients.\n" +
                    "Once claimed, the Scarab resonates faintly, as if “remembering” something. It is believed to be the first key in unlocking the ancient escape mechanism, acting as a “soul trigger” that aligns part of the portal’s core systems.\n", "Found at end of Riddle Puzzle", "Ancient Egypt", "10-10-10", "Activates the first piece of the portal interface"),
            new ObjectData("Eye Of Horus", "OB13-2", "Maze Trail Artifact", "45 x 80 x 22 cm", R.drawable.artefact_triangle, "The Eye of Horus symbolizes perception, guidance, and the power to see through illusion. This artifact, carved from ancient stone and etched with protective runes, was believed to be a navigation relic, helping chosen explorers find their way through sacred passageways.\n" +
                    "Recovered at the heart of the tomb’s labyrinth, the Eye of Horus is earned by surviving a shifting maze filled with false paths and time-sensitive doorways. Ancient carvings along the walls tell of “a single eye that sees the path when all else is lost.”\n" +
                    "When placed into the portal console, the Eye of Horus acts as a stabilizer, guiding the portal’s pathing system and ensuring a safe destination during activation.\n", "Found at the end of the Maze", "Ancient Egypt", "30-30-20", "Activates the second piece of the portal interface"),
            new ObjectData("Ankh", "OB13-3", "Laser Trail Artifact", "45 x 80 x 10 cm", R.drawable.artefact_eye, "The Ankh — the key of life — has long been associated with immortality and the soul’s passage into the afterlife. This particular artifact, however, is a fusion of divine symbolism and advanced energy manipulation. Metallic and polished, it pulses with a soft blue glow as if channeling a hidden power source.\n" +
                    "To retrieve it, players must solve an intricate light-based puzzle, aligning laser beams through ancient statues to unlock the chamber’s final mechanism. The Ankh responds only to perfect symmetry and timing, a test meant to prove worthiness.\n" +
                    "Once integrated into the portal console, the Ankh completes the energy circuit. It acts as the central conduit, focusing and amplifying the combined power of the other two artifacts — triggering the portal and offering Alan his last chance to escape.\n", "Acquired after completing the laser reflection puzzle", "Ancient Egypt", "30-30-20", "Activates the final piece of the portal interface")

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