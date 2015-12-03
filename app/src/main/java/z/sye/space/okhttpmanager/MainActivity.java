package z.sye.space.okhttpmanager;

import android.os.Bundle;
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

import com.squareup.okhttp.Request;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import z.sye.space.library.OkHttpManager;
import z.sye.space.library.response.ResponseCallBack;

public class MainActivity extends AppCompatActivity {

    String url = "";
    private TextView tv;
    private MyCallBack myCallBack;
    private HashMap<String, String> headers;
    private HashMap<String, String> params;
    private JSONObject jsonObject;

    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpManager.url(url)
                        .addHeader(headers)
                        .json(jsonObject)
                        .callback(myCallBack)
                        .postEnqueue();

            }
        });

        tv = (TextView) findViewById(R.id.tv);

        try {
//            OkHttpManager.setCertificates(getAssets().open("srca.cer"));
            OkHttpManager.setHostnameVerifier("")
                    .setCertificates(getAssets().open(""), "");
        } catch (IOException e) {
            e.printStackTrace();
        }


        myCallBack = new MyCallBack();

        OkHttpManager.url(url)
                .addHeader(headers)
                .json(jsonObject)
                .callback(myCallBack)
                .postEnqueue();

        OkHttpManager.url("https://kyfw.12306.cn/otn/")
                .callback(new ResponseCallBack<String>() {
                    @Override
                    public void onResponse(String response) {

                    }

                    @Override
                    public void onFailure(Request request, Exception e) {

                    }
                })
                .getEnqueue();


    }

    private class MyCallBack extends ResponseCallBack<String>{

        @Override
        public void onResponse(String response) {
            Log.i(this.toString(), response);
            count++;
                    tv.setText("success" + count);
        }

        @Override
        public void onFailure(Request request, Exception e) {
            Log.e(this.toString(), e.toString());
        }
    }

    Handler handler = new Handler();

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
}
