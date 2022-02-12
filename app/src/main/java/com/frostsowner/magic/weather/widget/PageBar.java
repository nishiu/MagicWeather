package com.frostsowner.magic.weather.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fm.commons.util.PixelUtils;
import com.frostsowner.magic.weather.R;


public class PageBar {

	private View view;
	private Context context;

	private int pageCount = -1;
	private int pageIndex;
	private int oldPageIndex;

	private int defaultResId;
	private int selectedResId;

	private int pageBarWidth;
	private int pageBarMargin;

	public PageBar(Context context){
		this(context,3,5);
	}

	public PageBar(Context context,int width,int margin){
		this.context = context;
		setBgResId(R.drawable.bg_page_default, R.drawable.bg_page_selected);
		setPageBarWidth(width);
		setPageBarMargin(margin);
	}

	public void setView(View view){
		this.view = view;
		pageIndex = 0;
	}
	
	public void setPageCount(int pageCount){
		this.pageCount = pageCount;
		initPageBar();
		setPageIndex(pageIndex);
	}

	public void setPageBarWidth(int width){
		pageBarWidth = width;
	}

	public void setPageBarMargin(int margin){
		pageBarMargin = margin;
	}

	public void setBgResId(int defaultResId,int selectedResId){
	    this.defaultResId = defaultResId;
	    this.selectedResId = selectedResId;
    }
	
	private void initPageBar() {
        if(pageCount <= 1){
            view.setVisibility(View.INVISIBLE);
            return;
        }
        view.setVisibility(View.VISIBLE);
		ViewGroup viewGroup = (ViewGroup) view;
		viewGroup.removeAllViews();
		for (int i = 0; i < pageCount; i++){
			ImageView imageView = new ImageView(view.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PixelUtils.dip2px(context,pageBarWidth),PixelUtils.dip2px(context,pageBarWidth));
            layoutParams.setMargins(PixelUtils.dip2px(context,pageBarMargin), 0, PixelUtils.dip2px(context,pageBarMargin), 0);
            layoutParams.gravity = Gravity.CENTER;
			imageView.setLayoutParams(layoutParams);
			imageView.setBackgroundResource(defaultResId);
			viewGroup.addView(imageView);
		}
	}
	
	public int getPageCount(){
		return pageCount;
	}

	public void setPageIndex(int index){
	    if(pageCount <= 1)return;
		oldPageIndex = pageIndex;
		pageIndex = index;
		updatePage();
	}
	
	private void updatePage() {
		ViewGroup viewGroup = (ViewGroup) view;
		if(viewGroup == null)return;
		if(viewGroup.getChildCount() <= 0)return;
		ImageView currentPage = (ImageView) viewGroup.getChildAt(pageIndex);
		ImageView oldPage = (ImageView) viewGroup.getChildAt(oldPageIndex);
		if(currentPage == null || oldPage == null)return;
        currentPage.setBackgroundResource(selectedResId);
		if(oldPageIndex != pageIndex){
            oldPage.setBackgroundResource(defaultResId);
		}
	}
}
