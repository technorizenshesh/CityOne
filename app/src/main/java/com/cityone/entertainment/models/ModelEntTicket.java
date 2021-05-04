package com.cityone.entertainment.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelEntTicket implements Serializable {

    private ArrayList<Result> result;
    private String status;
    private String message;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
        return this.result;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }

    public class Result
    {
        private String id;

        private String name;

        private String image;

        private String price;

        private String description;

        private String date_time;

        private String entertentment_category_id;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
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
    }


}
