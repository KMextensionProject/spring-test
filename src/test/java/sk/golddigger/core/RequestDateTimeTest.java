package sk.golddigger.core;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import sk.golddigger.core.RequestDateTime.DateUnit;

@TestInstance(Lifecycle.PER_CLASS) // now we can use the @BeforeAll on non-static methods
class RequestDateTimeTest {

	private RequestDateTime dateTime;
	private LocalDate today;

	@BeforeAll
	public void initRequestDateTime() {
		this.dateTime = new RequestDateTime();
		this.today = LocalDate.now();
	}

	@Nested
	@DisplayName("getFirstDayOf()")
	class GetFirstDayOfTests {

		@Test
		@DisplayName("check on date parts for the first day of year")
		public void yearTest() {
			LocalDate firstDayOfYear = dateTime.getFirstDayOf(DateUnit.YEAR);
			Assertions.assertAll(
				() -> Assertions.assertEquals(1, firstDayOfYear.getDayOfYear()),
				() -> Assertions.assertEquals(1, firstDayOfYear.getMonthValue(), 
						"Should modify entire date to make even month equal to the January."),
				() -> Assertions.assertEquals(Year.now().getValue(), firstDayOfYear.getYear())
			);
		}

		@Test
		@DisplayName("check on date parts for this month")
		public void monthTest() {
			LocalDate firstDayOfMonth = dateTime.getFirstDayOf(DateUnit.MONTH);
			Assertions.assertAll(
				() -> Assertions.assertEquals(1, firstDayOfMonth.getDayOfMonth()),
				() -> Assertions.assertEquals(today.getMonthValue(), firstDayOfMonth.getMonthValue())
			);
		}

		@Test
		@DisplayName("check on date parts for this week")
		public void weekTest() { 
			LocalDate firstDayOfWeek = dateTime.getFirstDayOf(DateUnit.WEEK);
			int firstDayOfThisWeekInYear = today.with(DayOfWeek.MONDAY).getDayOfYear();
			Assertions.assertEquals(firstDayOfThisWeekInYear, firstDayOfWeek.getDayOfYear());
		}
	}
}
