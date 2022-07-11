package sk.golddigger.core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;

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
	private void initRequestDateTime() {
		this.dateTime = new RequestDateTime();
		this.today = LocalDate.now();
	}

	@Nested
	@DisplayName("getFirstDayOf()")
	class GetFirstDayOfTests {

		@Test
		@DisplayName("check on date parts for the first day of year")
		void yearTest() {
			LocalDate firstDayOfYear = dateTime.getFirstDayOf(DateUnit.YEAR);
			assertAll(
				() -> assertEquals(1, firstDayOfYear.getDayOfYear()),
				() -> assertEquals(1, firstDayOfYear.getMonthValue(), "Should modify entire date to make even month equal to the January."),
				() -> assertEquals(Year.now().getValue(), firstDayOfYear.getYear()));
		}

		@Test
		@DisplayName("check on date parts for this month")
		void monthTest() {
			LocalDate firstDayOfMonth = dateTime.getFirstDayOf(DateUnit.MONTH);
			assertAll(
				() -> assertEquals(1, firstDayOfMonth.getDayOfMonth()),
				() -> assertEquals(today.getMonthValue(), firstDayOfMonth.getMonthValue()));
		}

		@Test
		@DisplayName("check on date parts for this week")
		void weekTest() { 
			LocalDate firstDayOfWeek = dateTime.getFirstDayOf(DateUnit.WEEK);
			int firstDayOfThisWeekInYear = today.with(DayOfWeek.MONDAY).getDayOfYear();
			assertEquals(firstDayOfThisWeekInYear, firstDayOfWeek.getDayOfYear());
		}
	}
}
