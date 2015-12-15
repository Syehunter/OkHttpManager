package z.sye.space.library.response;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Syehunter on 11/27/2015.
 */
public abstract class ResponseCallBack<T> {

    public Type mType;

    public ResponseCallBack(){
        mType = getSuperClassType(getClass());
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 获得当前泛型
     * @param clazz
     * @return
     */
    static Type getSuperClassType(Class<?> clazz){
        Type superClass = clazz.getGenericSuperclass();
        if (superClass instanceof Class){
            throw new RuntimeException("Missing type parameter.");
        }
        //获取泛型
        ParameterizedType parameterizedType = (ParameterizedType) superClass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }

    /**
     * 发送请求之前的操作
     */
    public void onPreExcute(){
    }

    /**
     * 文件下载
     * @param current
     * @param total
     * @param done
     */
    public void onDownLoadCallBack(final long current, final long total, final boolean done){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onDownLoad(current, total, done);
            }
        });
    }

    /**
     * 下载实现方法
     * @param current
     * @param total
     * @param done
     */
    protected void onDownLoad(long current, long total, boolean done) {
    }

    public void onResponseCallBack(final T response){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onResponse(response);
            }
        });
    }

    protected abstract void onResponse(T response);

    /**
     * 响应头，可根据需要复写该方法
     */
    public void onResponseHeaderCallBack(final Headers responseHeaders){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onResponseHeader(responseHeaders);
            }
        });
    }

    protected void onResponseHeader(Headers responseHeaders) {
    }

    public void onFailureCallBack(final Request request, final Response response, final Exception e){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onFailure(request, response, e);
            }
        });
    }

    protected abstract void onFailure(Request request, Response response, Exception e);

    public static final ResponseCallBack<String> defaultCallBack = new ResponseCallBack<String>() {

        @Override
        public void onResponse(String response) {

        }

        @Override
        public void onFailure(Request request, Response response, Exception e) {

        }
    };
}
