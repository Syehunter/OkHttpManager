# OkHttpManager
###OkHttpManager is a manager for OkHttp that can make it easier to use.

###Import

  repositories {
        // ...
        maven { url "https://github.com" }
 }
 
 dependencies {
	        compile 'com.github.Syehunter:OkHttpManager:0.1.0'
	}
	
### How to use it?
SyncGetRequest

  url = url + OkHttpManager.attachGetParamsToUrl("paramsKey", "paramsValue");     //add single Get params
        url = url + OkHttpManager.attachGetParamsToUrl(new HashMap<String, String>());   //Add multiple Get parmas
        OkHttpManager.url(url)
                .addHeader(headers)
                .getExcute();
              
AsyncGetRequest

  OkHttpManager.url(url)
                .addHeader(headers)
                .callback(myCallBack)
                .getEnqueue();
                
SyncPostRequest

  OkHttpManager.url(url)
                .addHeader(headers)
                .postExcute();
                
AsyncPostRequest

  OkHttpManager.url(url)
                .addHeader(headers)
                .callback(myCallBack)
                .json(jsonObject)           //postJson
                //.formBody(new HashMap<String, String>())  //postFormBody
                //.stream(mediaType, sink)  //postStream
                //.string(mediaType, s)     //postString
                //.file(mediaType, file)    //postFile
                //.multipart(new MultipartBuilder())        //post MultipartRequest
                .postEnqueue();
                
DownLoad

  OkHttpManager.url(url)
                .addHeader(headers)
                .callback(myCallBack)
                .downLoad(new File(Environment.getExternalStorageDirectory(), "ur Filename"));
                
Self-signed HTTPS(* U'd better put these codes in ur application)

  try {
            OkHttpManager.setCertificates(getAssets().open("ur certificate"), "ur password");
            //OkHttpManager.setCertificates(inputStream);
            
            //set this if SSLPeerUnverifiedException occured with "Hostname xxx not verified"
            //OkHttpManager.setHostnameVerifier("ur Hostname");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
CallBack

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
    }
