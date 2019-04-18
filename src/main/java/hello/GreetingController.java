package hello;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

@Controller
public class GreetingController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        //return sequentialFeedReader(message);
    	//return parallelFeedReaderwithThreadPool(message);
    	return forkjoinprinciple(message);
        //return parallelFeedReaderwithThreads(message);
    }

    public Greeting sequentialFeedReader(HelloMessage message) throws Exception {
        FeedReader parser = new FeedReader(HtmlUtils.htmlEscape(message.getUrL().get(0)));
        Channel channel = parser.fetchFeed();
        Thread.sleep(1000); // simulated delay
        FireGreeting r = new FireGreeting(this);
        new Thread(r).start();
        ArrayList<Channel> out = new ArrayList<Channel>();
        out.add(channel);
        return new Greeting(out);
    }

    public Greeting parallelFeedReaderwithThreads(HelloMessage message) throws Exception {
        FeedReader parser = new FeedReader();
        ArrayList<Channel> output = new ArrayList<Channel>();

        FeedUpdateTask feedupdatetask[] = new FeedUpdateTask[message.getUrL().size()];
        Thread task[] = new Thread[message.getUrL().size()];
        for (int i = 0; i < message.getUrL().size(); i++) {
            feedupdatetask[i] = new FeedUpdateTask(message.getUrL().get(i));
            task[i] = new Thread(feedupdatetask[i]);
            task[i].start();
            //call feed update after certain time interval
        }
        Thread.sleep(1000);
        FireGreeting r = new FireGreeting(this);
        new Thread(r).start();
        for (int i = 0; i < message.getUrL().size(); i++) {
            try {
                task[i].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        output = parser.getlist();
        return new Greeting(output);
    }
    public Greeting parallelFeedReaderwithThreadPool(HelloMessage message) {
        FeedReader parser = new FeedReader();
        ArrayList<Channel> output = new ArrayList<Channel>();
        ExecutorService exec=Executors.newFixedThreadPool(3);
        for(int i=0;i<message.getUrL().size();i++) {
            Runnable thread=new FeedReader(message.getUrL().get(i));
            exec.execute(thread);

        }
        exec.shutdown();
        while(!exec.isTerminated()) {

        }
        output=parser.getlist();
        return new Greeting(output);

    }
    
    public Greeting forkjoinprinciple(HelloMessage message) {
    	ArrayList<Channel> output = new ArrayList<Channel>();
    	ArrayList<String> input =new ArrayList<String>();
    	input.add("http://rss.cnn.com/rss/cnn_topstories.rss");
    	input.add("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml");
    	input.add("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml");
    	input.add("http://rss.cnn.com/rss/cnn_topstories.rss");
    	input.add("https://www.codelitt.com/blog/rss");
    	input.add("https://technology.condenast.com/feed/rss");
    	input.add("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml");
    	input.add("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml");
    	input.add("http://rss.cnn.com/rss/cnn_health.rss");
    	input.add("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml");
    	int nThreads=Runtime.getRuntime().availableProcessors();
    	 ForkJoinPool forkJoinPool = new ForkJoinPool(nThreads);
    	 System.out.println("number of threads"+nThreads);
    	 forkJoinPool.invoke(new ForkJoinReader(input,0,input.size()));
    	 ForkJoinReader forkjoinreader=new ForkJoinReader();
    	 output=forkjoinreader.getList();
    	return new Greeting(output);
    }

    public void fireGreeting() {
        System.out.println("Fire");
        ArrayList<Channel> result = new ArrayList<Channel>();
        ArrayList<Feed> resultFeeds = new ArrayList<Feed>();
        resultFeeds.add(new Feed("title", "description", "pubDate", "link", new Guid("true", "guid")));
        Channel channel = new Channel("Test", "This is new channel", "en-US", "Tester", "Sat, 14 April 2019", "Sat, 14 April 2019", null, resultFeeds);
        result.add(channel);
        //   this.template.convertAndSend("/topic/greetings", new Greeting(result));
    }
}

