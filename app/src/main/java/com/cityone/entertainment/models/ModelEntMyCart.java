package com.cityone.entertainment.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelEntMyCart implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;
    private int total_amount;

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
    public void setTotal_amount(int total_amount){
        this.total_amount = total_amount;
    }
    public int getTotal_amount(){
        return this.total_amount;
    }

    public class Result {

        private String id;

        private String name;

        private String image;

        private String date_time;

        private String cart_id;

        private String entertentment_category_id;

        private String price;

        private String description;

        private String quantity;

        private String entertentment_image;

        private String entertentment_item_id;

        public String getCart_id() {
            return cart_id;
        }

        public void setCart_id(String cart_id) {
            this.cart_id = cart_id;
        }

        public String getEntertentment_image() {
            return entertentment_image;
        }

        public void setEntertentment_image(String entertentment_image) {
            this.entertentment_image = entertentment_image;
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
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setEntertentment_category_id(String entertentment_category_id){
            this.entertentment_category_id = entertentment_category_id;
        }
        public String getEntertentment_category_id(){
            return this.entertentment_category_id;
        }
        public void setPrice(String price){
            this.price = price;
        }
        public String getPrice(){
            return this.price;
        }
        public void setDescription(String description){
            this.description = description;
        }
        public String getDescription(){
            return this.description;
        }
        public void setQuantity(String quantity){
            this.quantity = quantity;
        }
        public String getQuantity(){
            return this.quantity;
        }
        public void setEntertentment_item_id(String entertentment_item_id){
            this.entertentment_item_id = entertentment_item_id;
        }
        public String getEntertentment_item_id(){
            return this.entertentment_item_id;
        }
    }

}
