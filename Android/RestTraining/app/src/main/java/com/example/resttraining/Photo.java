package com.example.resttraining;

public class Photo {
   private String url,urlThumb,title;
   private int id,albumId;
    public Photo(int id,int albumId,String url,String urlThumb,String title){
        this.title=title;
        this.albumId=albumId;
        this.id=id;
        this.url=url;
        this.urlThumb=urlThumb;

    }

    public int getId(){
        return id;
    }
    public int getAlbumId(){
        return albumId;
    }
    public String getTitle(){
        return title;
    }
    public String getUrlThumb(){
        return urlThumb;
    }
    public String getUrl(){
        return url;
    }


    public void setId(){
        this.id=id;
    }
    public void setAlbumId(){
        this.albumId=albumId;
    }
    public void setTitle(){
        this.title=title;
    }
    public void setUrlThumb(){
        this.urlThumb=urlThumb;
    }
    public void setUrl(){
        this.url=url;
    }


}
