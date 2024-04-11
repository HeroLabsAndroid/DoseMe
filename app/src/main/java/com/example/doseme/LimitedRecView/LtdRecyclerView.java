package com.example.doseme.LimitedRecView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.Util;

public class LtdRecyclerView extends RecyclerView {

    private int maxheight = 360;


    public int getMaxheight() {
        return maxheight;
    }

    public void setMaxheight(int maxheight) {
        this.maxheight = maxheight;
    }



    public LtdRecyclerView(@NonNull Context context) {
        super(context);
    }

    public LtdRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LtdRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = MeasureSpec.makeMeasureSpec((int) Util.dpsToPx((float)maxheight, getContext()), MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}
