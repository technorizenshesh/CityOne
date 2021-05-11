package com.cityone.entertainment.movies.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.cityone.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class ModelMovieDetails implements Serializable {

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

        private String address;

        private String lat;

        private String lon;

        private String first_image;

        private String second_image;

        private String message;

        private String actor_name1;

        private String actor_name2;

        private String actor_name3;

        private String actor_name4;

        private String actor_name5;

        private String actor_image1;

        private String actor_image2;

        private String actor_image3;

        private String actor_image4;

        private String actor_image5;

        private String sart_time;

        private String end_time;

        private String treater_name;

        private String movie_lang_type;

        private String date_time;

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
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
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
        public void setFirst_image(String first_image){
            this.first_image = first_image;
        }
        public String getFirst_image(){
            return this.first_image;
        }
        public void setSecond_image(String second_image){
            this.second_image = second_image;
        }
        public String getSecond_image(){
            return this.second_image;
        }
        public void setMessage(String message){
            this.message = message;
        }
        public String getMessage(){
            return this.message;
        }
        public void setActor_name1(String actor_name1){
            this.actor_name1 = actor_name1;
        }
        public String getActor_name1(){
            return this.actor_name1;
        }
        public void setActor_name2(String actor_name2){
            this.actor_name2 = actor_name2;
        }
        public String getActor_name2(){
            return this.actor_name2;
        }
        public void setActor_name3(String actor_name3){
            this.actor_name3 = actor_name3;
        }
        public String getActor_name3(){
            return this.actor_name3;
        }
        public void setActor_name4(String actor_name4){
            this.actor_name4 = actor_name4;
        }
        public String getActor_name4(){
            return this.actor_name4;
        }
        public void setActor_name5(String actor_name5){
            this.actor_name5 = actor_name5;
        }
        public String getActor_name5(){
            return this.actor_name5;
        }
        public void setActor_image1(String actor_image1){
            this.actor_image1 = actor_image1;
        }
        public String getActor_image1(){
            return this.actor_image1;
        }
        public void setActor_image2(String actor_image2){
            this.actor_image2 = actor_image2;
        }
        public String getActor_image2(){
            return this.actor_image2;
        }
        public void setActor_image3(String actor_image3){
            this.actor_image3 = actor_image3;
        }
        public String getActor_image3(){
            return this.actor_image3;
        }
        public void setActor_image4(String actor_image4){
            this.actor_image4 = actor_image4;
        }
        public String getActor_image4(){
            return this.actor_image4;
        }
        public void setActor_image5(String actor_image5){
            this.actor_image5 = actor_image5;
        }
        public String getActor_image5(){
            return this.actor_image5;
        }
        public void setSart_time(String sart_time){
            this.sart_time = sart_time;
        }
        public String getSart_time(){
            return this.sart_time;
        }
        public void setEnd_time(String end_time){
            this.end_time = end_time;
        }
        public String getEnd_time(){
            return this.end_time;
        }
        public void setTreater_name(String treater_name){
            this.treater_name = treater_name;
        }
        public String getTreater_name(){
            return this.treater_name;
        }
        public void setMovie_lang_type(String movie_lang_type){
            this.movie_lang_type = movie_lang_type;
        }
        public String getMovie_lang_type(){
            return this.movie_lang_type;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.default_profile_icon)
                .into(view);
    }

}
