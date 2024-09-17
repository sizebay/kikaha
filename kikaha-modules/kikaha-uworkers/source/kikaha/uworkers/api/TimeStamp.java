package kikaha.uworkers.api;

import lombok.Getter;

@Getter
public class TimeStamp {
	final long timestamp = System.currentTimeMillis();
}