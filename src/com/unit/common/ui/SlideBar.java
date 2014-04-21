package com.unit.common.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SlideBar extends View {

	private Paint paint = new Paint();
	private OnTouchLetterChangeListenner listenner;
	// 是否画出背景
	private boolean showBg = false;
	// 选中的项
	private int choose = -1;

	// 构造方法
	public SlideBar(Context context) {
		super(context);
	}

	public SlideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 获取宽和高
		int width = getWidth();
		int height = getHeight();
		// 每个字母的高度
		int singleHeight = height / list.size();
		if (showBg) {
			// 画出背景
			canvas.drawColor(Color.parseColor("#77ac98"));
		}
		// 画字母
		for (int i = 0; i < list.size(); i++) {
			paint.setColor(Color.BLACK);
			// 设置字体格式
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(20f);
			// 如果这一项被选中，则换一种颜色画
			if (i == choose) {
				paint.setColor(Color.parseColor("#afb4db"));
				paint.setFakeBoldText(true);
			}
			// 要画的字母的x,y坐标
			float posX = width / 2 - paint.measureText(list.get(i)) / 2;
			float posY = i * singleHeight + singleHeight;
			// 画出字母
			canvas.drawText(list.get(i), posX, posY, paint);
			// 重新设置画笔
			paint.reset();
		}
	}

	/**
	 * 处理SlideBar的状态
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final float y = event.getY();
		// 算出点击的字母的索引
		final int index = (int) (y / getHeight() * list.size());
		// 保存上次点击的字母的索引到oldChoose
		final int oldChoose = choose;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			showBg = true;
			if (oldChoose != index && listenner != null && index > 0
					&& index < list.size()) {
				choose = index;
				listenner.onTouchLetterChange(showBg, list.get(index));
				invalidate();
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (oldChoose != index && listenner != null && index > 0
					&& index < list.size()) {
				choose = index;
				listenner.onTouchLetterChange(showBg, list.get(index));
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			showBg = false;
			choose = -1;
			if (listenner != null) {
				if (index <= 0) {
					listenner.onTouchLetterChange(showBg, "A");
				} else if (index > 0 && index < list.size()) {
					listenner.onTouchLetterChange(showBg, list.get(index));
				} else if (index >= list.size()) {
					listenner.onTouchLetterChange(showBg, "Z");
				}
			}
			invalidate();
			break;
		}
		return true;
	}

	/**
	 * 回调方法，注册监听器
	 * 
	 * @param listenner
	 */
	public void setOnTouchLetterChangeListenner(
			OnTouchLetterChangeListenner listenner) {
		this.listenner = listenner;
	}

	/**
	 * SlideBar 的监听器接口
	 * 
	 * @author Folyd
	 * 
	 */
	public interface OnTouchLetterChangeListenner {

		void onTouchLetterChange(boolean isTouched, String s);
	}

	private List<String> list = new ArrayList<String>();

	public void setLetters(List<String> list) {
		this.list = list;
	}

}
