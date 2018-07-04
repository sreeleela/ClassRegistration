package com.example.sree.classregistration;

import android.app.Application;
import android.util.Log;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Sree on 3/31/2018.
 */

public class CertificateApplication extends Application {
    public void onCreate() {
        super.onCreate();
        trustBismarckCertificate();
    }

    void trustBismarckCertificate() {
        try {
            TrustManager[] trustBismarck = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            if ( new BigInteger("231075417988431460729521241789065810259") != certs[0].getSerialNumber())
                                new CertificateException();
                        }
                    }
            };

            SSLContext bismarckContext = SSLContext.getInstance("SSL");
            bismarckContext.init(null, trustBismarck, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(bismarckContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals("bismarck.sdsu.edu");
                }
            });
        } catch (Exception e) {
        }
    }
}