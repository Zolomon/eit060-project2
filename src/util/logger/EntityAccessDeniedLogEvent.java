package util.logger;

import util.Entity;
import util.Record;

public class EntityAccessDeniedLogEvent extends EntityAccessLogEvent {

	public EntityAccessDeniedLogEvent(int level, Entity entity, Record record,
			int accessRight) {
		super(level, entity, record, accessRight);
		this.msg = "DENIED: " + this.msg;
	}

	public EntityAccessDeniedLogEvent(Entity entity, Record record,
			int accessRight) {
		super(Log.LVL_INFO, entity, record, accessRight);
		this.msg = "DENIED: " + this.msg;
	}

}
