package com.vw.isms.util;

import com.vw.isms.standard.model.AuditLog;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.tools.ant.util.ReflectUtil;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by clg on 2018/4/1.
 */
public class ExportUtil {
    public static void exportExcel(String fileName, List<? extends Object> lists, String[] headers, String[] fields, HttpServletResponse response) throws Exception{
        response.setHeader( "Content-Disposition", "attachment;filename=\""+ new String( fileName.getBytes( "gb2312" ), "ISO8859-1" )+ ".xls" + "\"" );

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("table");  //创建table工作薄
        HSSFRow row;
        HSSFCell cell;

        //style
        HSSFCellStyle cellStyle = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short)14);
        cellStyle.setFont(font);


        //title
        row = sheet.createRow(0);
        for(int i=0;i<headers.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(cellStyle);
            sheet.setColumnWidth(i, headers[i].getBytes().length*2*150);
        }


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(int i = 0; i < lists.size(); i++) {
            row = sheet.createRow(i+1);//创建表格行
            Object obj = lists.get(i);
            for(int j=0;j<fields.length;j++){
                Object field = ReflectUtil.getField(obj,fields[j]);
                cell = row.createCell(j);
                if(field!=null&&field instanceof Date){
                    cell.setCellValue(simpleDateFormat.format(field));
                }
                else {
                    cell.setCellValue(ObjectUtils.toString(field));
                }
            }
        }

        wb.write(response.getOutputStream());
        wb.close();
    }
}
