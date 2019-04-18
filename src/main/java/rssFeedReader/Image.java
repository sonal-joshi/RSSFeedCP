package rssFeedReader;

public class Image {
    public String title;
    public String url;
    public String link;

    public Image(String title, String url, String link) {
        this.title = title;
        this.url = url;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
