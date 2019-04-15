package hello;

import java.util.ArrayList;

public class HelloMessage {

    private ArrayList<String> url;

    public HelloMessage() {
    }

    public HelloMessage(ArrayList<String> url) {
        this.url = url;
    }

    public ArrayList<String> getUrL() {
        return url;
    }

    public void setUrl(ArrayList<String> url) {
        this.url = url;
    }
}
