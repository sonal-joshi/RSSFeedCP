package rssFeedReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

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
				 parser = new FeedReader();
				 try {
					 setList(parser.fetchFeed(new URL(input.get(i))));
				 } catch (MalformedURLException e) {
					 e.printStackTrace();
				 }
			 }			
            
         } else {	    	
            int mid = low + (high - low) / 2;
            ForkJoinReader left=new ForkJoinReader(input,low,mid);
            ForkJoinReader right=new ForkJoinReader(input,mid,high);
            
            left.fork();
            right.compute();
            left.join();
          
         }
		
		
	}
	
	public synchronized void setList(Channel channel) {
		if(channel!=null)
    		output.add(channel);
	}
	
	public ArrayList<Channel> getList() {
		return output;
	}

	public synchronized void clearOutputList(){
    	output.clear();
	}
	
}


