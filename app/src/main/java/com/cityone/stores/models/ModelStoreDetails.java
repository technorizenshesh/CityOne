package com.cityone.stores.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelStoreDetails implements Serializable {

    private Result result;

    private String message;

    private String status;

    public void setResult(Result result){
        this.result = result;
    }
    public Result getResult(){
        return this.result;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public class Result
    {
        private String id;

        private String name;

        private String image;

        private String lat;

        private String lon;

        private String open_time;

        private String close_time;

        private String description;

        private String created_at;

        private String update_at;

        private String status;

        private String address;

        private ArrayList<Restaurant_sub_category> restaurant_sub_category;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
        public void setLat(String lat){
            this.lat = lat;
        }
        public String getLat(){
            return this.lat;
        }
        public void setLon(String lon){
            this.lon = lon;
        }
        public String getLon(){
            return this.lon;
        }
        public void setOpen_time(String open_time){
            this.open_time = open_time;
        }
        public String getOpen_time(){
            return this.open_time;
        }
        public void setClose_time(String close_time){
            this.close_time = close_time;
        }
        public String getClose_time(){
            return this.close_time;
        }
        public void setDescription(String description){
            this.description = description;
        }
        public String getDescription(){
            return this.description;
        }
        public void setCreated_at(String created_at){
            this.created_at = created_at;
        }
        public String getCreated_at(){
            return this.created_at;
        }
        public void setUpdate_at(String update_at){
            this.update_at = update_at;
        }
        public String getUpdate_at(){
            return this.update_at;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setRestaurant_sub_category(ArrayList<Restaurant_sub_category> restaurant_sub_category){
            this.restaurant_sub_category = restaurant_sub_category;
        }
        public ArrayList<Restaurant_sub_category> getRestaurant_sub_category(){
            return this.restaurant_sub_category;
        }

        public class Restaurant_sub_category
        {
            private String id;

            private String restaurant_id;

            private String restaurant_category_id;

            private String name;

            private String image;

            private String status;

            private String created_at;

            private String updated_at;

            private String restaurant_category_name;

            private String restaurant_sub_category_image;

            public void setId(String id){
                this.id = id;
            }
            public String getId(){
                return this.id;
            }
            public void setRestaurant_id(String restaurant_id){
                this.restaurant_id = restaurant_id;
            }
            public String getRestaurant_id(){
                return this.restaurant_id;
            }
            public void setRestaurant_category_id(String restaurant_category_id){
                this.restaurant_category_id = restaurant_category_id;
            }
            public String getRestaurant_category_id(){
                return this.restaurant_category_id;
            }
            public void setName(String name){
                this.name = name;
            }
            public String getName(){
                return this.name;
            }
            public void setImage(String image){
                this.image = image;
            }
            public String getImage(){
                return this.image;
            }
            public void setStatus(String status){
                this.status = status;
            }
            public String getStatus(){
                return this.status;
            }
            public void setCreated_at(String created_at){
                this.created_at = created_at;
            }
            public String getCreated_at(){
                return this.created_at;
            }
            public void setUpdated_at(String updated_at){
                this.updated_at = updated_at;
            }
            public String getUpdated_at(){
                return this.updated_at;
            }
            public void setRestaurant_category_name(String restaurant_category_name){
                this.restaurant_category_name = restaurant_category_name;
            }
            public String getRestaurant_category_name(){
                return this.restaurant_category_name;
            }
            public void setRestaurant_sub_category_image(String restaurant_sub_category_image){
                this.restaurant_sub_category_image = restaurant_sub_category_image;
            }
            public String getRestaurant_sub_category_image(){
                return this.restaurant_sub_category_image;
            }
        }

    }


}
