package com.cityone.entertainment.movies.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelAvilTheater implements Serializable {

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

        private String movie_id;

        private String name;

        private String address;

        private String treater_start_date;

        private String treater_end_date;

        private String treater_time_slote;

        private String treater_image;

        private String seat;

        private String date_time;

        private String sub_admin_id;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setMovie_id(String movie_id){
            this.movie_id = movie_id;
        }
        public String getMovie_id(){
            return this.movie_id;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setTreater_start_date(String treater_start_date){
            this.treater_start_date = treater_start_date;
        }
        public String getTreater_start_date(){
            return this.treater_start_date;
        }
        public void setTreater_end_date(String treater_end_date){
            this.treater_end_date = treater_end_date;
        }
        public String getTreater_end_date(){
            return this.treater_end_date;
        }
        public void setTreater_time_slote(String treater_time_slote){
            this.treater_time_slote = treater_time_slote;
        }
        public String getTreater_time_slote(){
            return this.treater_time_slote;
        }
        public void setTreater_image(String treater_image){
            this.treater_image = treater_image;
        }
        public String getTreater_image(){
            return this.treater_image;
        }
        public void setSeat(String seat){
            this.seat = seat;
        }
        public String getSeat(){
            return this.seat;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setSub_admin_id(String sub_admin_id){
            this.sub_admin_id = sub_admin_id;
        }
        public String getSub_admin_id(){
            return this.sub_admin_id;
        }
    }

    @BindingAdapter("imageurl")
    public static void loadImage(ImageView imageView,String url){
        Picasso.get().load(url).into(imageView);
    }

}
