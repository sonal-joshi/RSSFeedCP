package rssFeedReader;


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
public class RssFeedServiceController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/fetchFeed")
    @SendTo("/rss/feeds")
    public RssFeedReaderService fetchFeed(FeedRequest feedRequest) throws Exception {
        switch (feedRequest.getMode()) {
            case "0":
                return sequentialFeedReader(feedRequest);
            case "1":
                return parallelFeedReaderwithThreads(feedRequest);
            case "2":
                return parallelFeedReaderwithThreadPool(feedRequest);
            case "3":
                return forkjoinprinciple(feedRequest);
            case "4":
                return divideWorkAmongThreads(feedRequest);
            default:
                return null;
        }
    }

    public RssFeedReaderService sequentialFeedReader(FeedRequest feedRequest) throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("timer start " + start);
        FeedReader feedReader = new FeedReader(feedRequest.getUrL());
        feedReader.clearOutputList();
        feedReader.run();
        ArrayList<Channel> out = feedReader.getlist();
        long end = System.currentTimeMillis();
        System.out.println("timer end " + end);
        long diff = end - start;
        System.out.println("total time " + diff);
        System.out.println("total channel " + out.size());
        return new RssFeedReaderService(out);
    }

    public RssFeedReaderService parallelFeedReaderwithThreads(FeedRequest feedRequest) throws Exception {
        FeedReader parser = new FeedReader();
        parser.clearOutputList();
        ArrayList<Channel> output = new ArrayList<Channel>();
        long start = System.currentTimeMillis();
        System.out.println("timer start " + start);
        FeedUpdateTask feedupdatetask[] = new FeedUpdateTask[feedRequest.getUrL().size()];
        Thread task[] = new Thread[feedRequest.getUrL().size()];
        for (int i = 0; i < feedRequest.getUrL().size(); i++) {
            feedupdatetask[i] = new FeedUpdateTask(feedRequest.getUrL().get(i));
            task[i] = new Thread(feedupdatetask[i]);
            task[i].start();
            //call feed update after certain time interval
        }
        for (int i = 0; i < feedRequest.getUrL().size(); i++) {
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
        System.out.println("total channel " + output.size());
        return new RssFeedReaderService(output);
    }

    public RssFeedReaderService parallelFeedReaderwithThreadPool(FeedRequest feedRequest) {
        FeedReader parser = new FeedReader();
        parser.clearOutputList();
        long start = System.currentTimeMillis();
        System.out.println("timer start " + start);
        ArrayList<Channel> output = new ArrayList<Channel>();
        System.out.println("timer start "+ System.currentTimeMillis());
        ExecutorService exec=Executors.newFixedThreadPool(3);
        for (int i = 0; i < feedRequest.getUrL().size(); i++) {
            Runnable thread = new FeedReader(new ArrayList<>(Arrays.asList(feedRequest.getUrL().get(i))));
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
        System.out.println("total channel " + output.size());
        return new RssFeedReaderService(output);

    }

    public RssFeedReaderService forkjoinprinciple(FeedRequest feedRequest) {
    	ArrayList<Channel> output = new ArrayList<Channel>();
        long start = System.currentTimeMillis();
        System.out.println("timer start " + start);
    	int nThreads=Runtime.getRuntime().availableProcessors();
    	 System.out.println("timer start "+ System.currentTimeMillis());
        ForkJoinReader forkjoinreader=new ForkJoinReader();
        forkjoinreader.clearOutputList();
    	 ForkJoinPool forkJoinPool = new ForkJoinPool(nThreads);
    	 System.out.println("number of threads"+nThreads);
        forkJoinPool.invoke(new ForkJoinReader(feedRequest.getUrL(), 0, feedRequest.getUrL().size()));
    	 output=forkjoinreader.getList();
        long end = System.currentTimeMillis();
        System.out.println("timer end " + end);
        long diff = end - start;
        System.out.println("total time " + diff);
        System.out.println("total channel " + output.size());
        return new RssFeedReaderService(output);
    }


    public RssFeedReaderService divideWorkAmongThreads(FeedRequest feedRequest) {
        int procs = Runtime.getRuntime().availableProcessors();
        System.out.println(procs+" - "+feedRequest.getUrL());
        long startTime = System.currentTimeMillis();
        System.out.println("timer start " + startTime);
        ExecutorService es = Executors.newFixedThreadPool(procs);
        System.out.println("timer start "+ System.currentTimeMillis());
        int tasks = feedRequest.getUrL().size();
        int overflow = tasks % procs;
        List<Future<?>> futures = new ArrayList<>();

        int remainder = tasks % procs;
        int defaultBlockSize = Math.floorDiv(tasks, procs);
        int end = 0;
        int start = 0;
        int numberofTasks = 0;
        FeedReader reader = new FeedReader();
        reader.clearOutputList();
        for (int i = 1; i <= procs; i++) {
            if (i <= remainder)
                numberofTasks = defaultBlockSize + 1;
            else
                numberofTasks = defaultBlockSize;
            start = end > 0 ? end : 0;
            end = end + numberofTasks;
            System.out.println("New thread Range:" + start + " end : " + end+ " number of tasks "+numberofTasks);
            ArrayList<String> urls = new ArrayList<>(feedRequest.getUrL().subList(start, end));
            Runnable parser = new FeedReader(urls);
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
        ArrayList<Channel> output = new ArrayList<Channel>(reader.getlist());
        long endtime = System.currentTimeMillis();
        System.out.println("timer end " + endtime);
        long diff = endtime - startTime;
        System.out.println("total time " + diff);
        System.out.println("total channel " + output.size());
        return new RssFeedReaderService(output);
    }
}

