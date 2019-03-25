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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        EasyExcelFactory.readBySax(inputStream, new Sheet(sheetIndex, 1), excelListener);

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
    public static void writeResult(String filename, int sheetIndex, List<List<Object>> data) {

        try {
            OutputStream out = new FileOutputStream(filename);
            ExcelWriter writer = EasyExcelFactory.getWriter(out);
            //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
            Sheet sheet = new Sheet(sheetIndex, 1);
//            sheet.setTableStyle(createTableStyle());
//            sheet.setStartRow(1);
            sheet.setAutoWidth(Boolean.TRUE);
            writer.write1(data, sheet);

            writer.finish();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
