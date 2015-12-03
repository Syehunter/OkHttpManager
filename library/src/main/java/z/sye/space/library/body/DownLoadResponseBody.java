/**
 * @(#) z.sye.space.library.body 2015/12/3;
 * <p/>
 * Copyright (c), 2009 深圳孔方兄金融信息服务有限公司（Shenzhen kfxiong
 * Financial Information Service Co. Ltd.）
 * <p/>
 * 著作权人保留一切权利，任何使用需经授权。
 */
package z.sye.space.library.body;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import z.sye.space.library.response.ResponseCallBack;

/**
 * Created by Syehunter on 2015/12/3.
 * 下载响应体
 */
public class DownLoadResponseBody extends ResponseBody {

    /**
     * The Real responseBody
     */
    private final ResponseBody responseBody;

    private BufferedSource mSource;
    private ResponseCallBack mResponseCallBack;

    public DownLoadResponseBody(ResponseBody responseBody, ResponseCallBack responseCallBack) {
        this.responseBody = responseBody;
        this.mResponseCallBack = responseCallBack;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() throws IOException {
        if (null == mSource){
            mSource = Okio.buffer(encapSource(responseBody.source()));
        }
        return mSource;
    }

    /**
     * 封装source
     * @param source
     * @return
     */
    private Source encapSource(Source source){
        ForwardingSource forwardingSource = new ForwardingSource(source) {

            long current = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                current += bytesRead != -1 ? bytesRead : 0;
                if (null != mResponseCallBack){
                    mResponseCallBack.onDownLoadCallBack(current, responseBody.contentLength(),
                            current == responseBody.contentLength());
                }
                return bytesRead;
            }
        };
        return forwardingSource;
    }

}
