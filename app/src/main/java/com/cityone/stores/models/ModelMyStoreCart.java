package com.cityone.stores.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelMyStoreCart implements Serializable {

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

    public class Result
    {
        private String id;

        private String user_id;

        private String restaurant_id;

        private String item_id;

        private String quantity;

        private String status;

        private String amount;

        private String image;

        private String name;

        private String updated_at;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setRestaurant_id(String restaurant_id){
            this.restaurant_id = restaurant_id;
        }
        public String getRestaurant_id(){
            return this.restaurant_id;
        }
        public void setItem_id(String item_id){
            this.item_id = item_id;
        }
        public String getItem_id(){
            return this.item_id;
        }
        public void setQuantity(String quantity){
            this.quantity = quantity;
        }
        public String getQuantity(){
            return this.quantity;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setUpdated_at(String updated_at){
            this.updated_at = updated_at;
        }
        public String getUpdated_at(){
            return this.updated_at;
        }
    }


}
