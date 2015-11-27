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

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONObject;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        HashMap<String, String> headers = new HashMap<>();
        headers.put("version", "1.0.0");
        headers.put("secretKey", "");
        headers.put("channel", "Android");

        HashMap<String, String> params = new HashMap<>();
        params.put("pageSize", "8");
        JSONObject jsonObject = new JSONObject(params);

        OkHttpManager.url(url)
                .addHeader(headers)
                .addJsonBody(jsonObject)
                .callback(new ResponseCallBack<String>() {

                    @Override
                    public void onResponse(String json) {
//                        String json = jsonObject.toString();
                        Log.i(this.toString(), json);
                    }

                    @Override
                    public void onFailure(Request request, Exception e) {

                    }
                })
                .postJson();

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
