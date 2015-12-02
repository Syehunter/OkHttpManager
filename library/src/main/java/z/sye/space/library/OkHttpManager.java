package z.sye.space.library;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
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
    private static HashMap<String, String> mFormBody;

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
     * 提交json
     * @param jsonObject
     * @return
     */
    public static OkHttpManager json(JSONObject jsonObject){
        mJsonObject = jsonObject;
        return mInstance;
    }

    /**
     * 提交表单
     * @return
     */
    public static OkHttpManager formBody(HashMap<String, String> formBody){
        mFormBody = formBody;
        return mInstance;
    }

    /**
     * 同步post请求
     */
    public static void postExcute(){
        Request request = getBuilder();

        OkHttpClientManager.getInstance().excute(request);

        reset();
    }

    /**
     * 异步post请求
     */
    public static void postEnqueue(){
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

        inspectParamsLegitimacy();

        buildUrl(builder);

        buildHeader(builder);

        buildRemovedHeader(builder);

        buildJson(builder);

        return builder.build();
    }

    /**
     * 检验参数是否合法
     */
    private static boolean inspectParamsLegitimacy() {
        //TODO
        return false;
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
        if (null != mFormBody){
            FormEncodingBuilder encodingBuilder = new FormEncodingBuilder();
            for (String key : mFormBody.keySet()){
                encodingBuilder.add(key, mFormBody.get(key));
            }
            builder.post(encodingBuilder.build());
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
        mFormBody = null;
    }

    /**
     * cancel request by Url
     * 取消请求
     * @param url 请求地址
     */
    public static void cancelRequest(String url){
        try {
            OkHttpClientManager.getInstance().cancel(url);
        } catch (Exception e){
            //catch OkHttp取消请求时可能throw出的Exception,防止crash
            Log.e("OnOkHttpCancel", e.toString());
            e.printStackTrace();
        }
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
