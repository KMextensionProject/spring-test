package sk.golddigger.core;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RequestDateTime {

	private static final Logger logger = Logger.getLogger(RequestDateTime.class);

	public long getEpochSecondsUTC() {
		return ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond();
	}

	public LocalDate getLocalDateUTC() {
		return LocalDate.now(ZoneId.of("UTC"));
	}

	public LocalDate getFirstDayOf(DateUnit dateUnit) {
		LocalDate requestedDate = getLocalDateUTC();
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

	public LocalDate getFirstDayAdjusted(DateUnit dateUnit) {
		LocalDate today = getLocalDateUTC();
		LocalDate resultDate = getFirstDayOf(dateUnit);

		// polygon cannot give opening and closing prices for current date,
		// so that's when a day adjustment must take place.
		switch (dateUnit) {
		case WEEK:
			return (today.getDayOfWeek().equals(resultDate.getDayOfWeek())) ? subtractOneDay(resultDate) : resultDate;
		case MONTH:
			return (today.getDayOfMonth() == resultDate.getDayOfMonth()) ? subtractOneDay(resultDate) : resultDate;
		case YEAR:
			return (today.getDayOfYear() == resultDate.getDayOfYear()) ? subtractOneDay(resultDate) : resultDate;
		default:
			return resultDate;
		}
	}

	private LocalDate subtractOneDay(LocalDate date) {
		if (logger.isDebugEnabled()) {
			logger.debug(resolveMessage("dateAdjusting", date));
		}
		return date.minus(1L, ChronoUnit.DAYS);
	}

	public enum DateUnit {
		WEEK,
		MONTH,
		YEAR
	}
}
