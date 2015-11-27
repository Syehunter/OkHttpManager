package z.sye.space.library;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import z.sye.space.library.response.ResponseCallBack;


/**
 * Created by Syehunter on 2015/11/26.
 */
public class OkHttpManager {

    private static ResponseCallBack mCallBack;

    private static final OkHttpManager mInstance = new OkHttpManager();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static String mUrl;
    private static HashMap<String, String> mHeaders;
    private static JSONObject mJsonObject;
    private static ArrayList<String> mRemovedHeaders = new ArrayList<>();

    private OkHttpManager(){

    }

    public static OkHttpManager getInstance(){
        return mInstance;
    }

    /**
     * @param callBack 请求回调
     * @return
     */
    public static OkHttpManager callback(ResponseCallBack callBack){
        mCallBack = callBack;
        return mInstance;
    }

    /**
     * @param url  request Url
     * @return
     */
    public static OkHttpManager url(String url){
        mUrl = url;
        return mInstance;
    }

    /**
     * add RequestHeaders
     * 设置请求头
     * @param headers
     * @return
     */
    public static OkHttpManager addHeader(HashMap<String, String> headers){
        mHeaders = headers;
        return mInstance;
    }

    /**
     * @param header header will be removed
     * @return
     */
    public static OkHttpManager removeHeader(String header){
        mRemovedHeaders.add(header);
        return mInstance;
    }

    /**
     * Json格式请求体
     * @param jsonObject
     * @return
     */
    public static OkHttpManager addJsonBody(JSONObject jsonObject){
        mJsonObject = jsonObject;
        return mInstance;
    }

    /**
     * POST请求提交Json串
     */
    public static void postJson(){
        Request request = getBuilder();

        OkHttpClientManager.getInstance().enqueue(request, mCallBack);


        reset();
    }

    /**
     * put params into builder
     * 配置builder参数
     * @return
     */
    private static Request getBuilder() {
        Request.Builder builder = new Request.Builder();

        buildUrl(builder);

        buildHeader(builder);

        buildRemovedHeader(builder);

        buildJson(builder);

        return builder.build();
    }

    /**
     * build RequestBody with MediaType.Json
     * build Json请求体
     * @param builder
     */
    private static void buildJson(Request.Builder builder) {
        if (null != mJsonObject){
            RequestBody requestBody = RequestBody.create(JSON, mJsonObject.toString());
            builder.post(requestBody);
        }
    }

    /**
     * remove RequestHeader
     * 移除请求头
     * @param builder
     */
    private static void buildRemovedHeader(Request.Builder builder) {
        for (String header : mRemovedHeaders){
            if (mHeaders.containsKey(header)){
                builder.removeHeader(header);
            }
        }
    }

    /**
     * build RequestHeaders
     * build 请求头
     * @param builder
     */
    private static void buildHeader(Request.Builder builder) {
        if (null != mHeaders){
            for (String key : mHeaders.keySet()){
                builder.addHeader(key, mHeaders.get(key));
            }
        }
    }

    /**
     * build url&&tag
     * build 请求url和tag
     * @param builder
     */
    private static void buildUrl(Request.Builder builder) {
        if (null != mUrl){
            builder.url(mUrl);
            builder.tag(mUrl);
        }
    }

    /**
     * reset all params after request been sent
     * 发送请求后将所有参数重置
     */
    private static void reset(){
        mHeaders = null;
        mUrl = null;
        mJsonObject = null;
        mRemovedHeaders.clear();
    }

    /**
     * cancel request by Url
     * 取消请求
     * @param url 请求地址
     */
    public static void cancelRequest(String url){
        OkHttpClientManager.getInstance().cancel(url);
    }

    /**
     * set the time out of each request
     * 设置请求超时时间
     * @param timeOut
     * @param unit
     * @return
     */
    public static OkHttpManager setConnectTimeOut(long timeOut, TimeUnit unit){
        OkHttpClientManager.getInstance().setConnectionTimeOut(timeOut, unit);
        return mInstance;
    }
}
