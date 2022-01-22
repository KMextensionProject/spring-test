package sk.golddigger.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;

import net.sf.jett.transform.ExcelTransformer;

/**
 * 
 */
// TODO: implement
public class XlsxUtils {

	public static void generateXlsx(String template, Map<String, Object> dataSource, String targetName, HttpServletResponse response) throws InvalidFormatException, FileNotFoundException, IOException {
		InputStream input = XlsxUtils.class.getClassLoader().getResourceAsStream(template);
		
		addResponseHeaders(response, targetName);
		OutputStream output = response.getOutputStream();
		
		ExcelTransformer transformer = new ExcelTransformer();
		Workbook workbook = transformer.transform(input, dataSource);
		workbook.write(output);
		
	}

	private static void addResponseHeaders(HttpServletResponse response, String targetName) {
		
		
	}

}
