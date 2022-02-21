package com.example.finalproject_diaryapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Diary implements Serializable{

    public static final int TYPE_GRID= 1;
    public static final int TYPE_LIST= 2;
    public static final int TYPE_STAGGERED= 3;

    private String id;
    private String title;
    private String date;
    private String content;
    private String typeDisplay;

    public Diary(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTypeDisplay() {
        return typeDisplay;
    }

    public void setTypeDisplay(String typeDisplay) {
        this.typeDisplay = typeDisplay;
    }

    public Diary(String id,String title,String date,String content,String typeDisplay) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
        this.typeDisplay = typeDisplay;
    }
    public String toString(){
        return "Diary{"+
                "id="+id +
                ", title='" + title+'\''+
                ", date='" + date+'\''+
                ", content='" + content+'\''+
                ", typeDisplay='" + typeDisplay+'\''+
                "}";
    }
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("title",title);
        result.put("date",date);
        result.put("content",content);
        result.put("typeDisplay",typeDisplay);
        return result;


    }
}
