package z.sye.space.okhttpmanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.okhttp.Request;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import z.sye.space.library.OkHttpManager;
import z.sye.space.library.response.ResponseCallBack;

public class MainActivity extends AppCompatActivity {

    String url = "http://app.kfxiong.com/client/homeBorrow.do";

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
                OkHttpManager.url("https://kyfw.12306.cn/otn/")
                        .callback(new ResponseCallBack<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i(this.toString(), "response success : " + response);
                            }

                            @Override
                            public void onFailure(Request request, Exception e) {
                            }
                        })
                        .getEnqueue();
            }
        });

        try {
            OkHttpManager.setCertificates(getAssets().open("srca.cer"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, String> headers = new HashMap<>();
        headers.put("version", "1.0.0");
        headers.put("secretKey", "");
        headers.put("channel", "Android");

        HashMap<String, String> params = new HashMap<>();
        params.put("pageSize", "8");
        JSONObject jsonObject = new JSONObject(params);

//        OkHttpManager.url(url)
//                .addHeader(headers)
//                .json(jsonObject)
//                .callback(new ResponseCallBack<String>() {
//
//                    @Override
//                    public void onResponse(String json) {
//                        Log.i(this.toString(), json);
//                    }
//
//                    @Override
//                    public void onFailure(Request request, Exception e) {
//
//                    }
//                })
//                .postEnqueue();

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
