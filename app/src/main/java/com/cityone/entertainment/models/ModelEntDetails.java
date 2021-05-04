package com.cityone.entertainment.models;

import java.io.Serializable;

public class ModelEntDetails implements Serializable {

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

        private String entertentment_category_id;

        private String phone;

        private String country;

        private String state;

        private String city;

        private String sub_admin_id;

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
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setEntertentment_category_id(String entertentment_category_id){
            this.entertentment_category_id = entertentment_category_id;
        }
        public String getEntertentment_category_id(){
            return this.entertentment_category_id;
        }
        public void setPhone(String phone){
            this.phone = phone;
        }
        public String getPhone(){
            return this.phone;
        }
        public void setCountry(String country){
            this.country = country;
        }
        public String getCountry(){
            return this.country;
        }
        public void setState(String state){
            this.state = state;
        }
        public String getState(){
            return this.state;
        }
        public void setCity(String city){
            this.city = city;
        }
        public String getCity(){
            return this.city;
        }
        public void setSub_admin_id(String sub_admin_id){
            this.sub_admin_id = sub_admin_id;
        }
        public String getSub_admin_id(){
            return this.sub_admin_id;
        }
    }

}
