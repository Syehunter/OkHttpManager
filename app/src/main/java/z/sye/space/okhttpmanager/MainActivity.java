package z.sye.space.okhttpmanager;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import z.sye.space.library.OkHttpManager;
import z.sye.space.library.response.ResponseCallBack;

public class MainActivity extends AppCompatActivity {

    String url = "";
    private MyCallBack myCallBack = new MyCallBack();
    private HashMap<String, String> headers;
    private HashMap<String, String> params;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //SyncGetRequest
        url = url + OkHttpManager.attachGetParamsToUrl("paramsKey", "paramsValue");     //add single Get params
        url = url + OkHttpManager.attachGetParamsToUrl(new HashMap<String, String>());   //Add multiple Get parmas
        OkHttpManager.url(url)
                .addHeader(headers)
                .getExcute();

        //AsyncGetRequest
        OkHttpManager.url(url)
                .addHeader(headers)
                .callback(myCallBack)
                .getEnqueue();

        //SyncPostRequest
        OkHttpManager.url(url)
                .addHeader(headers)
                .postExcute();

        //AsyncPostRequest
        OkHttpManager.url(url)
                .addHeader(headers)
                .callback(myCallBack)
                .json(jsonObject)           //postJson
//                .formBody(new HashMap<String, String>())  //postFormBody
//                .stream(mediaType, sink)  //postStream
//                .string(mediaType, s)     //postString
//                .file(mediaType, file)    //postFile
//                .multipart(new MultipartBuilder())        //post MultipartRequest
                .postEnqueue();

        //DownLoad
        OkHttpManager.url(url)
                .addHeader(headers)
                .callback(myCallBack)
                .downLoad(new File(Environment.getExternalStorageDirectory(), "ur Filename"));

        //Self-signed HTTPS
        // * U'd better put these codes in ur application
        try {
            OkHttpManager.setCertificates(getAssets().open("ur certificate"), "ur password");
//            OkHttpManager.setCertificates(inputStream);
            //set this if SSLPeerUnverifiedException occured with "Hostname xxx not verified"
//            OkHttpManager.setHostnameVerifier("ur Hostname");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Cache
        //OkHttpManager.setCache(new Cache());
        //Cache cache = OkHttpManager.getCache();


        //Other Configurations

        //OkHttpManager.setConnectTimeOut()
                //.setWriteTimeout()
                //.setReadTimeout();

    }

    private class MyCallBack extends ResponseCallBack<String>{

        @Override
        public void onResponse(String response) {
            Log.i(this.toString(), "Success : " + response);
        }

        @Override
        public void onFailure(Request request, Exception e) {
            Log.e(this.toString(), e.toString());
        }

        @Override
        protected void onDownLoad(long current, long total, boolean done) {
            int progress = (int) (current * 100 / total);
            Log.i(this.toString(), "=======> DownLoading <=================" + progress);
            super.onDownLoad(current, total, done);
        }

        @Override
        protected void onResponseHeader(Headers responseHeaders) {
            //Override this method if u need to get response headers
            super.onResponseHeader(responseHeaders);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        OkHttpManager.cancelRequest(url);
        super.onDestroy();
    }
}
