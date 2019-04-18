package rssFeedReader;

import java.util.ArrayList;

public class RssFeedReaderService {

    private ArrayList<Channel> channel;

    public RssFeedReaderService(ArrayList<Channel> channel) {
        this.channel = channel;
    }

    public RssFeedReaderService() {
        channel = new ArrayList<Channel>();
    }

    public void addChannel(Channel channel) {
        this.channel.add(channel);
    }

    public ArrayList<Channel> getChannel() {
        return channel;
    }

}
