package com.cityone.entertainment.movies.models;

import java.util.ArrayList;

public class ModelTheaterDetailsNew {
    private  Result result;

    private String status;

    private String message;

    public void setResult (Result result) {
        this.result = result;
    }
    public Result getResult () {
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

        private ArrayList<slots> slots;

        public class slots {

            private String id;

            private String theater_id;

            private String slots_start;

            private String slots_end;

            private String movie_id;

            private String screen_id;

            private ArrayList<seats> seats;


            public class seats {
                private String seat_no;

                private String status;
                public String getSeat_no() {
                    return seat_no;
                }

                public void setSeat_no(String seat_no) {
                    this.seat_no = seat_no;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTheater_id() {
                return theater_id;
            }

            public void setTheater_id(String theater_id) {
                this.theater_id = theater_id;
            }

            public String getSlots_start() {
                return slots_start;
            }

            public void setSlots_start(String slots_start) {
                this.slots_start = slots_start;
            }

            public String getSlots_end() {
                return slots_end;
            }

            public void setSlots_end(String slots_end) {
                this.slots_end = slots_end;
            }

            public String getMovie_id() {
                return movie_id;
            }

            public void setMovie_id(String movie_id) {
                this.movie_id = movie_id;
            }

            public String getScreen_id() {
                return screen_id;
            }

            public void setScreen_id(String screen_id) {
                this.screen_id = screen_id;
            }

            public ArrayList<Result.slots.seats> getSeats() {
                return seats;
            }

            public void setSeats(ArrayList<Result.slots.seats> seats) {
                this.seats = seats;
            }
        }

        public ArrayList<Result.slots> getSlots() {
            return slots;
        }

        public void setSlots(ArrayList<Result.slots> slots) {
            this.slots = slots;
        }



/*   public ArrayList<Result.theater_seats> getTheater_seats() {
            return theater_seats;
        }

        public void setTheater_seats(ArrayList<Result.theater_seats> theater_seats) {
            this.theater_seats = theater_seats;
        }*/

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
