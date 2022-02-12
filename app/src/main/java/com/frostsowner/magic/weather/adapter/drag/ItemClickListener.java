package com.frostsowner.magic.weather.adapter.drag;

import java.util.List;

/**
 * Created by yulijun on 2017/7/25.
 */

public interface ItemClickListener<T> {

    void onItemClick(List<T> item, int position);
}
