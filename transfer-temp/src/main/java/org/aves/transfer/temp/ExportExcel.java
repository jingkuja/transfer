/**
 * 
 */
package org.aves.transfer.temp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.Region;

/**
 * @author kuja
 * 
 */
public class ExportExcel {
	HSSFWorkbook wb = null;

	public void extractExcel(List<List<Map<String, String>>> cm,
			OutputStream out, String name, boolean sacle, boolean ver,
			boolean title) {
		try {
			outDatasInSheet(cm, name, sacle, ver, title);
			wb.write(out);
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void outDatasInSheet(List<List<Map<String, String>>> cm,
			String sheetname, boolean sacle, boolean ver, boolean title)
			throws FileNotFoundException, IOException {
		wb = new HSSFWorkbook(); // 产生工作簿对象
		HSSFSheet sheet = wb.createSheet(); // 产生工作表对象
		if (title)
			sheet.addMergedRegion(new Region(0, (short) 3, 0, (short) 8));
		CellStyle cellStyle = wb.createCellStyle();
		CellStyle cellStyled = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		HSSFFont fontd = wb.createFont();

		cellStyle.setFont(font);
		font.setFontHeightInPoints((short) 11);
		if (title)
			fontd.setFontHeightInPoints((short) 14);
		else {
			fontd.setFontHeightInPoints((short) 11);
			cellStyled.setAlignment(CellStyle.ALIGN_CENTER);
		}
		cellStyled.setFont(fontd);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		// 设置左右
		cellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
		wb.setSheetName(0, sheetname);
		for (int i = 0; i < cm.size(); i++) {
			if (i == 0) {
				cellStyle.setFont(fontd);
			} else {
				cellStyle.setFont(font);
			}
			List<Map<String, String>> cl = cm.get(i);
			if (cl != null) {
				HSSFRow row = sheet.createRow(i);
				for (int j = 0; j < cl.size(); j++) {
					Map<String, String> dm = cl.get(j);
					String value = dm.get("value");
					if (value == null) {
						value = "";
					}
					short cnum = Short.valueOf(dm.get("cellno"));
					int width = 0;
					try {
						width = sheet.getColumnWidth((int) cnum);
					} catch (Exception e) {
					}

					if (i == 0 && !title) {
						if (dm.get("setlen") != null
								&& dm.get("setlen").equals("1")) {
							int length = value.length() * 2 * 270 + 370;
							if (length > width)
								sheet.setColumnWidth(cnum, length);
						} else {
							int length = value.length() * 270 + 700;
							if (length > width)
								sheet.setColumnWidth(cnum, length);
						}
					}

					if (i != 0) {
						if (dm.get("setlen") != null
								&& dm.get("setlen").equals("1")) {
							int length = value.length() * 2 * 270 + 370;
							if (length > width)
								sheet.setColumnWidth(cnum, length);
						} else {
							int length = value.length() * 270 + 700;
							if (length > width)
								sheet.setColumnWidth(cnum, length);
						}
					}

					HSSFCell cell = row.createCell(
							Integer.valueOf(dm.get("cellno")),
							HSSFCell.CELL_TYPE_STRING);
					if (i == 0) {
						cell.setCellStyle(cellStyled);
					} else {
						cell.setCellStyle(cellStyle);
					}
					cell.setCellValue(value);
				}
			}
		}
		HSSFPrintSetup printSetup = sheet.getPrintSetup();
		if (ver)
			printSetup.setLandscape(true);
		if (sacle)
			printSetup.setScale((short) 66);
	}

}
