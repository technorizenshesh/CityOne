package com.cityone.entertainment.movies.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelAvilTheaterOne implements Serializable {

        private ArrayList<Result> result;

        private String status;

        private String message;

        public void setResult (ArrayList <Result> result) {
            this.result = result;
        }
        public ArrayList<Result> getResult () {
            return this.result;
        }
        public void setStatus (String status){
            this.status = status;
        }
        public String getStatus () {
            return this.status;
        }
        public void setMessage (String message){
            this.message = message;
        }
        public String getMessage () {
            return this.message;
        }

        public class Result {

            private String id;

            private String name;

            private String address;

            private String city;

            private String state;

            private String zip_code;

            private String phone_number;

            private String email;

            private String website;

            private String seating_capacity;

            private String screens;

            private String opening_hours;

            private String closing_hours;

            private String ticket_prices;

            private String show_types;

            private String special_features;

            private String accessibility_features;

            private String image;


            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getZip_code() {
                return zip_code;
            }

            public void setZip_code(String zip_code) {
                this.zip_code = zip_code;
            }

            public String getPhone_number() {
                return phone_number;
            }

            public void setPhone_number(String phone_number) {
                this.phone_number = phone_number;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getWebsite() {
                return website;
            }

            public void setWebsite(String website) {
                this.website = website;
            }

            public String getSeating_capacity() {
                return seating_capacity;
            }

            public void setSeating_capacity(String seating_capacity) {
                this.seating_capacity = seating_capacity;
            }

            public String getScreens() {
                return screens;
            }

            public void setScreens(String screens) {
                this.screens = screens;
            }

            public String getOpening_hours() {
                return opening_hours;
            }

            public void setOpening_hours(String opening_hours) {
                this.opening_hours = opening_hours;
            }

            public String getClosing_hours() {
                return closing_hours;
            }

            public void setClosing_hours(String closing_hours) {
                this.closing_hours = closing_hours;
            }

            public String getTicket_prices() {
                return ticket_prices;
            }

            public void setTicket_prices(String ticket_prices) {
                this.ticket_prices = ticket_prices;
            }

            public String getShow_types() {
                return show_types;
            }

            public void setShow_types(String show_types) {
                this.show_types = show_types;
            }

            public String getSpecial_features() {
                return special_features;
            }

            public void setSpecial_features(String special_features) {
                this.special_features = special_features;
            }

            public String getAccessibility_features() {
                return accessibility_features;
            }

            public void setAccessibility_features(String accessibility_features) {
                this.accessibility_features = accessibility_features;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }
        }
    }

