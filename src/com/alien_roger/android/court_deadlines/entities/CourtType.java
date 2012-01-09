package com.alien_roger.android.court_deadlines.entities;

/**
 * CourtType class
 *
 * @author alien_roger
 * @created at: 27.12.11 0:13
 */
public class CourtType {
    private int level = 0;
    private String body = "";
    private String parent = "";
    private String link = "";

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}
