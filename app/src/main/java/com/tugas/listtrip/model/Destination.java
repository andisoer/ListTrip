package com.tugas.listtrip.model;

public class Destination{

    private String id, name, description, location, ticket, photo, rating, view;

    public Destination() {
    }

    public Destination(String id, String name, String description, String location, String ticket, String photo, String rating, String view) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.ticket = ticket;
        this.photo = photo;
        this.rating = rating;
        this.view = view;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

}
