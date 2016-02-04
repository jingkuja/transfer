/**
 * 
 */
package org.aves.transfer.imp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author kuja
 * 
 */
public class ReadExcel {
	Workbook wb = null;

	public List<List<String>> extractExcel(InputStream is) {
		List<List<String>> output = new ArrayList<List<String>>();
		try {
			wb = new HSSFWorkbook(is);
			return getDatasInSheet(wb, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	public List<List<String>> getDatasInSheet(Workbook workbook, int sheetNumber)
			throws FileNotFoundException, IOException {

		List<List<String>> result = new ArrayList<List<String>>();

		// 获得指定的表
		Sheet sheet = workbook.getSheetAt(sheetNumber);

		// 获得数据总行数
		int rowCount = sheet.getLastRowNum();
		if (rowCount < 1) {
			return result;
		}

		// 逐行读取数据
		for (int rowIndex = 0; rowIndex <= rowCount; rowIndex++) {

			// 获得行对象
			Row row = sheet.getRow(rowIndex);

			if (row != null) {

				// 获得本行中单元格的个数
				int columnCount = row.getLastCellNum();

				List<String> op = new ArrayList<String>();
				// 获得本行中各单元格中的数据
				for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
					Cell cell = row.getCell(columnIndex);
					try {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						// 获得指定单元格中数据
						Object cellStr = cell.getStringCellValue();
						op.add(String.valueOf(cellStr));
					} catch (Exception e) {
						op.add("");
					}
				}
				result.add(op);
			}
		}
		return result;
	}

	public boolean isExcel2003(String filePath) {
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

	public boolean isExcel2007(String filePath) {
		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}

	public Object getCellString(Cell cell) {
		Object result = null;
		if (cell != null) {
			int cellType = cell.getCellType();
			switch (cellType) {
			case HSSFCell.CELL_TYPE_STRING:
				result = cell.getRichStringCellValue().getString();
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				result = cell.getNumericCellValue();
				break;
			case HSSFCell.CELL_TYPE_FORMULA:
				result = cell.getNumericCellValue();
				break;
			case HSSFCell.CELL_TYPE_ERROR:
				result = "";
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				result = cell.getBooleanCellValue();
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				result = "";
				break;
			}
		}
		return result;
	}
}
