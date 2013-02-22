package server;
import java.util.ArrayList;


public class Log {

	private ArrayList<Events> events;
	
	public Log(){	
		this.events = new ArrayList<Events>();
	}
	
	public  void updateLog(Events event){
		events.add(event);
	}
	
}
