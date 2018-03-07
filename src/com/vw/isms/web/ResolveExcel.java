package com.vw.isms.web;

import com.vw.isms.standard.model.NetworkEvaluation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clg on 2018/3/7.
 */
public class ResolveExcel {
    public static List<NetworkEvaluation> resolveNetrowkEvaluation(InputStream in) throws Exception{
        List<NetworkEvaluation> rt = new ArrayList();
        //read excel
        Workbook workbook = WorkbookFactory.create(in);
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        for (Row row : sheet) {
            if(row.getRowNum()==0) {
                continue;
            }
            NetworkEvaluation networkEvaluation = new NetworkEvaluation();

            String target = getCellValue(formatter,row.getCell(1));
            String index = getCellValue(formatter,row.getCell(2));
            String item = getCellValue(formatter,row.getCell(3));
            String result = getCellValue(formatter,row.getCell(4));
            String conformity = getCellValue(formatter,row.getCell(5));
            String remark = getCellValue(formatter,row.getCell(6));
            if(item==null||item.equals("")){
                continue;
            }
            networkEvaluation.setEvaluationTarget(target);
            networkEvaluation.setEvaluationIndex(index);
            networkEvaluation.setControlItem(item);
            networkEvaluation.setResult(result);
            networkEvaluation.setConformity(conformity);
            networkEvaluation.setRemark(remark);
            rt.add(networkEvaluation);
        }
        return rt;
    }

    private static String getCellValue(DataFormatter formatter,Cell cell){
        Cell tempCell = getMergedRegionValue(cell.getSheet(),cell.getRowIndex(),cell.getColumnIndex());
        if(tempCell!=null){
            return formatter.formatCellValue(tempCell);
        }else{
            return formatter.formatCellValue(cell);
        }
    }


    private static Cell getMergedRegionValue(Sheet sheet ,int row , int column){
        int sheetMergeCount = sheet.getNumMergedRegions();   //获得该sheet所有合并单元格数量

        for(int i = 0 ; i < sheetMergeCount ; i++){
            CellRangeAddress ca = sheet.getMergedRegion(i);    // 获得合并区域
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            //判断传入的单元格的行号列号是否在合并单元格的范围内，如果在合并单元格的范围内，择返回合并区域的首单元格格值
            if(row >= firstRow && row <= lastRow){

                if(column >= firstColumn && column <= lastColumn){
                    Row fRow = sheet.getRow(firstRow);
                    return fRow.getCell(firstColumn);
                }
            }
        }
        //如果该单元格行号列号不在任何一个合并区域，择返回null
        return null ;
    }
}
