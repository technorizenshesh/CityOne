package com.cityone.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelStoreBooking implements Serializable {

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

    public class Result implements Serializable {

        private String id;

        private String order_id;

        private String user_id;

        private String restaurant_id;

        private String total_amount;

        private String comment;

        private String cart_id;

        private String date;

        private String time;

        private String address;

        private String lat;

        private String lon;

        private String date_time;

        private String status;

        private String req_datetime;

        private String send_drivers;

        private String driver_id;

        private String accept_time;

        private String pickup_time;

        private String delivered_time;

        private String restaurant_name;

        private String restaurant_address;

        private String restaurant_image;

        private ArrayList<Item_data> item_data;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getOrder_id() {
            return this.order_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_id() {
            return this.user_id;
        }

        public void setRestaurant_id(String restaurant_id) {
            this.restaurant_id = restaurant_id;
        }

        public String getRestaurant_id() {
            return this.restaurant_id;
        }

        public void setTotal_amount(String total_amount) {
            this.total_amount = total_amount;
        }

        public String getTotal_amount() {
            return this.total_amount;
        }

        public void setCart_id(String cart_id) {
            this.cart_id = cart_id;
        }

        public String getCart_id() {
            return this.cart_id;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return this.date;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTime() {
            return this.time;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress() {
            return this.address;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLat() {
            return this.lat;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getLon() {
            return this.lon;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getDate_time() {
            return this.date_time;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public void setReq_datetime(String req_datetime) {
            this.req_datetime = req_datetime;
        }

        public String getReq_datetime() {
            return this.req_datetime;
        }

        public void setSend_drivers(String send_drivers) {
            this.send_drivers = send_drivers;
        }

        public String getSend_drivers() {
            return this.send_drivers;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String getDriver_id() {
            return this.driver_id;
        }

        public void setAccept_time(String accept_time) {
            this.accept_time = accept_time;
        }

        public String getAccept_time() {
            return this.accept_time;
        }

        public void setPickup_time(String pickup_time) {
            this.pickup_time = pickup_time;
        }

        public String getPickup_time() {
            return this.pickup_time;
        }

        public void setDelivered_time(String delivered_time) {
            this.delivered_time = delivered_time;
        }

        public String getDelivered_time() {
            return this.delivered_time;
        }

        public void setRestaurant_name(String restaurant_name) {
            this.restaurant_name = restaurant_name;
        }

        public String getRestaurant_name() {
            return this.restaurant_name;
        }

        public void setRestaurant_address(String restaurant_address) {
            this.restaurant_address = restaurant_address;
        }

        public String getRestaurant_address() {
            return this.restaurant_address;
        }

        public void setRestaurant_image(String restaurant_image) {
            this.restaurant_image = restaurant_image;
        }

        public String getRestaurant_image() {
            return this.restaurant_image;
        }

        public void setItem_data(ArrayList<Item_data> item_data) {
            this.item_data = item_data;
        }

        public ArrayList<Item_data> getItem_data() {
            return this.item_data;
        }

        public class Item_data implements Serializable {

            private String id;

            private String item_name;

            private String quantity;

            private String price;

            private String discount;

            private String item_image;

            private ArrayList<Extra_options> extra_options;

            public ArrayList<Extra_options> getExtra_options() {
                return extra_options;
            }

            public void setExtra_options(ArrayList<Extra_options> extra_options) {
                this.extra_options = extra_options;
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

            public void setItem_name(String item_name) {
                this.item_name = item_name;
            }

            public String getItem_name() {
                return this.item_name;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

            public String getQuantity() {
                return this.quantity;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getPrice() {
                return this.price;
            }

            public void setItem_image(String item_image) {
                this.item_image = item_image;
            }

            public String getItem_image() {
                return this.item_image;
            }

            public class Extra_options implements Serializable {

                private String id;

                private String restaurant_id;

                private String extra_item;

                private String extra_price;

                private String date_time;

                private String sub_admin_id;

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

                public void setSub_admin_id(String sub_admin_id) {
                    this.sub_admin_id = sub_admin_id;
                }

                public String getSub_admin_id() {
                    return this.sub_admin_id;
                }
            }

        }
    }


}
