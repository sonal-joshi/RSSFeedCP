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
        switch (message.getMode()) {
            case "0":
                return sequentialFeedReader(message);
            case "1":
                return parallelFeedReaderwithThreads(message);
            case "2":
                return parallelFeedReaderwithThreadPool(message);
            case "3":
                return forkjoinprinciple(message);
            case "4":
                return divideWorkAmongThreads(message);
            default:
                return null;
        }
    }

    public Greeting sequentialFeedReader(HelloMessage message) throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("timer start " + start);
        FeedReader feedReader = new FeedReader(message.getUrL());
        feedReader.run();
        ArrayList<Channel> out = feedReader.getlist();
        long end = System.currentTimeMillis();
        System.out.println("timer end " + end);
        long diff = end - start;
        System.out.println("total time " + diff);
        return new Greeting(out);
    }

    public Greeting parallelFeedReaderwithThreads(HelloMessage message) throws Exception {
        FeedReader parser = new FeedReader();
        ArrayList<Channel> output = new ArrayList<Channel>();
        long start = System.currentTimeMillis();
        System.out.println("timer start " + start);
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
        long end = System.currentTimeMillis();
        System.out.println("timer end " + end);
        long diff = end - start;
        System.out.println("total time " + diff);
        return new Greeting(output);
    }
    public Greeting parallelFeedReaderwithThreadPool(HelloMessage message) {
        FeedReader parser = new FeedReader();
        long start = System.currentTimeMillis();
        System.out.println("timer start " + start);
        ArrayList<Channel> output = new ArrayList<Channel>();
        System.out.println("timer start "+ System.currentTimeMillis());
        ExecutorService exec=Executors.newFixedThreadPool(3);
        for(int i=0;i<message.getUrL().size();i++) {
            Runnable thread = new FeedReader(new ArrayList<>(Arrays.asList(message.getUrL().get(i))));
            exec.execute(thread);

        }
        exec.shutdown();
        while(!exec.isTerminated()) {

        }
        output=parser.getlist();
        long end = System.currentTimeMillis();
        System.out.println("timer end " + end);
        long diff = end - start;
        System.out.println("total time " + diff);
        return new Greeting(output);

    }

    public Greeting forkjoinprinciple(HelloMessage message) {
    	ArrayList<Channel> output = new ArrayList<Channel>();
        long start = System.currentTimeMillis();
        System.out.println("timer start " + start);
    	int nThreads=Runtime.getRuntime().availableProcessors();
    	 System.out.println("timer start "+ System.currentTimeMillis());
    	 ForkJoinPool forkJoinPool = new ForkJoinPool(nThreads);
    	 System.out.println("number of threads"+nThreads);
    	 forkJoinPool.invoke(new ForkJoinReader(message.getUrL(),0,message.getUrL().size()));
    	 ForkJoinReader forkjoinreader=new ForkJoinReader();
    	 output=forkjoinreader.getList();
        long end = System.currentTimeMillis();
        System.out.println("timer end " + end);
        long diff = end - start;
        System.out.println("total time " + diff);
    	return new Greeting(output);
    }


    public Greeting divideWorkAmongThreads(HelloMessage message) {
        int procs = Runtime.getRuntime().availableProcessors();
        long startTime = System.currentTimeMillis();
        System.out.println("timer start " + startTime);
        ExecutorService es = Executors.newFixedThreadPool(procs);
        System.out.println("timer start "+ System.currentTimeMillis());
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
            ArrayList<String> urls = new ArrayList<>(message.getUrL().subList(start, end));
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
        long endtime = System.currentTimeMillis();
        System.out.println("timer end " + endtime);
        long diff = endtime - startTime;
        System.out.println("total time " + diff);
        return new Greeting(output);
    }
}

