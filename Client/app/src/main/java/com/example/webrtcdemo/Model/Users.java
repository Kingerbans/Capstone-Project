package com.example.webrtcdemo.Model;

public class Users {
    private String id;
    private String email;
    private String fullname;

    public Users() {
    }

    public Users(String id, String email, String fullname) {
        this.id = id;
        this.email = email;
        this.fullname = fullname;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullname) {
        this.fullname = fullname;
    }

}
