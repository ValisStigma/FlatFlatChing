package com.flatflatching.flatflatching.helpers;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.flatflatching.flatflatching.R;

public final class SnackBarStyler {

    private SnackBarStyler() {

    }
    private static final int red = R.color.red;
    private static final int green = R.color.green;
    private static final int blue = R.color.blue;
    private static final int textColor = R.color.white;
    private static View getSnackBarLayout(Snackbar snackbar) {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;
    }

    private static Snackbar colorSnackBar(Snackbar snackbar, int colorId, Context context) {
        View snackBarView = getSnackBarLayout(snackbar);
        if (snackBarView != null) {
            snackBarView.setBackgroundColor(context.getResources().getColor(colorId));
        }
        snackbar.setActionTextColor(context.getResources().getColor(textColor));
        return snackbar;
    }


    public static Snackbar info(Snackbar snackbar, Context context) {
        return colorSnackBar(snackbar, blue, context);
    }

    public static Snackbar alert(Snackbar snackbar, Context context) {
        return colorSnackBar(snackbar, red, context);
    }

    public static Snackbar confirm(Snackbar snackbar, Context context) {
        return colorSnackBar(snackbar, green, context);
    }

}
