package sample.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: haitian
 * @Date: 2019/3/6 10:20 PM
 * @Description:
 */
public class ExcelListener extends AnalysisEventListener {

    private List<Object> data = new ArrayList<Object>();

    private ParseListener parseListener;

    public ExcelListener(ParseListener listener) {
        this.parseListener = listener;
    }

    @Override
    public void invoke(Object object, AnalysisContext context) {
//        System.out.println(context.getCurrentSheet());
        data.add(object);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

        parseListener.onParseCompleted(data);
    }

}