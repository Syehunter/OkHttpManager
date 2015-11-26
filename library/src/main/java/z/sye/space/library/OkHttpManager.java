package z.sye.space.library;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import z.sye.space.library.builder.RequestBuilder;
import z.sye.space.library.response.ResponseCallback;

/**
 * Created by Syehunter on 2015/11/26.
 */
public class OkHttpManager {

    private static RequestBuilder requestBuilder = new RequestBuilder();
    private static ResponseCallback responseCallback = new ResponseCallback();

    private static final OkHttpManager mInstance = new OkHttpManager();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpManager(){

    }

    public static OkHttpManager getInstance(){
        return mInstance;
    }

    public static OkHttpManager requestBuilder(RequestBuilder requestBuilder){
        mInstance.requestBuilder = requestBuilder;
        return mInstance;
    }

    public static OkHttpManager callback(ResponseCallback responseCallback){
        mInstance.responseCallback = responseCallback;
        return mInstance;
    }

    /**
     * POST请求提交Json串
     */
    public static void postJson(){
        Request.Builder builder = new Request.Builder();

        //url
        if (null != requestBuilder.url()){
            builder.url(requestBuilder.url());
        }

        //RequestHeader
        if (null != requestBuilder.addHeader()){
            for (String key : requestBuilder.addHeader().keySet()){
                builder.addHeader(key, requestBuilder.addHeader().get(key));
            }
        }

        //添加RequestBody
        if (JsonValidator.validate(requestBuilder.addJsonBody())){
            String s = requestBuilder.addJsonBody();
            RequestBody requestBody = RequestBody.create(JSON, requestBuilder.addJsonBody());
            Log.i(mInstance.toString(), requestBuilder.addJsonBody());
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
    }

}
