package sk.implementation;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

@Component
public class RequestTime {

	public long getEpochSecondsUTC() {
		return ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond();
	}

}
