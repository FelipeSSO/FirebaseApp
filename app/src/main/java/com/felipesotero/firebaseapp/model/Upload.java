package com.felipesotero.firebaseapp.model;

import java.io.Serializable;

public class Upload implements Serializable {
    private String id;
    private String nomeImagem;
    private String url;

    // Firebase utiliza esse construtor -> envia dados
    public Upload(){

    }
    public Upload(String id, String nomeImagem, String url) {
        this.id = id;
        this.nomeImagem = nomeImagem;
        this.url = url;
    }

    public String getId() {
        return id;
    }
    public String getNomeImagem() {
        return nomeImagem;
    }
    public String getUrl() {
        return url;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setNomeImagem(String nomeImagem) {
        this.nomeImagem = nomeImagem;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
