package z.sye.space.library;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.util.HashMap;

import z.sye.space.library.response.ResponseCallback;
import z.sye.space.library.utils.JsonValidator;

/**
 * Created by Syehunter on 2015/11/26.
 */
public class OkHttpManager {

    private static ResponseCallback responseCallback = new ResponseCallback();

    private static final OkHttpManager mInstance = new OkHttpManager();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static String mUrl;
    private static HashMap<String, String> mHeader;
    private static JSONObject mJsonObject;

    private OkHttpManager(){

    }

    public static OkHttpManager getInstance(){
        return mInstance;
    }

    public static OkHttpManager callback(ResponseCallback responseCallback){
        mInstance.responseCallback = responseCallback;
        return mInstance;
    }

    /**
     * 设置请求的Url
     * @param url
     * @return
     */
    public static OkHttpManager url(String url){
        mInstance.mUrl = url;
        return mInstance;
    }

    /**
     * 设置请求头
     * @param header
     * @return
     */
    public static OkHttpManager addHeader(HashMap<String, String> header){
        mInstance.mHeader = header;
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
        Request.Builder builder = new Request.Builder();

        //url
        if (null != mUrl){
            builder.url(mUrl);
        }

        //RequestHeader
        if (null != mHeader){
            for (String key : mHeader.keySet()){
                builder.addHeader(key, mHeader.get(key));
            }
        }

        //添加RequestBody
        if (null != mJsonObject){
            RequestBody requestBody = RequestBody.create(JSON, mJsonObject.toString());
            builder.post(requestBody);
        }

        Request request = builder.build();
        if (null == responseCallback){
            //不需要回调
            OkHttpClientManager.postJson(request);
        } else {
            //需要回调
            OkHttpClientManager.postJson(request, responseCallback);
        }
        reset();
    }

    /**
     * 发送请求后将所有参数重置     */
    private static void reset(){
        responseCallback = null;
        mHeader = null;
        mUrl = null;
        mJsonObject = null;
    }
}
