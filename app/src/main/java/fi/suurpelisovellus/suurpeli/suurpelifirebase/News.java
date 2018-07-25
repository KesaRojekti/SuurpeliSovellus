package fi.suurpelisovellus.suurpeli.suurpelifirebase;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class News {

    private String title;
    private String content;
    private String author;

    public News(){
    }

    public News(String author, String content){
        this.author = author;
        this.content = content;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public String getAuthor(){
        return this.author;
    }

    public String getContent(){
        return this.content;
    }
}
