package com.upu173.andbase.main.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.andbase.library.util.AbLogUtil;
import com.upu173.andbase.R;
import com.upu173.andbase.global.Constant;

/**
 * WEB 通用页面 不带刷新
 */
public class WebActivity extends AppCompatActivity {

    private WebView webView;
    private boolean showToolbar;
    private String url = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局
        setContentView(R.layout.activity_web);

        showToolbar = this.getIntent().getBooleanExtra("TOOLBAR",true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //设置返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!showToolbar){
            toolbar.setVisibility(View.GONE);
        }
        getSupportActionBar().setTitle("加载中...");
        url = Constant.SERVER_ADDRESS;
        AbLogUtil.e("WebView",url);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initWebView();
            }
        },100);

    }

    public void initWebView() {

        webView = new WebView(this);

        LinearLayout parentLayout = (LinearLayout)this.findViewById(R.id.parent_layout);
        parentLayout.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));


        WebSettings webSettings = webView.getSettings();
        //设置支持JavaScript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置可以支持缩放
        webSettings.setSupportZoom(true);
        //设置默认缩放方式尺寸是far
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        //设置出现缩放工具
        webSettings .setBuiltInZoomControls(false);
        webSettings.setDefaultFontSize(20);

        //CookieSyncManager.createInstance(this);
        //CookieManager cookieManager = CookieManager.getInstance();
        //cookieManager.setCookie("https://xxxxxxx", "");
        //CookieSyncManager.getInstance().sync();

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.indexOf("type=game") != -1){
                }else{
                    view.loadUrl(url);
                }
                return true;
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {


            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView webView, String title) {
                super.onReceivedTitle(webView, title);
                if(showToolbar) {
                    getSupportActionBar().setTitle(title);
                }
            }
        });

        webView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            back();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 拦截返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
            // 如果不是back键正常响应
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void back(){
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            webView.destroy();
            finish();
        }
    }
}
