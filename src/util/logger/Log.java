package util.logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Log {

	public static int LVL_ERROR = 0;
	public static int LVL_WARNING = 1;
	public static int LVL_INFO = 2;
	public static int LVL_DEBUG = 3;
	public static int LVL_TRACE = 4;

	public static String[] lvlToString = new String[] { "LVL_ERROR",
			"LVL_WARNING", "LVL_INFO", "LVL_DEBUG", "LVL_TRACE" };

	private ArrayList<LogEvent> events;
	private OutputStream stream;

	public Log(OutputStream stream) {
		this.events = new ArrayList<LogEvent>();
		this.stream = stream;
	}

	public void updateLog(LogEvent event) {
		events.add(event);
		try {
			stream.write(event.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			updateLog(new LogEvent(LVL_ERROR, "EXCEPTION",
					"Error when writing to output stream"));
		}
	}

}
