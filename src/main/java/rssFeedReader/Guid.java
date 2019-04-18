package rssFeedReader;

public class Guid {
    private String isPermLink;
    private String value;

    public Guid(String isPermLink, String value) {
        this.isPermLink = isPermLink;
        this.value = value;
    }

    public String getIsPermLink() {
        return this.isPermLink;
    }

    public void setIsPermLink(String isPermLink) {
        this.isPermLink = isPermLink;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
