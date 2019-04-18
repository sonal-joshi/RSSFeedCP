package rssFeedReader;

import java.util.ArrayList;
import java.util.Arrays;

public class FeedUpdateTask implements Runnable {

    String url;
    FeedReader parser;

    public FeedUpdateTask(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        // get urls and get feeds using multiple threads and display
        parser = new FeedReader(new ArrayList<>(Arrays.asList(url)));
        parser.run();
    }
}
