package com.frostsowner.magic.weather.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.base.BaseDialog;
import com.frostsowner.magic.weather.view.SpecialClickableSpan;

import butterknife.BindView;
import butterknife.OnClick;

public class PrivacyDialog extends BaseDialog {

    @BindView(R.id.simple_content)
    TextView contentText;

    private View.OnClickListener agreeClickListener;
    private View.OnClickListener unAgreeClickListener;

    public PrivacyDialog(@NonNull Context context, View.OnClickListener agreeClickListener, View.OnClickListener unAgreeClickListener) {
        super(context, R.style.dialog_full);
        this.agreeClickListener = agreeClickListener;
        this.unAgreeClickListener = unAgreeClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_privacy);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        initWidget();
    }

    private void initWidget(){
        String content = "欢迎使用“神奇天气查”！我们非常重视您的个人信息和隐私保护。在您使用“神奇天气查”服务之前，请仔细阅读<font color='#792323'><a style=\"text-decoration:none;\" href='privacy'>《隐私政策》</a></font>和<font color='#792323'><a style=\"text-decoration:none;\" href='service' >《用户服务协议》</a></font>。如果您同意政策,请点击“同意”,并开始使用我们的产品和服务,我们尽全力保护您的个人信息安全。";
        contentText.setText(Html.fromHtml(content));
        contentText.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable spannable = new SpannableString(contentText.getText());
        URLSpan[] urlSpans = spannable.getSpans(0,contentText.getText().length(),URLSpan.class);
        SpannableStringBuilder stylesBuilder = new SpannableStringBuilder(contentText.getText());
        stylesBuilder.clearSpans();
        for (URLSpan url : urlSpans) {
            SpecialClickableSpan clickableSpan = new SpecialClickableSpan(getContext(),url.getURL(),
                    "privacy","service");
            stylesBuilder.setSpan(clickableSpan, spannable.getSpanStart(url),
                    spannable.getSpanEnd(url), spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        contentText.setText(stylesBuilder);
    }

    @OnClick({R.id.btn_agree, R.id.btn_not_agree})
    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.btn_agree:
                if(agreeClickListener != null)agreeClickListener.onClick(v);
                dismiss();
                break;
            case R.id.btn_not_agree:
//                showNotice("请您阅读并同意本协议后才可以继续使用");
                if(unAgreeClickListener != null)unAgreeClickListener.onClick(v);
                dismiss();
                break;
        }
    }
}
