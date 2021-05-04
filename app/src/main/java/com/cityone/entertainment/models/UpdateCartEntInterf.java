package com.cityone.entertainment.models;

import java.util.ArrayList;

public interface UpdateCartEntInterf {
    void onSuccess(ArrayList<ModelEntMyCart.Result> itemsList);
    void onSuccess();
}
