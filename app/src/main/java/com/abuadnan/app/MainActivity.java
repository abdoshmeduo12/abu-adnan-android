package com.abuadnan.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
    private static final String HOME = "file:///android_asset/index.html";
    // الروابط المسموح فتحها خارج التطبيق (واتساب والخريطة). أضف هنا لو زودت روابط جديدة.
    private static final Set<String> LINK_HOSTS = new HashSet<>(Arrays.asList(
            "wa.me", "api.whatsapp.com", "maps.app.goo.gl", "www.google.com", "google.com", "maps.google.com"));
    // الموارد الوحيدة المسموح تحميلها من الإنترنت (خطوط جوجل).
    private static final Set<String> RESOURCE_HOSTS = new HashSet<>(Arrays.asList(
            "fonts.googleapis.com", "fonts.gstatic.com"));

    private WebView web;

    private static boolean isAsset(Uri u) {
        return "file".equals(u.getScheme()) && u.getPath() != null && u.getPath().startsWith("/android_asset/");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView.setWebContentsDebuggingEnabled(false);
        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);          // الموقع محتاج JavaScript
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);           // ملفات assets شغالة، وباقي ملفات الجهاز مقفولة
        s.setAllowContentAccess(false);
        s.setAllowFileAccessFromFileURLs(false);
        s.setAllowUniversalAccessFromFileURLs(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        s.setGeolocationEnabled(false);
        s.setSupportMultipleWindows(false);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        if (Build.VERSION.SDK_INT >= 26) s.setSafeBrowsingEnabled(true);

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                if (isAsset(u)) return false;                       // تنقل داخلي
                String sc = u.getScheme();
                boolean ok = "tel".equals(sc)
                        || ("https".equals(sc) && u.getHost() != null && LINK_HOSTS.contains(u.getHost()));
                if (ok) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) { }
                }
                return true;                                        // أي رابط تاني ممنوع
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                String sc = u.getScheme();
                if ("data".equals(sc) || isAsset(u)) return null;
                if ("https".equals(sc) && u.getHost() != null && RESOURCE_HOSTS.contains(u.getHost())) return null;
                return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream(new byte[0]));
            }
        });

        if (savedInstanceState != null) web.restoreState(savedInstanceState);
        else web.loadUrl(HOME);
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack(); else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (web != null) { web.stopLoading(); web.destroy(); }
        super.onDestroy();
    }
}
