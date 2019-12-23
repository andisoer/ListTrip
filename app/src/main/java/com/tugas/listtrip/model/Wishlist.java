package com.tugas.listtrip.model;

public class Wishlist {

    private String id, id_user, id_destination, name, view, rating, photo, ticket;

    public Wishlist() {
    }

    public Wishlist(String id, String id_user, String id_destination, String name, String view, String rating, String photo, String ticket) {
        this.id = id;
        this.id_user = id_user;
        this.id_destination = id_destination;
        this.name = name;
        this.view = view;
        this.rating = rating;
        this.photo = photo;
        this.ticket = ticket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_destination() {
        return id_destination;
    }

    public void setId_destination(String id_destination) {
        this.id_destination = id_destination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
