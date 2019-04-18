package hello;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class ForkJoinReader extends RecursiveAction{
	
	int low,high;
    ArrayList<String> input=new ArrayList<String>();
    private static ArrayList<Channel> output = new ArrayList<Channel>();
    FeedReader parser;
    
    public ForkJoinReader() {}
	public ForkJoinReader(ArrayList<String> input,int low,int high) {
		// TODO Auto-generated constructor stub
		for(String str: input)
			this.input.add(str);
		this.low=low;
		this.high=high;
	}

	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		
		if(high - low <= 2) {
			 for(int i = low; i < high; ++i) {
				 parser =new FeedReader(input.get(i));
				 setList(parser.fetchFeed());
				 System.out.println("in end task");
			 }			
            
         } else {	    	
            int mid = low + (high - low) / 2;
            System.out.println("dividing");
            ForkJoinReader left=new ForkJoinReader(input,low,mid);
            ForkJoinReader right=new ForkJoinReader(input,mid,high);
            
            left.fork();
            right.compute();
            left.join();
          
         }
		
		
	}
	
	public synchronized void setList(Channel channel) {
		System.out.println("adding to main output list");
		output.add(channel);
		
	}
	
	public ArrayList<Channel> getList() {
		System.out.println("printing output list:: ");
		for(Channel channel: output)
			System.out.println("channel names in "+channel.getTitle());
		return output;
	}
	
}


