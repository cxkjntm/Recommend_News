package us.codecraft.webmagic.sql;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

public class NewsInfo {

    private long ID;
    private String title;
    private String author;
    private String content;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void print(){
        System.out.println("---------------------------print newinfo start--------------------------------");
        System.out.println("author: "+ this.getAuthor()+"\ntitle: "+this.getTitle()+"\ncontents: "+this.getContent());
        System.out.println("---------------------------print newinfo over --------------------------------");
    }
    public boolean save(Statement statement) throws SQLException {
        String sql = "INSERT ignore INTO news(AUTHOR,TITLE,CONTENT) VALUES('"+this.getAuthor()+"','"+this.getTitle()+"','"+this.getContent()+"')";
        int flag = statement.executeUpdate(sql);
        if(flag<0) return false;
        return true;
    }
}
