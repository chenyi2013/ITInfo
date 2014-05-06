package com.kevin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;

public class LinearIndexScroller extends LinearLayout {

	private ScrollIndexScroller mScroller = null;
	private SectionIndexer mAdapter;

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();

		mAdapter = new MyAdapter();
		mScroller = new ScrollIndexScroller(getContext(), this);
		mScroller.setAdapter(mAdapter);

	}

	public LinearIndexScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LinearIndexScroller(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		// Overlay index bar
		if (mScroller != null)
			mScroller.draw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null)
			mScroller.onSizeChanged(w, h, oldw, oldh);
	}

	private class MyAdapter implements SectionIndexer {

		@Override
		public int getPositionForSection(int section) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			String string = "ABCDEFGHIGKLMN";
			String[] indexer = new String[string.length()];
			for (int i = 0; i < string.length(); i++) {
				indexer[i] = String.valueOf(string.charAt(i));
			}

			return indexer;
		}

	}

}
