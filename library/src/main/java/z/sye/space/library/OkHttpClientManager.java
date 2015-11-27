package z.sye.space.library;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import z.sye.space.library.response.ResponseCallback;

/**
 * Created by Syehunter on 2015/11/26.
 */
public class OkHttpClientManager {

    private static final OkHttpClientManager mInstance = new OkHttpClientManager();
    private static final OkHttpClient mClient = new OkHttpClient();

    //自定义默认的请求超时时间
    private static long mTimeOut = 10;
    private static TimeUnit mUint = TimeUnit.SECONDS;

    private OkHttpClientManager(){

    }

    public static OkHttpClientManager getInstance(){
        return mInstance;
    }

    public static void postJson(Request request){
        mClient.newCall(request).enqueue(new ResponseCallback());
    }

    public static void postJson(Request request, ResponseCallback responseCallback){
        mClient.newCall(request).enqueue(responseCallback);
    }

    public static void cancel(Object tag){
        mClient.cancel(tag);
    }

    public static void setConnectionTimeOut(Long timeOut, TimeUnit unit){
        if (null != timeOut){
            mTimeOut = timeOut;
        }
        if (null != unit){
            mUint = unit;
        }
        mClient.setConnectTimeout(mTimeOut, mUint);
    }

}
