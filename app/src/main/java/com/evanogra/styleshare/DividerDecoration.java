package com.evanogra.styleshare;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Evan on 2/25/2016.
 */
public class DividerDecoration extends RecyclerView.ItemDecoration{
    private Drawable mDivider;

    public DividerDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    public DividerDecoration(Context context, int id) {
        mDivider = ContextCompat.getDrawable(context, id);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getPaddingRight();
        for(int i=0;i<parent.getChildCount();i++) {
            View v = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) v.getLayoutParams();
            int top = v.getBottom()+layoutParams.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
