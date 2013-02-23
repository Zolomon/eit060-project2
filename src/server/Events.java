package server;

import util.EntityWithAccessControl;
import util.Record;


public class Events {
	private int eventClassId;
	private EntityWithAccessControl entity;
	private Record record;
	private boolean failEvent;
	
	public Events(int eventClassId, EntityWithAccessControl entity, Record journal, boolean failEvent){
		this.eventClassId = eventClassId;
		this.entity = entity;
		this.record = journal;
		this.failEvent = failEvent;
	}
	
	public boolean getIfFail(){
		return failEvent;
	}
}
