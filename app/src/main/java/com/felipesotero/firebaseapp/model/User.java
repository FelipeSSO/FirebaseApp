package com.felipesotero.firebaseapp.model;

public class User {
    private String id;
    private String email;
    private String nome;
    private String photoUrl;
    private boolean receiveRequest;

    public User(){
    }
    public User(String id, String email, String nome) {
        this.id = id;
        this.email = email;
        this.nome = nome;
    }

    public String getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getNome() {
        return nome;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public boolean getReceiveRequest() {
        return receiveRequest;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public void setReceiveRequest(boolean b) {
        this.receiveRequest = b;
    }

    public boolean equals(User u){
        return this.id.equals(u.getId());
    }

}
