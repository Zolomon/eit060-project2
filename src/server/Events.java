package server;

import util.EntityWithAccessControl;
import util.Journal;


public class Events {
	private int eventClassId;
	private EntityWithAccessControl entity;
	private Journal journal;
	private boolean failEvent;
	
	public Events(int eventClassId, EntityWithAccessControl entity, Journal journal, boolean failEvent){
		this.eventClassId = eventClassId;
		this.entity = entity;
		this.journal = journal;
		this.failEvent = failEvent;
	}
	
	public boolean getIfFail(){
		return failEvent;
	}
}
