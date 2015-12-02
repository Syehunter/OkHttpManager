package z.sye.space.library;

import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okio.BufferedSink;
import z.sye.space.library.response.ResponseCallBack;


/**
 * Created by Syehunter on 2015/11/26.
 */
public class OkHttpManager {

    enum Method{
        GET, POST
    }

    private static ResponseCallBack mCallBack;

    private static final OkHttpManager mInstance = new OkHttpManager();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static String mUrl;
    private static HashMap<String, String> mHeaders;
    private static ArrayList<String> mRemovedHeaders = new ArrayList<>();
    private static MediaType mType;
    private static JSONObject mJsonObject;
    private static HashMap<String, String> mFormBody;
    private static String mString;
    private static File mFile;
    private static BufferedSink mSink;
    private static MultipartBuilder mMultipartBuilder;

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
        if (TextUtils.isEmpty(url)){
            throw new NullPointerException("Request Url cannot be null!");
        }
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
     * post提交json
     * @param jsonObject
     * @return
     */
    public static OkHttpManager json(JSONObject jsonObject){
        mJsonObject = jsonObject;
        return mInstance;
    }

    /**
     * post提交String，<1MB
     * @param type
     * @param string
     * @return
     */
    public static OkHttpManager string(MediaType type, String string){
        mType = type;
        mString = string;
        return mInstance;
    }

    /**
     *
     * @param type
     * @param sink
     * @return
     */
    public static OkHttpManager stream(MediaType type, BufferedSink sink){
        mType = type;
        mSink = sink;
        return mInstance;
    }

    /**
     * post提交文件
     * @param type
     * @param file
     * @return
     */
    public static OkHttpManager file(MediaType type, File file){
        mType = type;
        mFile = file;
        return mInstance;
    }

    /**
     * post提交分块请求，由用户自定义builder
     * @param builder
     * @return
     */
    public static OkHttpManager multipart(MultipartBuilder builder){
        mMultipartBuilder = builder;
        return mInstance;
    }

    /**
     * post提交表单
     * @return
     */
    public static OkHttpManager formBody(HashMap<String, String> formBody){
        mFormBody = formBody;
        return mInstance;
    }

    /**
     * 同步GET请求
     */
    public static Response getExcute(){
        Request request = buildRequest(Method.GET);

        Response response = OkHttpClientManager.getInstance().excute(request);

        reset();

        return response;
    }

    /**
     * 异步GET请求
     */
    public static void getEnqueue(){
        if (null == mCallBack){
            throw new NullPointerException("Enqueue Request must have a CallBack");
        }

        Request request = buildRequest(Method.GET);
        OkHttpClientManager.getInstance().enqueue(request, mCallBack);

        reset();
    }

    /**
     * 同步POST请求
     */
    public static Response postExcute(){
        Request request = buildRequest(Method.POST);

        Response response = OkHttpClientManager.getInstance().excute(request);

        reset();

        return response;
    }

    /**
     * 异步POST请求
     */
    public static void postEnqueue(){
        if (null == mCallBack){
            throw new NullPointerException("Enqueue Request must have a CallBack");
        }

        Request request = buildRequest(Method.POST);

        OkHttpClientManager.getInstance().enqueue(request, mCallBack);

        reset();
    }

    /**
     * put params into Request
     * 配置Request参数
     * @return
     */
    private static Request buildRequest(Method method) {
        Request.Builder builder = new Request.Builder();

        if (!inspectParamsLegitimacy()){
            throw new IllegalArgumentException("There should be only one RequestBody.");
        }

        buildUrl(builder);

        buildHeader(builder);

        buildRemovedHeader(builder);

        if (Method.POST == method){
            //如果是post请求，添加请求体
            buildRequestBody(builder);
        }

        return builder.build();
    }

    /**
     * 检验参数是否合法
     * @return
     *      true 参数合法
     *      false 参数不合法
     */
    private static boolean inspectParamsLegitimacy() {
        int paramsCount = 0;
        if (null != mMultipartBuilder){
            paramsCount += 1;
        }
        if (null != mJsonObject){
            paramsCount += 1;
        }
        if (null != mFormBody){
            paramsCount += 1;
        }
        if (null != mType && null != mString){
            paramsCount += 1;
        }
        if (null != mType && null != mFile){
            paramsCount += 1;
        }
        if (null != mType && null != mSink){
            paramsCount += 1;
        }
        return paramsCount > 1 ? false : true;
    }

    /**
     * build RequestBody with MediaType.Json
     * build Json请求体
     * @param builder
     */
    private static void buildRequestBody(Request.Builder builder) {
        if (null != mJsonObject){
            RequestBody requestBody = RequestBody.create(JSON, mJsonObject.toString());
            builder.post(requestBody);
            return;
        }

        if (null != mFormBody){
            FormEncodingBuilder encodingBuilder = new FormEncodingBuilder();
            for (String key : mFormBody.keySet()){
                encodingBuilder.add(key, mFormBody.get(key));
            }
            builder.post(encodingBuilder.build());
            return;
        }

        if (null != mType && null != mString){
            RequestBody requestBody = RequestBody.create(mType, mString);
            builder.post(requestBody);
            return;
        }

        if (null != mType && null != mFile){
            RequestBody requestBody = RequestBody.create(mType, mFile);
            builder.post(requestBody);
            return;
        }

        if (null != mType && null != mFile){
            RequestBody requestBody = new RequestBody() {

                @Override
                public MediaType contentType() {
                    return mType;
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    sink = mSink;
                }
            };
            builder.post(requestBody);
            return;
        }

        if (null != mMultipartBuilder){
            builder.post(mMultipartBuilder.build());
            return;
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
        if (!TextUtils.isEmpty(mUrl)){
            builder.url(mUrl);
            builder.tag(mUrl);
        } else {
            throw new NullPointerException("Request Url cannot be null!");
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
        mType = null;
        mString = null;
        mFile = null;
        mSink = null;
        mMultipartBuilder = null;
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
        OkHttpClientManager.getInstance().setConnectionTimeout(timeOut, unit);
        return mInstance;
    }

    /**
     * set the time out of each request
     * 设置写入超时时间
     * @param timeOut
     * @param unit
     * @return
     */
    public static OkHttpManager setWriteTimeout(long timeOut, TimeUnit unit){
        OkHttpClientManager.getInstance().setWriteTimeout(timeOut, unit);
        return mInstance;
    }

    /**
     * set the time out of each request
     * 设置读取超时时间
     * @param timeOut
     * @param unit
     * @return
     */
    public static OkHttpManager setReadTimeout(long timeOut, TimeUnit unit){
        OkHttpClientManager.getInstance().setReadTimeout(timeOut, unit);
        return mInstance;
    }

    /**
     * 设置缓存
     * @param cache
     * @return
     */
    public static OkHttpManager setCache(Cache cache){
        OkHttpClientManager.getInstance().setCache(cache);
        return mInstance;
    }

    /**
     * 获取缓存
     * @return
     */
    public static Cache getCache(){
        return OkHttpClientManager.getInstance().getCache();
    }

    /**
     * 设置Http AUTH认证
     * @param authenticator
     * @return
     */
    public static OkHttpManager setAuthenticator(Authenticator authenticator){
        if (null != authenticator){
            OkHttpClientManager.getInstance().setAuthenticator(authenticator);
        }
        return mInstance;
    }

    /**
     * 添加信任自签名HTTPS证书
     * @param certificates
     */
    public void setCertificates(InputStream... certificates)
    {
        try
        {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates)
            {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try
                {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e)
                {
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init
                    (
                            null,
                            trustManagerFactory.getTrustManagers(),
                            new SecureRandom()
                    );
            OkHttpClientManager.getInstance().setSslSocketFactory(sslContext.getSocketFactory());


        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 将Get请求的参数转化为字符串
     */
    public static String attachGetParamsToUrl(HashMap<String, String> params){
        String paramsUrl = "";
        if (null != params && params.size() > 0){
            for (String key : params.keySet()){
                paramsUrl += key + "=" + params.get(key) + "&";
            }
            paramsUrl = "?" + paramsUrl.substring(0, paramsUrl.length() - 1);
        }
        return paramsUrl;
    }

    /**
     * 将Get请求的参数转化为字符串
     * 单一请求参数
     */
    public static String attachGetParamsToUrl(String key, String value){
        return "?" + key + "=" + value;
    }
}
