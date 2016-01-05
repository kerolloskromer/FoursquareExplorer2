package com.kromer.foursquareexplorer;

import java.util.Calendar;


public class URls {
    private static final String baseURL = "https://api.foursquare.com/v2/venues/";

    public static String getVenues(String oauth_token, String Lat, String Long) {

        return baseURL + "search?ll=" + Lat + "," + Long + "&oauth_token=" + oauth_token + "&v=" + getCurrentDate();
    }

    public static String getVenueDetails(String VenueID, String oauth_token) {
        return baseURL + VenueID + "?oauth_token=" + oauth_token + "&v=" + getCurrentDate();
    }




    private static String zeroString(int num) {
        String str = "";
        if (num < 10) {
            str = "0" + String.valueOf(num);
        } else {
            str = String.valueOf(num);
        }

        return str;
    }

    private static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(year) + zeroString(month) + zeroString(day);
    }
}
