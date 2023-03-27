package com.cityone.stores.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelStoreItems implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;

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

    public class Result {
        
        private String id;

        private String restaurant_id;

        private String restaurant_category_id;

        private String restaurant_sub_category_id;

        private String amount;

        private String image;

        private String created_at;

        private String update_at;

        private String name;

        private String discount;

        private String item_description;

        private String additional_discount;

        private String product_point;

        private String restaurant_sub_category_name;

        private String restaurant_category_name;

        private String restaurant_name;

        private String restaurant_image;

        private ArrayList<Extra_options_item> extra_options_item;

        public String getProduct_point() {
            return product_point;
        }

        public void setProduct_point(String product_point) {
            this.product_point = product_point;
        }

        public ArrayList<Extra_options_item> getExtra_options_item() {
            return extra_options_item;
        }

        public String getItem_description() {
            return item_description;
        }

        public void setItem_description(String item_description) {
            this.item_description = item_description;
        }

        public String getAdditional_discount() {
            return additional_discount;
        }

        public void setAdditional_discount(String additional_discount) {
            this.additional_discount = additional_discount;
        }

        public void setExtra_options_item(ArrayList<Extra_options_item> extra_options_item) {
            this.extra_options_item = extra_options_item;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setRestaurant_id(String restaurant_id) {
            this.restaurant_id = restaurant_id;
        }

        public String getRestaurant_id() {
            return this.restaurant_id;
        }

        public void setRestaurant_category_id(String restaurant_category_id) {
            this.restaurant_category_id = restaurant_category_id;
        }

        public String getRestaurant_category_id() {
            return this.restaurant_category_id;
        }

        public void setRestaurant_sub_category_id(String restaurant_sub_category_id) {
            this.restaurant_sub_category_id = restaurant_sub_category_id;
        }

        public String getRestaurant_sub_category_id() {
            return this.restaurant_sub_category_id;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAmount() {
            return this.amount;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getImage() {
            return this.image;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getCreated_at() {
            return this.created_at;
        }

        public void setUpdate_at(String update_at) {
            this.update_at = update_at;
        }

        public String getUpdate_at() {
            return this.update_at;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setRestaurant_sub_category_name(String restaurant_sub_category_name) {
            this.restaurant_sub_category_name = restaurant_sub_category_name;
        }

        public String getRestaurant_sub_category_name() {
            return this.restaurant_sub_category_name;
        }

        public void setRestaurant_category_name(String restaurant_category_name) {
            this.restaurant_category_name = restaurant_category_name;
        }

        public String getRestaurant_category_name() {
            return this.restaurant_category_name;
        }

        public void setRestaurant_name(String restaurant_name) {
            this.restaurant_name = restaurant_name;
        }

        public String getRestaurant_name() {
            return this.restaurant_name;
        }

        public void setRestaurant_image(String restaurant_image) {
            this.restaurant_image = restaurant_image;
        }

        public String getRestaurant_image() {
            return this.restaurant_image;
        }

        public class Extra_options_item implements Serializable{
            private String id;

            private String restaurant_id;

            private String extra_item;

            private String extra_price;

            private String date_time;
            
            private boolean isChecked;
            
            public boolean isChecked() {
                return isChecked;
            }

            public void setChecked(boolean checked) {
                isChecked = checked;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getId() {
                return this.id;
            }

            public void setRestaurant_id(String restaurant_id) {
                this.restaurant_id = restaurant_id;
            }

            public String getRestaurant_id() {
                return this.restaurant_id;
            }

            public void setExtra_item(String extra_item) {
                this.extra_item = extra_item;
            }

            public String getExtra_item() {
                return this.extra_item;
            }

            public void setExtra_price(String extra_price) {
                this.extra_price = extra_price;
            }

            public String getExtra_price() {
                return this.extra_price;
            }

            public void setDate_time(String date_time) {
                this.date_time = date_time;
            }

            public String getDate_time() {
                return this.date_time;
            }
        }

    }

}
