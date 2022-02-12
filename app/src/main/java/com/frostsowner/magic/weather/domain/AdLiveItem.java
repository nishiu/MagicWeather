package com.frostsowner.magic.weather.domain;

import com.fm.commons.domain.BaseEntity;

public class AdLiveItem extends BaseEntity {

    public static final int TYPE_LIVE = 1;
    public static final int TYPE_AD = 2;

    private int type;
    private String content;
    private String label;
    private String status;
    private int contentColor;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getContentColor() {
        return contentColor;
    }

    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
