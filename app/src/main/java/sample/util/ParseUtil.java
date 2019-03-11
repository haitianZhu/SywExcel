package sample.util;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Font;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.TableStyle;

import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sample.listener.ExcelListener;
import sample.listener.ParseListener;
import sample.model.LocationInfo;
import sample.model.MatchResult;

/**
 * @Author: haitian
 * @Date: 2019/3/6 10:09 PM
 * @Description:
 */
public class ParseUtil {

    /**
     * parse sheet in excel.
     * @param filename
     * @param sheetIndex
     * @throws Exception
     */
    public static void parseSheetInExcel(String filename, int sheetIndex, ParseListener listener) throws Exception {

        InputStream inputStream = FileUtil.getFileInputStream(filename);
        ExcelListener excelListener = new ExcelListener(listener);
        EasyExcelFactory.readBySax(inputStream, new Sheet(sheetIndex, 0), excelListener);

        inputStream.close();
    }

    public static List<List<MatchResult>> beginMatch(List<LocationInfo> firstList, List<LocationInfo> secondList, float matchDistance) {

        List<List<MatchResult>> result = new LinkedList<>();

        for (int i = 0; i < firstList.size(); i++) {

            LocationInfo firstLocation = firstList.get(i);
            ArrayList<MatchResult> matchResultList = new ArrayList<>();
            for (LocationInfo secondLocation : secondList) {

                double distance = LocationUtil.getDistance(firstLocation.latitude, firstLocation.longitude, secondLocation.latitude, secondLocation.longitude);
                if (distance <= matchDistance) {
                    matchResultList.add(new MatchResult(distance, secondLocation.name));
                }
            }

            result.add(matchResultList);
        }

        return result;

    }

    /**
     * add match result to original data.
     * @param data
     * @param matchResultList
     */
    public static void convertMatchResult(List<Object> data, List<List<MatchResult>> matchResultList) {

        if (data.size() != matchResultList.size()) {
            System.out.println("匹配结果与原表数据长度不一致");
        }

        int appendColumn = -1;

        for (int i = 0; i < matchResultList.size(); i++) {

            List<MatchResult> matchResults = matchResultList.get(i);

            StringBuilder sb = new StringBuilder();
            for (MatchResult matchResult : matchResults) {
                sb.append(matchResult.sourceStr + "\n\r");
            }

            if (!sb.toString().isEmpty()) {

                List<String> rowData = ((List<String>)data.get(i));
                if (appendColumn == -1) {
                    appendColumn = rowData.size();
                }

                if (rowData.size() > appendColumn) {
                    rowData.set(appendColumn, sb.toString());
                } else {
                    rowData.add(appendColumn, sb.toString());
                }

            }
        }

    }

    /**
     * write match result to sheet in file
     * @param filename
     * @param sheetIndex
     * @param data
     */
    public static void writeResult(String filename, int sheetIndex, List<List<String>> header, List<List<Object>> data) {

        try {
            OutputStream out = new FileOutputStream(filename);
            ExcelWriter writer = EasyExcelFactory.getWriter(out);
//            写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
            Sheet sheet = new Sheet(sheetIndex, 1);
            sheet.setHead(header);
//            sheet.setStartRow(0);
            sheet.setAutoWidth(Boolean.TRUE);
            writer.write1(data, sheet);

//            //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
//            Sheet sheet1 = new Sheet(1, 3);
//            sheet1.setSheetName("第一个sheet");
//            sheet1.setHead(createTestListStringHead());
//            sheet1.setAutoWidth(Boolean.TRUE);
//            writer.write1(createTestListObject(), sheet1);

            writer.finish();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static List<List<String>> createTestListStringHead(){
        //写sheet3  模型上没有注解，表头数据动态传入
        List<List<String>> head = new ArrayList<List<String>>();
        List<String> headCoulumn1 = new ArrayList<String>();
        List<String> headCoulumn2 = new ArrayList<String>();
        List<String> headCoulumn3 = new ArrayList<String>();
        List<String> headCoulumn4 = new ArrayList<String>();
        List<String> headCoulumn5 = new ArrayList<String>();

        headCoulumn1.add("第一列");headCoulumn1.add("第一列");headCoulumn1.add("第一列");
        headCoulumn2.add("第一列");headCoulumn2.add("第一列");headCoulumn2.add("第一列");

        headCoulumn3.add("第二列");headCoulumn3.add("第二列");headCoulumn3.add("第二列");
        headCoulumn4.add("第三列");headCoulumn4.add("第三列2");headCoulumn4.add("第三列2");
        headCoulumn5.add("第一列");headCoulumn5.add("第3列");headCoulumn5.add("第4列");

        head.add(headCoulumn1);
        head.add(headCoulumn2);
        head.add(headCoulumn3);
        head.add(headCoulumn4);
        head.add(headCoulumn5);
        return head;
    }

    public static List<List<Object>> createTestListObject() {
        List<List<Object>> object = new ArrayList<List<Object>>();
        for (int i = 0; i < 5; i++) {
            List<Object> da = new ArrayList<Object>();
            da.add("字符串"+i);
            da.add(Long.valueOf(187837834l+i));
            da.add(Integer.valueOf(2233+i));
            da.add(Double.valueOf(2233.00+i));
            da.add(Float.valueOf(2233.0f+i));
            da.add(new Date());
            da.add(new BigDecimal("3434343433554545"+i));
            da.add(Short.valueOf((short)i));
            object.add(da);
        }
        return object;
    }

    /**
     * match two string to find repeat char count.
     * @param firstStr
     * @param secondStr
     * @return
     */
    private static int match(String firstStr, String secondStr) {

        int count = 0;
        for (int i = 0; i < secondStr.length(); i++) {

            if(firstStr.contains(secondStr.charAt(i)+"")) {
                count++;
            }
        }

        return count;
    }

    public static TableStyle createTableStyle() {
        TableStyle tableStyle = new TableStyle();

        tableStyle.setTableHeadBackGroundColor(IndexedColors.BLUE);

        Font contentFont = new Font();
        contentFont.setBold(true);
        contentFont.setFontHeightInPoints((short)22);
        tableStyle.setTableContentFont(contentFont);
        tableStyle.setTableContentBackGroundColor(IndexedColors.GREEN);
        return tableStyle;
    }
}
