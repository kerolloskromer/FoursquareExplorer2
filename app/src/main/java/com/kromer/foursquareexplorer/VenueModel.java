package com.kromer.foursquareexplorer;

import br.com.condesales.models.Venue;


public class VenueModel {
    private VenuesModel.Meta meta;
    private Response response;

    public VenuesModel.Meta getMeta() {
        return meta;
    }

    public void setMeta(VenuesModel.Meta meta) {
        this.meta = meta;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public class Response {
        private Venue venue;

        public Venue getVenue() {
            return venue;
        }

        public void setVenue(Venue venue) {
            this.venue = venue;
        }
    }


}
