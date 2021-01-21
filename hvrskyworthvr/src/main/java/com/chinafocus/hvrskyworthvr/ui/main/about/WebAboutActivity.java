package com.chinafocus.hvrskyworthvr.ui.main.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.global.Constants;
import com.chinafocus.hvrskyworthvr.service.event.VrAboutConnect;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.chinafocus.hvrskyworthvr.global.Constants.ACTIVITY_ABOUT;

public class WebAboutActivity extends AppCompatActivity {

    public static void startWebAboutActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, WebAboutActivity.class);
        intent.putExtra(WEB_TITLE, title);
        intent.putExtra(WEB_URL, url);
        context.startActivity(intent);
    }

    private static final String WEB_URL = "web_url";
    private static final String WEB_TITLE = "web_title";

    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, true);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String url = intent.getStringExtra(WEB_URL);
        String title = intent.getStringExtra(WEB_TITLE);

        Constants.ACTIVITY_TAG = ACTIVITY_ABOUT;

        setContentView(R.layout.activity_web_about);
        findViewById(R.id.iv_mine_web_back).setOnClickListener(v -> finish());

        AppCompatTextView tvTitle = findViewById(R.id.tv_mine_web_title);
        mWebView = findViewById(R.id.web_about);
        initWebView();

        tvTitle.setText(title);
        mWebView.loadUrl(url);
    }

    @SuppressWarnings("all")
    private void initWebView() {
        //不能垂直滑动
        mWebView.setVerticalScrollBarEnabled(false);
        //不能水平滑动
        mWebView.setHorizontalScrollBarEnabled(false);
        // 用WebView去滚动的方法没软用，x5用下面方法去掉
        IX5WebViewExtension ix5 = mWebView.getX5WebViewExtension();
        if (null != ix5) {
            ix5.setScrollBarFadingEnabled(false);
            ix5.setHorizontalScrollBarEnabled(false);//水平不显示滚动按钮
            ix5.setVerticalScrollBarEnabled(false); //垂直不显示滚动按钮
        }
        WebSettings settings = mWebView.getSettings();
        //settings.setUseWideViewPort(true);//调整到适合webView的大小，不过尽量不要用，有些手机有问题
        //设置WebView是否使用预览模式加载界面。
        settings.setLoadWithOverviewMode(true);
        //支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持内容重新布局
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //允许dom操作
        settings.setDomStorageEnabled(true);
        //设置WebView属性，能够执行Javascript脚本
        settings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient());
    }

    /**
     * 在首页戴上VR眼镜
     *
     * @param event 戴上VR眼镜事件
     */
    @Subscribe()
    @SuppressWarnings("unused")
    public void connectToVR(VrAboutConnect event) {
        Log.d("MyLog", "-----在关于页面戴上VR眼镜-----");
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
    }
}