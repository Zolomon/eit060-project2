package util.logger;

import java.util.Date;

public class LogEvent implements ILogEvent {

	protected String msg;
	protected long timestamp;
	protected static int id;
	protected int myId;
	private int level;
	private String type;

	public LogEvent(int level, String type, String msg) {
		this.msg = msg;
		this.level = level;
		this.type = type;
		this.timestamp = System.currentTimeMillis();
		this.myId = id++;
	}

	@Override
	public String getMessage() {
		return msg;
	}

	@Override
	public long getTime() {
		return timestamp;
	}

	@Override
	public int getId() {
		return myId;
	}
	
	@Override
	public String toString() {
		return String.format("[%s, %s, %s]\n", Log.lvlToString[level], type, msg);
	}

}
