package z.sye.space.library;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import z.sye.space.library.response.ResponseCallBack;

/**
 * Created by Syehunter on 2015/11/26.
 */
public class OkHttpClientManager {

    private static final OkHttpClientManager mInstance = new OkHttpClientManager();
    private static final OkHttpClient mClient = new OkHttpClient();

    //默认请求超时时间
    private static long mTimeOut = 10;
    private static TimeUnit mUint = TimeUnit.SECONDS;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final Gson mGson;

    private OkHttpClientManager(){
        mGson = new Gson();
    }

    public static OkHttpClientManager getInstance(){
        return mInstance;
    }

    /**
     * 同步请求
     * @param request
     */
    public void excute(Request request) {
        try {
            mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步请求
     * @param request
     * @param responseCallBack
     */
    public void enqueue(final Request request, final ResponseCallBack responseCallBack){
        responseCallBack.onPreExcute();
        mClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                doFailure(request, e, responseCallBack);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (null == responseCallBack){
                    return;
                }

                if (!response.isSuccessful()){
                    doFailure(request, new RuntimeException(response.body().string()), responseCallBack);
                } else {
                    doResponse(response, responseCallBack);
                }
            }
        });
    }

    /**
     * 处理请求成功时的动作
     * @param response
     */
    private void doResponse(final Response response, final ResponseCallBack responseCallBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                responseCallBack.onPostExcute();
                try {
                    if (responseCallBack.mType == String.class){
                        //单独处理String类型
                        responseCallBack.onResponse(response.body().string());
                    } else {
                        responseCallBack.onResponse(mGson.fromJson(response.body().string(), responseCallBack.mType));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 处理请求失败的动作
     * @param request
     * @param e
     * @param responseCallBack
     */
    private void doFailure(final Request request, final Exception e, final ResponseCallBack responseCallBack) {
        if (null == responseCallBack){
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                responseCallBack.onPostExcute();
                responseCallBack.onFailure(request, e);
            }
        });
    }

    public void cancel(Object tag){
        mClient.cancel(tag);
    }

    public void setConnectionTimeOut(Long timeOut, TimeUnit unit){
        if (null != timeOut){
            mTimeOut = timeOut;
        }
        if (null != unit){
            mUint = unit;
        }
        mClient.setConnectTimeout(mTimeOut, mUint);
    }

}
