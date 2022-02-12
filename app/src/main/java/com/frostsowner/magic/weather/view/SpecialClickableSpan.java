package com.frostsowner.magic.weather.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.frostsowner.magic.weather.activity.PrivacyHtmlActivity;
import com.frostsowner.magic.weather.activity.ServiceHtmlActivity;

public class SpecialClickableSpan extends ClickableSpan {

    private Context context;
    private String privacy;
    private String service;
    private String clickString;

    public SpecialClickableSpan(Context context, String clickString, String privacy, String service){
        this.privacy = privacy;
        this.service = service;
        this.context = context;
        this.clickString = clickString;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
//        super.updateDrawState(ds);
        ds.setUnderlineText(true);
        //给标记的部分 的文字 添加颜色
        if(clickString.equals(privacy) || clickString.equals(service)){
            ds.setColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public void onClick(@NonNull View widget) {
        if(clickString.equals(service)){
            context.startActivity(new Intent(context, ServiceHtmlActivity.class));
        }
        else if(clickString.equals(privacy)){
            context.startActivity(new Intent(context, PrivacyHtmlActivity.class));
        }
    }
}
