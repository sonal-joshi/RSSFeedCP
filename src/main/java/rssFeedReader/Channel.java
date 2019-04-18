package rssFeedReader;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    // feed class
    public String title;
    public String description;
    public String language;
    public String copyright;
    public String lastBuildDate;
    public String pubDate;
    public Image image;
    public ArrayList<Feed> entries;


    public Channel(String title, String description, String language,
                   String copyright, String lastBuildDate, String pubDate, Image image) {
        this.title = title;
        this.description = description;
        this.language = language;
        this.copyright = copyright;
        this.lastBuildDate = lastBuildDate;
        this.image = image;
        this.pubDate = pubDate;
        this.entries = new ArrayList<Feed>();
    }

    public Channel(String title, String description, String language,
                   String copyright, String lastBuildDate, String pubDate, Image image, ArrayList<Feed> entries) {
        this.title = title;
        this.description = description;
        this.language = language;
        this.copyright = copyright;
        this.lastBuildDate = lastBuildDate;
        this.image = image;
        this.pubDate = pubDate;
        this.entries = entries;
    }


    public List<Feed> getEntries() {
        return entries;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }
}
