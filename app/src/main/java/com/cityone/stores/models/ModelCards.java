package com.cityone.stores.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelCards implements Serializable {

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

        private String user_id;

        private String card_number;

        private String expiry_date;

        private String expiry_month;

        private String cvc_code;

        private String cust_id;

        private String holder_name;

        private String date_time;

        public String getHolder_name() {
            return holder_name;
        }

        public void setHolder_name(String holder_name) {
            this.holder_name = holder_name;
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
        public void setCard_number(String card_number){
            this.card_number = card_number;
        }
        public String getCard_number(){
            return this.card_number;
        }
        public void setExpiry_date(String expiry_date){
            this.expiry_date = expiry_date;
        }
        public String getExpiry_date(){
            return this.expiry_date;
        }
        public void setExpiry_month(String expiry_month){
            this.expiry_month = expiry_month;
        }
        public String getExpiry_month(){
            return this.expiry_month;
        }
        public void setCvc_code(String cvc_code){
            this.cvc_code = cvc_code;
        }
        public String getCvc_code(){
            return this.cvc_code;
        }
        public void setCust_id(String cust_id){
            this.cust_id = cust_id;
        }
        public String getCust_id(){
            return this.cust_id;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
    }

}
