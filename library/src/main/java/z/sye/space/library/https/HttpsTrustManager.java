/**
 * @(#) z.sye.space.library 2015/12/3;
 * <p/>
 * Copyright (c), 2009 深圳孔方兄金融信息服务有限公司（Shenzhen kfxiong
 * Financial Information Service Co. Ltd.）
 * <p/>
 * 著作权人保留一切权利，任何使用需经授权。
 */
package z.sye.space.library.https;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import z.sye.space.library.OkHttpManager;

/**
 * Created by Syehunter on 2015/12/3.
 */
public class HttpsTrustManager implements X509TrustManager {

    private X509TrustManager defaultTrustManager;
    private X509TrustManager localTrustManager;

    public HttpsTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        defaultTrustManager = OkHttpManager.chooseTrustManager(trustManagerFactory.getTrustManagers());
        this.localTrustManager = localTrustManager;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            defaultTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException ce) {
            localTrustManager.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
