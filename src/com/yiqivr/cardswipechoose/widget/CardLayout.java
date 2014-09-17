package com.yiqivr.cardswipechoose.widget;

import java.security.acl.LastOwnerException;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

/**
 * @author lvning
 * @version create time:2014-9-16_下午2:55:51
 * @Description 卡片布局
 */
public class CardLayout extends RelativeLayout {

	private static final String TAG = CardLayout.class.getSimpleName();
	// 计速器
	private VelocityTracker vTracker;
	private float mLastTouchX = 0, mLastTouchY = 0;
	// 移动距离
	private float mPosX, mPosY;
	private int mTouchSlop;
	private int layoutHeight;
	// 触发距离
	private float triggerDis;

	private CardSwipeListener swipeListener;
	
	private State curState = State.NONE;
	
	public enum State{
		NONE,
		SWIPINGUP,
		SWIPINGDOWN
	}

	public CardLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CardLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		ViewConfiguration vf = ViewConfiguration.get(getContext());
		mTouchSlop = vf.getScaledTouchSlop();
		post(new Runnable() {
			@Override
			public void run() {
				layoutHeight = getHeight();
				triggerDis = layoutHeight / 2.f;
				Log.e(TAG, "layoutHeight = " + layoutHeight + ", triggerDis = " + triggerDis);
			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);

		measureChildren(widthMeasureSpec, heightMeasureSpec);

		setMeasuredDimension(width, height);
	}

	@Override
	protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);

		int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = getChildAt(i);
			measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);
		}
	}

	@Override
	protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
		child.measure(parentWidthMeasureSpec, parentHeightMeasureSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		int pointerId = event.getPointerId(event.getActionIndex());

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (vTracker == null) {
				vTracker = VelocityTracker.obtain();
			} else {
				vTracker.clear();
			}
			vTracker.addMovement(event);
			final float x = MotionEventCompat.getX(event, pointerId);
			final float y = MotionEventCompat.getY(event, pointerId);
			mLastTouchX = x;
			mLastTouchY = y;

			break;
		case MotionEvent.ACTION_MOVE:
			vTracker.addMovement(event);
			vTracker.computeCurrentVelocity(100);
			float xVelocity = vTracker.getXVelocity();
			float yVeloctiy = vTracker.getYVelocity();
			Log.d("", "---- xVelocity: " + xVelocity);
			Log.d("", "---- yVeloctiy: " + yVeloctiy);

			float mx = MotionEventCompat.getX(event, pointerId);
			float my = MotionEventCompat.getY(event, pointerId);

			float dx = mx - mLastTouchX;
			float dy = my - mLastTouchY;

			mPosX += dx;
			mPosY += dy;

			mLastTouchX = mx;
			mLastTouchY = my;
			
			boolean swipingDown = yVeloctiy > 0 && mPosY > mTouchSlop;

			if (swipingDown) {
				// 向下滑
				int percent = Math.min(100, (int) (mPosY / triggerDis * 100));
				if (swipeListener != null) {
					swipeListener.like(percent);
				}
				if (percent == 100) {
					return false;
				}
				Log.v("", "==============like percent: " + percent);
			}
			
			boolean swipingUp = yVeloctiy < 0 && mPosY < 0 && Math.abs(mPosY) > mTouchSlop;
			if (swipingUp) {
				// 向下滑
				int percent = Math.min(100, (int) (Math.abs(mPosY) / triggerDis * 100));
				if (swipeListener != null) {
					swipeListener.unlike(percent);
				}
				if (percent == 100) {
					return false;
				}
				Log.v("", "==============unlike percent: " + percent);
			}

			Log.e("", "---- total distance mPosY = : " + mPosY);
			Log.e("", "---- total distance mPosX = : " + mPosX);
			
//				if (swipeListener != null) {
//					swipeListener.cancel();
//				}

			
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			vTracker.recycle();
			mPosX = 0;
			mPosY = 0;
			mLastTouchX = MotionEventCompat.getX(event, pointerId);
			mLastTouchY = MotionEventCompat.getY(event, pointerId);
			if (swipeListener != null) {
				swipeListener.cancel();
			}
			break;
		}

		return true;
	}

	public void setCardSwipeListener(CardSwipeListener swipeListener) {
		this.swipeListener = swipeListener;
	}

	public interface CardSwipeListener {
		/**
		 * 下滑喜欢
		 */
		void like(int percent);

		/**
		 * 上滑不喜欢
		 */
		void unlike(int percent);

		void cancel();
	}
}
