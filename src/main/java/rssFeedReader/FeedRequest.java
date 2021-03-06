package rssFeedReader;

import java.util.ArrayList;

public class FeedRequest {

    private ArrayList<String> url;
    private String mode;

    public FeedRequest() {
    }

    public FeedRequest(ArrayList<String> url, String mode) {
        this.url = url;
        this.mode = mode;
    }

    public ArrayList<String> getUrL() {
        return url;
    }

    public void setUrl(ArrayList<String> url) {
        this.url = url;
    }

    public String getMode() {
        return this.mode;
    }
}
