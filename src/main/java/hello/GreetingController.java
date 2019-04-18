package hello;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Controller
public class GreetingController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        //return sequentialFeedReader(message);
        return parallelFeedReaderwithThreadPool(message);
        //return parallelFeedReaderwithThreads(message);
        //	return forkjoinprinciple(message);
        //return divideWorkAmongThreads(message);
    }

    public Greeting sequentialFeedReader(HelloMessage message) throws Exception {
        FeedReader parser = new FeedReader();
        Runnable thread = new FeedReader(new ArrayList<>(Arrays.asList(message.getUrL().get(0))));
        thread.run();
        Thread.sleep(1000); // simulated delay
        FireGreeting r = new FireGreeting(this);
        new Thread(r).start();
        ArrayList<Channel> out = new ArrayList<Channel>(parser.getlist());
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
            Runnable thread = new FeedReader(new ArrayList<>(Arrays.asList(message.getUrL().get(i))));
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
    	int nThreads=Runtime.getRuntime().availableProcessors();
    	 ForkJoinPool forkJoinPool = new ForkJoinPool(nThreads);
    	 System.out.println("number of threads"+nThreads);
    	 forkJoinPool.invoke(new ForkJoinReader(message.getUrL(),0,message.getUrL().size()));
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

    public Greeting divideWorkAmongThreads(HelloMessage message) {
        int procs = Runtime.getRuntime().availableProcessors();
        ExecutorService es = Executors.newFixedThreadPool(procs);

        int tasks = message.getUrL().size();
        int overflow = tasks % procs;
        List<Future<?>> futures = new ArrayList<>();

        int remainder = tasks % procs;
        int defaultBlockSize = Math.floorDiv(tasks, procs);
        int end = 0;
        int start = 0;
        int numberofTasks = 0;
        FeedReader parser = new FeedReader();
        for (int i = 1; i <= procs; i++) {
            if (i <= remainder)
                numberofTasks = defaultBlockSize + 1;
            else
                numberofTasks = defaultBlockSize;
            start = end > 0 ? end + 1 : 0;
            end = end + numberofTasks - 1;
            System.out.println("New thread Range:" + start + " end : " + end);
            ArrayList<String> urls = new ArrayList<>(message.getUrL().subList(start - 1, end - 1));
            parser = new FeedReader(urls);
            futures.add(es.submit(parser));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        ArrayList<Channel> output = new ArrayList<Channel>(parser.getlist());
        return new Greeting(output);
    }
}

