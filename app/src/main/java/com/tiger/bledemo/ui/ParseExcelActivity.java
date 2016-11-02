package com.tiger.bledemo.ui;

import java.io.File;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

import com.tiger.bledemo.R;

public class ParseExcelActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
//        readExcel();

		parseExcel();
    }

	private void parseExcel() {
		try {
			Workbook workbook = null;
			try {
				File file=new File(Environment.getExternalStorageDirectory()+File.separator+"test.xls");
				workbook = Workbook.getWorkbook(file);
			} catch (Exception e) {
				throw new Exception("File not found");
			}
			//得到第一张表
			Sheet sheet = workbook.getSheet(0);
			//列数
			int columnCount = sheet.getColumns();
			//行数
			int rowCount = sheet.getRows();
			//单元格
			Cell cell = null;
			for (int everyRow = 0; everyRow < rowCount; everyRow++) {
				for (int everyColumn = 0; everyColumn < columnCount; everyColumn++) {
					cell = sheet.getCell(everyColumn, everyRow);
					if (cell.getType() == CellType.NUMBER) {
						System.out.println("数字="+ ((NumberCell) cell).getValue());
					} else if (cell.getType() == CellType.DATE) {
						System.out.println("时间="+ ((DateCell) cell).getDate());
					} else {
						System.out.println("everyColumn="+everyColumn+",everyRow="+everyRow+
								",cell.getContents()="+ cell.getContents());
					}
				}
			}
			//关闭workbook,防止内存泄露
			workbook.close();
		} catch (Exception e) {

		}
	}
	/*private void readExcel()

	{

		try {

			Workbook workbook = null;

			try {

				workbook = Workbook
						.getWorkbook(new File("/sdcard/ab.xls"));

			} catch (Exception e) {

				throw new Exception("file to import not found!");

			}

			Sheet sheet = workbook.getSheet(0);

			Cell cell = null;

			int columnCount = sheet.getColumns();

			int rowCount = sheet.getRows();

			for (int i = 0; i < rowCount; i++) {

				for (int j = 0; j < columnCount; j++) {

					// ע�⣬�����������������һ���Ǳ�ʾ�еģ��ڶ��ű�ʾ��

					cell = sheet.getCell(j, i);

					// Ҫ���ݵ�Ԫ������ͷֱ������������ʽ���������ݿ��ܻ᲻��ȷ

					if (cell.getType() == CellType.NUMBER) {

						System.out.print(((NumberCell) cell).getValue());

					} else if (cell.getType() == CellType.DATE) {

						System.out.print(((DateCell) cell).getDate());

					} else {

						System.out.print(cell.getContents());

					}

					System.out.print("\t");

				}

				System.out.print("\n");

			}
			// �ر�������������ڴ�й¶
			workbook.close();
		} catch (Exception e) {

		}

	}*/

}