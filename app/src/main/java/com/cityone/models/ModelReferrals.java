package com.cityone.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class ModelReferrals implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;
    private String referral_code_point;

    public String getReferral_code_point() {
        return referral_code_point;
    }

    public void setReferral_code_point(String referral_code_point) {
        this.referral_code_point = referral_code_point;
    }

    public void setResult(ArrayList<Result> result) {
        this.result = result;
    }

    public ArrayList<Result> getResult() {
        return this.result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public class Result implements Serializable {
        private String id;

        private String my_referral_code;

        private String other_referral_code;

        private String referral_code_point;

        private String date_time;

        private String user_name;

        private String image;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setMy_referral_code(String my_referral_code) {
            this.my_referral_code = my_referral_code;
        }

        public String getMy_referral_code() {
            return this.my_referral_code;
        }

        public void setOther_referral_code(String other_referral_code) {
            this.other_referral_code = other_referral_code;
        }

        public String getOther_referral_code() {
            return this.other_referral_code;
        }

        public void setReferral_code_point(String referral_code_point) {
            this.referral_code_point = referral_code_point;
        }

        public String getReferral_code_point() {
            return this.referral_code_point;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getDate_time() {
            return this.date_time;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_name() {
            return this.user_name;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getImage() {
            return this.image;
        }
    }


}
