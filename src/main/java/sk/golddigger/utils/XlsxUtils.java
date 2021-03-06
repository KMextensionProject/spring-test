package sk.golddigger.utils;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;

import net.sf.jett.transform.ExcelTransformer;

/**
 * 
 */
public class XlsxUtils {

	private XlsxUtils() {
		throw new IllegalStateException(resolveMessage("factoryClassInstantiationError", XlsxUtils.class));
	}

	public static void generateXlsx(String template, Map<String, Object> dataSource, String targetName, HttpServletResponse response) throws IOException, InvalidFormatException {
		InputStream inputStream = XlsxUtils.class.getClassLoader().getResourceAsStream(template);
		addResponseHeaders(response, targetName);
		ServletOutputStream outputStream = response.getOutputStream();
		ExcelTransformer transformer = new ExcelTransformer();
		Workbook workbook = transformer.transform(inputStream, dataSource);
		workbook.write(outputStream);
	}

	private static void addResponseHeaders(HttpServletResponse response, String targetName) {
		response.addHeader("Content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		String contentDisposition = "attachment; filename=" + targetName;
		response.addHeader("Content-disposition", contentDisposition);
	}

	public static void removeTimeFromDate(Map<String, Object> data, String... keys) {
		for (String key : keys) {
			LocalDate date = ZonedDateTime.parse(String.valueOf(data.get(key))).toLocalDate();
			data.replace(key, date);
		}
	}
}
