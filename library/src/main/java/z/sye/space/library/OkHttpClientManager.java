package z.sye.space.library;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

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

    private final Gson mGson;

    private OkHttpClientManager() {
        mGson = new Gson();
    }

    public static OkHttpClientManager getInstance() {
        return mInstance;
    }

    /**
     * 同步请求
     *
     * @param request
     */
    public Response excute(Request request) {
        try {
            Response response = mClient.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步请求
     *
     * @param request
     * @param responseCallBack
     */
    public void enqueue(final Request request, final ResponseCallBack responseCallBack) {
        responseCallBack.onPreExcute();
        mClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                doFailure(request, e, responseCallBack);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (null == responseCallBack) {
                    return;
                }

                if (!response.isSuccessful()) {
                    Log.e(this.toString(), "Error Response Code == " + response.code());
                    doFailure(request, new RuntimeException(response.body().string()), responseCallBack);
                } else {
                    doResponse(response, responseCallBack);
                }
            }
        });
    }

    /**
     * 处理请求成功时的动作
     *
     * @param response
     */
    private void doResponse(final Response response, final ResponseCallBack responseCallBack) {
        try {
            if (responseCallBack.mType == String.class) {
                //单独处理String类型
                responseCallBack.onResponseCallBack(response.body().string());
            } else {
                responseCallBack.onResponseCallBack(mGson.fromJson(response.body().string(), responseCallBack.mType));
            }
            responseCallBack.onResponseHeader(response.headers());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理请求失败的动作
     *
     * @param request
     * @param e
     * @param responseCallBack
     */
    private void doFailure(final Request request, final Exception e, final ResponseCallBack responseCallBack) {
        if (null == responseCallBack) {
            return;
        }
        Log.e(this.toString(), e.toString());
        responseCallBack.onFailureCallBack(request, e);
    }

    public void cancel(Object tag) {
        mClient.cancel(tag);
    }

    public void setConnectionTimeout(Long timeout, TimeUnit unit) {
        if (null != timeout) {
            mTimeOut = timeout;
        }
        if (null != unit) {
            mUint = unit;
        }
        mClient.setConnectTimeout(mTimeOut, mUint);
    }

    public void setWriteTimeout(Long timeout, TimeUnit unit) {
        if (null != timeout) {
            mTimeOut = timeout;
        }
        if (null != unit) {
            mUint = unit;
        }
        mClient.setWriteTimeout(mTimeOut, mUint);
    }

    public void setReadTimeout(Long timeout, TimeUnit unit) {
        if (null != timeout) {
            mTimeOut = timeout;
        }
        if (null != unit) {
            mUint = unit;
        }
        mClient.setReadTimeout(mTimeOut, mUint);
    }

    public void setAuthenticator(Authenticator authenticator) {
        if (null != authenticator) {
            mClient.setAuthenticator(authenticator);
        }
    }

    public void setCache(Cache cache) {
        if (null != cache) {
            mClient.setCache(cache);
        }
    }

    public Cache getCache() {
        return mClient.getCache();
    }

    public void setSslSocketFactory(SSLSocketFactory socketFactory) {
        mClient.setSslSocketFactory(socketFactory);
    }

    public void setHostnameVerifier(final String hostName){
        mClient.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (TextUtils.isEmpty(hostName)){
                    return true;
                }
                if (hostname.equals(hostName)){
                    return true;
                }
                return false;
            }
        });
    }
}
