package util.logger;

import java.util.Date;

public interface ILogEvent {

	public String getMessage();

	public long getTime();

	public int getId();
}
