package com.example.piramidnull;

public class Challenge {
        private String title;
        private String name;
        private int imageResId;
        private String buttonText;
        private int destinationId;
        public Challenge(String title, String name, int imageResId, String buttonText, int destinationId) {
            this.title = title;
            this.name = name;
            this.imageResId = imageResId;
            this.buttonText = buttonText;
            this.destinationId = destinationId;
        }
        public String getTitle() {
            return title;
        }
        public String getName() {
            return name;
        }
        public int getImageResId() {
            return imageResId;
        }
        public String getButtonText() {
            return buttonText;
        }
        public int getDestinationId() {
            return destinationId;
        }
    }

