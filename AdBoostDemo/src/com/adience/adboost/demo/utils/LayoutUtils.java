package com.adience.adboost.demo.utils;

import android.app.Activity;
import android.view.ViewGroup;

public class LayoutUtils {
    public static ViewGroup getRootLayout(Activity activity) {
        ViewGroup content = (ViewGroup)activity.findViewById(android.R.id.content);
        return (ViewGroup)content.getChildAt(0);
    }
}
