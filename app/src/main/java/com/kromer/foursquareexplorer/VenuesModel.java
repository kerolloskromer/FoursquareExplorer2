package com.kromer.foursquareexplorer;

import java.util.ArrayList;

import br.com.condesales.models.Location;
import br.com.condesales.models.Venue;

public class VenuesModel {

    private Meta meta;
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public class Meta {
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        private int code;

    }

    public class Response {

        private boolean confident;
        private ArrayList<Venue> venues;

        public ArrayList<Venue> getVenues() {
            return venues;
        }

        public void setVenues(ArrayList<Venue> venues) {
            this.venues = venues;
        }

        public boolean isConfident() {
            return confident;
        }

        public void setConfident(boolean confident) {
            this.confident = confident;
        }
    }


}
