package com.tiger.bledemo;

import android.os.Environment;

import org.junit.Test;

import java.io.File;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void add(){
        int temp = 0 ;
        for (int i = 1;i <= 100;i++){
            temp += i;
        }
        System.out.println(temp);
    }


    @Test
    public void parseExcel() {
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
}