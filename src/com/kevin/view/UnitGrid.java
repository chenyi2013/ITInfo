package com.kevin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class UnitGrid extends FrameLayout {
	public UnitGrid(Context paramContext) {
		super(paramContext);
	}

	public UnitGrid(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public UnitGrid(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		setMeasuredDimension(getDefaultSize(0, paramInt1),
				getDefaultSize(0, paramInt2));
		int i = getMeasuredWidth();
		getMeasuredHeight();
		int j = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
		super.onMeasure(j, j);
	}
}
