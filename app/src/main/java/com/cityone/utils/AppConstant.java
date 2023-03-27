package com.cityone.utils;

public interface AppConstant {

//  5306 9173 5617 4743, 08/27, 402
//  debit card
//  Yolanda Rozo López

    String BASE_URL = "http://city-one.co/webservice/";
  //String BASE_URL = "https://city-one.co/Cityone/webservice/";
    String IMAGE_URL = "https://www.pickpock.net/uploads/images/";

    String PAY_ADD_CARD = "http://city-one.co/webservice/add_card?";
    String PAY_DELETE_CARD = "http://city-one.co/webservice/delete_card?";
    String PAY_GET_ALL_CARD = "http://city-one.co/webservice/card_list?";
    String PAY_PAYMENT_API = "http://city-one.co/webservice/global_payment?";

    String LOGIN_API = "login";

    public static String USER_ID = "uid12345";

    //Email of the user initiating the purchase.
    public static String USER_EMAIL = "dev@paymentez.com";

    //Is Redeban SDK DEV environment?
    public static boolean REDEBAN_IS_TEST_MODE = true;

    //Ask the Redeban team for it

    public static String REDEBAN_CLIENT_APP_CODE = "WINGROUP-GLP-CLIENT";

    //Ask the Redeban team for it.
    public static String REDEBAN_CLIENT_APP_KEY = "DhhtyQUW6u1nHpX840abu5GuCOgCIl";

    //Backend Deployed from https://github.com/globalpayredeban/example-java-backend
    public static String BACKEND_URL = "https://example-redeban-backend-v2.herokuapp.com/";

    String STORE_BOOKING_PARAMS = "bookingparam";
    String SIGNUP_API = "signup";
    String EURO = "€";
    String EXCLUSIVE = "exclusive";
    String NORMAL = "normal";
    String CLASSIC = "classic";
    String CURRENCY = "COP";
    String UPDATE_PROFILE_API = "update_profile";
    String FORGOT_PASSWORD_API = "forgot_password";
    String CHANGE_PASSWORD_API = "change_password";

    String IS_FILTER = "is_filter";
    String IS_SEARCH = "is_search";
    String SEARCH_DATA = "search_data";
    String DOLLAR = "COP ";
    String FILTER_SELECTED_DATA = "filter_selected_item";

    String IS_REGISTER = "user_register";
    String USER_DETAILS = "user_details";
    String USER = "USER";
    String LAT_LON_LIST = "latlonlist";
    String PROVIDER = "PROVIDER";
    String UPDATE_ORDER_STATUS = "update_order_status";

}



