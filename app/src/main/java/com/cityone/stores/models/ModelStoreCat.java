package com.cityone.stores.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelStoreCat implements Serializable {

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

        private String name;

        private String created_at;

        private String update_at;

        private String restaurant_name;

        private String restaurant_image;

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
