package util.logger;

import util.Entity;
import util.EntityWithAccessControl;
import util.Record;

public class EntityAccessLogEvent extends LogEvent {

	public EntityAccessLogEvent(int level, Entity entity, Record record,
			int accessRight) {
		super(
				level,
				"Entity Accessed Record",
				String.format(
						"Entity #%d %s Record #%d",
						entity.getId(),
						accessRight == EntityWithAccessControl.READ ? "read"
								: accessRight == EntityWithAccessControl.WRITE ? "wrote to"
										: "executed", record.getId()));

	}

	public EntityAccessLogEvent(Entity entity, Record record, int accessRight) {
		super(
				Log.LVL_INFO,
				"Entity Accessed Record",
				String.format(
						"%s #%d %s Record #%d",
						entity.getType(),
						entity.getId(),
						accessRight == EntityWithAccessControl.READ ? "read"
								: accessRight == EntityWithAccessControl.WRITE ? "wrote to"
										: "executed", record.getId()));

	}

}
