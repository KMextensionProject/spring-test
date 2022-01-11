package sk.implementation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

@Component
public class RequestTime {

	public long getEpochSecondsUTC() {
		return ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond();
	}

	public LocalDate getFirstDayOf(RequestTime.DateUnit dateUnit) {
		LocalDate requestedDate = LocalDate.now(ZoneId.of("UTC"));
		switch (dateUnit) {
		case WEEK:
			return requestedDate.with(DayOfWeek.MONDAY);
		case MONTH:
			return requestedDate.withDayOfMonth(1);
		case YEAR:
			return requestedDate.withDayOfYear(1);
		}
		return requestedDate;
	}

	public static enum DateUnit {
		WEEK,
		MONTH,
		YEAR
	}
}
