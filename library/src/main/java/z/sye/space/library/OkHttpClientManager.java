package z.sye.space.library;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import z.sye.space.library.response.ResponseCallback;

/**
 * Created by Syehunter on 2015/11/26.
 */
public class OkHttpClientManager {

    private static final OkHttpClientManager mInstance = new OkHttpClientManager();
    private static final OkHttpClient mClient = new OkHttpClient();

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

}
