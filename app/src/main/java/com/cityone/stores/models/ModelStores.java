package com.cityone.stores.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelStores implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
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

        private String restaurant_id;

        private String restaurant_category_id;

        private String name;

        private String image;

        private String amount;

        private String status;

        private String created_at;

        private String updated_at;

        private String restaurant_category_name;

        private String restaurant_name;

        private String restaurant_image;
        private String address;
        private String lat;
        private String lon;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

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
        public void setAmount(String amount){
            this.amount = amount;
        }
        public String getAmount(){
            return this.amount;
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
        public void setRestaurant_name(String restaurant_name){
            this.restaurant_name = restaurant_name;
        }
        public String getRestaurant_name(){
            return this.restaurant_name;
        }
        public void setRestaurant_image(String restaurant_image){
            this.restaurant_image = restaurant_image;
        }
        public String getRestaurant_image(){
            return this.restaurant_image;
        }
    }


}
