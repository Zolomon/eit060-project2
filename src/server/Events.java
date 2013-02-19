
public class Events {
	private int eventClassId;
	private EntityWithAccessControl entity;
	private Journal journal;
	
	public Events(int eventClassId, EntityWithAccessControl entity, Journal journal){
		this.eventClassId = eventClassId;
		this.entity = entity;
		this.journal = journal;
	}
}
