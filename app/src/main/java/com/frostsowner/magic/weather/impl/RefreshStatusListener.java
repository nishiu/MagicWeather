package com.frostsowner.magic.weather.impl;

public interface RefreshStatusListener {

    int REFRESHING = 101;
    int REFRESH_SUCCESS = 102;
    int REFRESH_FAILED = 103;

    void refresh(int status);
}
