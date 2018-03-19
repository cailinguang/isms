package com.vw.isms.util;

import com.vw.isms.standard.model.NetworkEvaluation;
import com.vw.isms.standard.model.excel.Risk;
import com.vw.isms.standard.model.excel.RiskCategory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.InputStream;
import java.util.*;

/**
 * Created by clg on 2018/3/7.
 */
public class ResolveExcel {
    /**
     * 解析网络安全法
     * @param in
     * @return
     * @throws Exception
     */
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
            if(!"Y".equals(conformity)&&!"N".equals(conformity)){
                conformity = "N";
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

    public static Map<String,List<RiskCategory>> resolveRisks() throws Exception{
        Map<String,List<RiskCategory>> map = new LinkedHashMap();
        Workbook workbook = WorkbookFactory.create(ResolveExcel.class.getResourceAsStream("/files/risk_library.xls"));

        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        DataFormatter formatter = new DataFormatter();
        while (sheetIterator.hasNext()){
            Sheet sheet = sheetIterator.next();

            List<RiskCategory> riskCategories = new ArrayList();
            map.put(getCellValue(formatter,sheet.getRow(1).getCell(0)),riskCategories);

            for (Row row : sheet) {
                if(row.getRowNum()==0) {
                    if(row.getLastCellNum()>4){
                        //create riskCategory
                        for(int i=5;i<row.getLastCellNum();i++){
                            RiskCategory riskCategory = new RiskCategory();
                            riskCategory.setName(getCellValue(formatter,row.getCell(i)));
                            riskCategory.setRisks(new ArrayList());
                            riskCategories.add(riskCategory);
                        }
                    }
                    continue;
                }

                Risk risk = new Risk();
                risk.setEvaluationObject(getCellValue(formatter,row.getCell(0)));
                risk.setRiskSource(getCellValue(formatter,row.getCell(1)));
                risk.setConsequence(getCellValue(formatter,row.getCell(2)));
                risk.setRiskDescripion(getCellValue(formatter,row.getCell(3)));
                risk.setAppendix(getCellValue(formatter,row.getCell(4)));

                if(row.getLastCellNum()>4){
                    //create riskCategory
                    for(int i=5;i<row.getLastCellNum();i++){
                        String value = getCellValue(formatter,row.getCell(i));
                        if("√".equals(value)){
                            RiskCategory riskCategory = filterByName(riskCategories,getCellValue(formatter,sheet.getRow(0).getCell(i)));
                            riskCategory.getRisks().add(risk);
                        }
                    }
                }
            }
        }

        return map;
    }

    private static RiskCategory filterByName(List<RiskCategory> riskCategories,String name){
        for(RiskCategory riskCategory:riskCategories){
            if(name.equals(riskCategory.getName())){
                return riskCategory;
            }
        }
        return null;
    }

    //获取单元格数据
    private static String getCellValue(DataFormatter formatter,Cell cell){
        Cell tempCell = getMergedRegionValue(cell.getSheet(),cell.getRowIndex(),cell.getColumnIndex());
        if(tempCell!=null){
            return formatter.formatCellValue(tempCell);
        }else{
            return formatter.formatCellValue(cell);
        }
    }
    //获取实际合并的单元格
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


    public static void main(String[] args) throws Exception{
        Map<String, List<RiskCategory>> map = resolveRisks();
        System.out.println(map);
    }
}
