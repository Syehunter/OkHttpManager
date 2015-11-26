/**
 * @(#) z.sye.space.library.request 2015/11/26;
 * <p/>
 * Copyright (c), 2009 深圳孔方兄金融信息服务有限公司（Shenzhen kfxiong
 * Financial Information Service Co. Ltd.）
 * <p/>
 * 著作权人保留一切权利，任何使用需经授权。
 */
package z.sye.space.library.builder;

import java.util.HashMap;

import z.sye.space.library.listener.RequestBuilderListener;

/**
 * Created by Syehunter on 2015/11/26.
 */
public class RequestBuilder implements RequestBuilderListener {

    public RequestBuilder(){

    }

    @Override
    public HashMap<String, String> addHeader() {
        return null;
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public String addJsonBody() {
        return "{}";
    }
}
