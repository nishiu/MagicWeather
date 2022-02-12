package com.frostsowner.magic.weather.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.base.BaseActivity;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;

public class PrivacyHtmlActivity extends BaseActivity {

    @BindView(R.id.content_view)
    WebView contentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_html);
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
        contentView.setVerticalScrollBarEnabled(false);
        contentView.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = contentView.getSettings();
        // 如果是图片频道，则必须设置该接口为true，否则页面无法展现
        webSettings.setDomStorageEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //设置WebView自适应屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setTextZoom(100);
        //加载网页
        //当数据加载完成后隐藏缩放按钮
        webSettings.setDisplayZoomControls(false);
        loadContent();
    }

    private void loadContent(){
//        contentView.loadUrl("file:///android_asset/privacy.html");
        contentView.loadUrl("http://www.satogame.com/privacy/privacy.html");
    }
}
