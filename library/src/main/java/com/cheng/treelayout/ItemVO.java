package com.cheng.treelayout;

import android.view.View;

/**
 * Created by diygreen on 2017/5/28.
 */

public class ItemVO {

    float leftCentreX;
    float lefCentreY;
    int itemType;

    public static ItemVO newInstance(View view, TreeLayout.LayoutParams layoutParams) {
        ItemVO itemVO = new ItemVO();
        itemVO.leftCentreX = view.getLeft() - layoutParams.leftMargin;
        itemVO.lefCentreY = view.getTop() + view.getMeasuredHeight() / 2;
        itemVO.itemType = layoutParams.itemType;
        return itemVO;
    }

    @Override
    public String toString() {
        return "ItemVO{" +
                "leftCentreX=" + leftCentreX +
                ", lefCentreY=" + lefCentreY +
                ", itemType=" + itemType +
                '}';
    }
}
