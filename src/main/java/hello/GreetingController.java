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

@Controller
public class GreetingController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        //return sequentialFeedReader(message);
    	//return parallelFeedReaderwithThreadPool(message);
        return parallelFeedReaderwithThreads(message);
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

    public Greeting parallelFeedReaderwithThreads(HelloMessage message) {
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

    public void fireGreeting() {
        System.out.println("Fire");
        ArrayList<Channel> result = new ArrayList<Channel>();
        Channel channel = new Channel("Test", "This is new channel", "en-US", "Tester", "Sat, 14 April 2019", "Sat, 14 April 2019", null);
        result.add(channel);
        this.template.convertAndSend("/topic/greetings", new Greeting(result));
    }
}

