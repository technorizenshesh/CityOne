package com.cityone.models;

import java.io.Serializable;

public class ModelLogin implements Serializable {

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

        private String type;

        private String user_name;

        private String mobile;

        private String email;

        private String password;

        private String image;

        private String dob;

        private String social_id;

        private String token;

        private String lat;

        private String lon;

        private String address;

        private String land_mark;

        private String my_referral_code;

        private String gender;

        private String category_id;

        private String online_by;

        private String first_name;

        private String rating;

        private String last_name;

        private String register_id;

        private String ios_register_id;

        private String status;

        private String online_status;

        private String date_time;
        
        private String wallet_amount;

        public String getLand_mark() {
            return land_mark;
        }

        public void setLand_mark(String land_mark) {
            this.land_mark = land_mark;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getWallet_amount() {
            return wallet_amount;
        }

        public void setWallet_amount(String wallet_amount) {
            this.wallet_amount = wallet_amount;
        }

        public String getMy_referral_code() {
            return my_referral_code;
        }

        public void setMy_referral_code(String my_referral_code) {
            this.my_referral_code = my_referral_code;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setUser_name(String user_name){
            this.user_name = user_name;
        }
        public String getUser_name(){
            return this.user_name;
        }
        public void setMobile(String mobile){
            this.mobile = mobile;
        }
        public String getMobile(){
            return this.mobile;
        }
        public void setEmail(String email){
            this.email = email;
        }
        public String getEmail(){
            return this.email;
        }
        public void setPassword(String password){
            this.password = password;
        }
        public String getPassword(){
            return this.password;
        }
        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
        public void setDob(String dob){
            this.dob = dob;
        }
        public String getDob(){
            return this.dob;
        }
        public void setSocial_id(String social_id){
            this.social_id = social_id;
        }
        public String getSocial_id(){
            return this.social_id;
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
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setGender(String gender){
            this.gender = gender;
        }
        public String getGender(){
            return this.gender;
        }
        public void setCategory_id(String category_id){
            this.category_id = category_id;
        }
        public String getCategory_id(){
            return this.category_id;
        }
        public void setOnline_by(String online_by){
            this.online_by = online_by;
        }
        public String getOnline_by(){
            return this.online_by;
        }
        public void setFirst_name(String first_name){
            this.first_name = first_name;
        }
        public String getFirst_name(){
            return this.first_name;
        }
        public void setRating(String rating){
            this.rating = rating;
        }
        public String getRating(){
            return this.rating;
        }
        public void setLast_name(String last_name){
            this.last_name = last_name;
        }
        public String getLast_name(){
            return this.last_name;
        }
        public void setRegister_id(String register_id){
            this.register_id = register_id;
        }
        public String getRegister_id(){
            return this.register_id;
        }
        public void setIos_register_id(String ios_register_id){
            this.ios_register_id = ios_register_id;
        }
        public String getIos_register_id(){
            return this.ios_register_id;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setOnline_status(String online_status){
            this.online_status = online_status;
        }
        public String getOnline_status(){
            return this.online_status;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
    }

}
