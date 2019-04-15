package hello;

import java.util.ArrayList;

public class Greeting {

    private ArrayList<Channel> channel;

    public Greeting(ArrayList<Channel> channel) {
        this.channel = channel;
    }

    public Greeting() {
        channel = new ArrayList<Channel>();
    }

    public void addChannel(Channel channel) {
        this.channel.add(channel);
    }

    public ArrayList<Channel> getChannel() {
        return channel;
    }

}
