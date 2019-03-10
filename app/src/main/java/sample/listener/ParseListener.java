package sample.listener;

import java.util.List;

/**
 * @Author: haitian
 * @Date: 2019/3/6 10:27 PM
 * @Description:
 */
public interface ParseListener {

    void onParseCompleted(List<Object> data);
}
