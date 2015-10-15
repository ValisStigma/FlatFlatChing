package com.flatflatching.flatflatching.customLayouts;

/**
 * Created by rafael on 29.09.2015.
 */


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {
    private static final double M_SCALE = 1.0;
    private static final double HALF = 0.5;
    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (width > (int)((M_SCALE * height) + HALF)) {
            width = (int)((M_SCALE * height) + HALF);
        } else {
            height = (int)((width / M_SCALE) + HALF);
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        );
    }
}

