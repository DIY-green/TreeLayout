package com.cheng.treelayout.exception;

/**
 * Created by diygreen on 2017/5/28.
 * TreeLayout 中子 View （条目）类型混乱
 */

public class ItemConfusionException extends RuntimeException {

    public ItemConfusionException() {
        super();
    }

    public ItemConfusionException(String message) {
        super(message);
    }

    public static ItemConfusionException newInstance() {
        return new ItemConfusionException("TreeLayout item type confusion, ensure HEADER type or FOOTER type all in top or bottom.");
    }

    public static ItemConfusionException newInstance(String message) {
        return new ItemConfusionException(message);
    }

}
