package hello;

public class FeedUpdateTask implements Runnable {

    String url;
    FeedReader parser;

    public FeedUpdateTask(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        // get urls and get feeds using multiple threads and display
        parser = new FeedReader(url);
        Channel channel = parser.fetchFeed();
    }
}
