package com.example.firebase.model;

public class Client {
    private String id, name, address, city;

    public Client() {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
    }

    @Override
    public String toString() {
        return "Client {" +
                "id = '" + id + '\'' +
                ", name = '" + name + '\'' +
                ", address = '" + address + '\'' +
                ", city = '" + city + '\'' +
                '}';
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
