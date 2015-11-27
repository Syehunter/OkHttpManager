package z.sye.space.library.listener;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Syehunter on 2015/11/26.
 */
public interface RequestBuilderListener {

    /**
     * 添加RequestHeader
     * @return
     */
    HashMap<String, String> addHeader();

    /**
     * 请求地址
     * @return
     */
    String url();

    /**
     * 请求提交Json字符串
     * @return
     */
    JSONObject addJsonBody();
}
