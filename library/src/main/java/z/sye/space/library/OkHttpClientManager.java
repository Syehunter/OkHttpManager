package z.sye.space.library;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import z.sye.space.library.body.DownLoadResponseBody;
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
                doFailure(request, null, e, responseCallBack);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (null == responseCallBack) {
                    return;
                }

                if (!response.isSuccessful()) {
                    Log.e(this.toString(), "Error Response Code == " + response.code());
                    doFailure(request, response, new RuntimeException(response.body().string()), responseCallBack);
                } else {
                    doResponse(response, responseCallBack);
                }
            }
        });
    }

    /**
     * 下载
     *
     * @param file
     * @param request
     * @param responseCallBack
     */
    public void download(final File file, final Request request, final ResponseCallBack responseCallBack) {
        responseCallBack.onPreExcute();
        OkHttpClient clone = mClient.clone();
        clone.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                Response downloadBuild = originalResponse.newBuilder()
                        .body(new DownLoadResponseBody(originalResponse.body(), responseCallBack))
                        .build();
                return downloadBuild;
            }
        });
        clone.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                doFailure(request, null, e, responseCallBack);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (null == responseCallBack) {
                    return;
                }

                if (!response.isSuccessful()) {
                    Log.e(this.toString(), "Error Response Code == " + response.code());
                    doFailure(request, response, new RuntimeException(response.body().string()), responseCallBack);
                } else {
                    doDownload(file, response, responseCallBack);
                }
            }
        });
    }

    /**
     * 处理下载请求
     * @param file
     * @param response
     * @param responseCallBack
     */
    private void doDownload(File file, Response response, ResponseCallBack responseCallBack) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            responseCallBack.onResponseCallBack(file.getAbsolutePath());
        } catch (IOException e) {
            responseCallBack.onFailureCallBack(response.request(), response, e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }

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
            responseCallBack.onResponseHeaderCallBack(response.headers());
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
    private void doFailure(Request request, Response response, Exception e, ResponseCallBack responseCallBack) {
        if (null == responseCallBack) {
            return;
        }
        Log.e(this.toString(), e.toString());
        responseCallBack.onFailureCallBack(request, response, e);
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

    public void setHostnameVerifier(final String hostName) {
        mClient.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (TextUtils.isEmpty(hostName)) {
                    return true;
                }
                if (hostname.equals(hostName)) {
                    return true;
                }
                return false;
            }
        });
    }

}
