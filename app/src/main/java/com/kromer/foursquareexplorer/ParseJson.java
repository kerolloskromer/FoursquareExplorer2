package com.kromer.foursquareexplorer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class ParseJson {
    public static VenuesModel getAllVenues(String response) {

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<VenuesModel>() {
            }.getType();
            return gson.fromJson(response, type);
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public static VenueModel getVenue(String response) {

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<VenueModel>() {
            }.getType();
            return gson.fromJson(response, type);
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

}
