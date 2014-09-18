package com.yiqivr.cardswipechoose.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;

import com.yiqivr.cardswipechoose.R;

/**
 * @author lvning
 * @version create time:2014-9-16_下午2:55:51
 * @Description 卡片布局
 */
public class CardLayout extends LinearLayout {

	private static final String TAG = CardLayout.class.getSimpleName();
	// 计速器
	private VelocityTracker vTracker;
	private float mLastTouchX = 0, mLastTouchY = 0;
	// 移动距离
	private float mPosX, mPosY;
	private int layoutHeight;
	// 触发距离
	private float triggerDis;
	// 卡片移动距离
	private float cardMoveDis;
	private static final float MOVE_SLOP = 3.2f;

	private CardSwipeListener swipeListener;

	private State curState = State.NONE;

	public enum State {
		NONE, SWIPINGUP, SWIPINGDOWN, SWIPINGOCCUR
	}

	private CircleProgress topCircle, bottomCircle;
	private BounceInterpolator bounceInter;
	private AccelerateInterpolator accelerateInter;
	private static final long CIRLE_FADE_DUR = 500l;
	private static final long CARD_TRANS_DUR = 650l;
	private boolean topCircleShow = false;
	private boolean bottomCircleShow = false;

	public CardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CardLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.swipe_card, this);
		topCircle = (CircleProgress) findViewById(R.id.top_progress);
		bottomCircle = (CircleProgress) findViewById(R.id.down_progress);
		topCircle.animate().alpha(0).setDuration(CIRLE_FADE_DUR);
		bottomCircle.animate().alpha(0).setDuration(CIRLE_FADE_DUR);
		bounceInter = new BounceInterpolator();
		accelerateInter = new AccelerateInterpolator();
		post(new Runnable() {
			@Override
			public void run() {
				layoutHeight = getHeight();
				triggerDis = layoutHeight / 3.f;
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
	public boolean onTouchEvent(final MotionEvent event) {
		int action = event.getActionMasked();
		final int pointerId = 0;
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
//			Log.d("", "---- xVelocity: " + xVelocity);
			Log.d("", "---- yVeloctiy: " + yVeloctiy);

			float mx = MotionEventCompat.getX(event, pointerId);
			float my = MotionEventCompat.getY(event, pointerId);

			float dx = mx - mLastTouchX;
			float dy = my - mLastTouchY;

			mPosX += dx;
			mPosY += dy;

			mLastTouchX = mx;
			mLastTouchY = my;

			if (mPosY > 0) {
				if (curState == State.SWIPINGOCCUR)
					return false;
				curState = State.SWIPINGDOWN;
				if (!topCircleShow) {
					topCircle.animate().alpha(1).setDuration(CIRLE_FADE_DUR);
					topCircleShow = true;
				}
				// 向下滑
				int percent = Math.min(100, (int) (mPosY / triggerDis * 100));
				topCircle.setCurProgress(percent);
				// 消除slope误差
				if (bottomCircle.getCurProgress() != 0) {
					bottomCircle.setCurProgress(0);
				}
				// 同一流程上下切换
				if (bottomCircleShow) {
					bottomCircle.animate().alpha(0).setDuration(CIRLE_FADE_DUR);
					bottomCircleShow = false;
				}
				if (percent >= 100) {
					curState = State.SWIPINGOCCUR;
					if (swipeListener != null) {
						swipeListener.like();
					}
					return false;
				}
				cardMoveDis = percent * MOVE_SLOP;
				setTranslationY(cardMoveDis);
			} else if (mPosY < 0) {
				if (curState == State.SWIPINGOCCUR)
					return false;
				curState = State.SWIPINGUP;
				if (!bottomCircleShow) {
					bottomCircle.animate().alpha(1).setDuration(CIRLE_FADE_DUR);
					bottomCircleShow = true;
				}
				// 向上滑
				int percent = Math.min(100, (int) (Math.abs(mPosY) / triggerDis * 100));
				bottomCircle.setCurProgress(percent);
				if (topCircle.getCurProgress() != 0) {
					topCircle.setCurProgress(0);
				}
				if (topCircleShow) {
					topCircle.animate().alpha(0).setDuration(CIRLE_FADE_DUR);
					topCircleShow = false;
				}
				if (percent >= 100 && curState != State.SWIPINGOCCUR) {
					curState = State.SWIPINGOCCUR;
					if (swipeListener != null) {
						swipeListener.unlike();
					}
					return false;
				}
				cardMoveDis = -percent * MOVE_SLOP;
				setTranslationY(cardMoveDis);
			} else {
				curState = State.NONE;
				if (swipeListener != null) {
					swipeListener.cancel();
				}
				topCircle.setCurProgress(0);
				bottomCircle.setCurProgress(0);
			}

			Log.e("", "---- total distance mPosY = : " + mPosY);
//			Log.e("", "---- total distance mPosX = : " + mPosX);

			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (curState != State.NONE) {
				animate().setInterpolator(bounceInter).setDuration(CARD_TRANS_DUR).translationYBy(-cardMoveDis)
						.setListener(new AnimatorListener() {

							@Override
							public void onAnimationStart(Animator arg0) {

							}

							@Override
							public void onAnimationRepeat(Animator arg0) {

							}

							@Override
							public void onAnimationEnd(Animator arg0) {
								try {
									vTracker.recycle();
								} catch (Exception e) {
									e.printStackTrace();
								}
								cardMoveDis = 0;
								mPosX = 0;
								mPosY = 0;
								mLastTouchX = MotionEventCompat.getX(event, pointerId);
								mLastTouchY = MotionEventCompat.getY(event, pointerId);
								if (swipeListener != null && curState != State.SWIPINGOCCUR) {
									swipeListener.cancel();
								}
								curState = State.NONE;
								topCircle.setCurProgress(0);
								bottomCircle.setCurProgress(0);

							}

							@Override
							public void onAnimationCancel(Animator arg0) {

							}
						});
			}
			topCircle.animate().alpha(0).setDuration(CIRLE_FADE_DUR);
			bottomCircle.animate().alpha(0).setDuration(CIRLE_FADE_DUR);
			topCircleShow = false;
			bottomCircleShow = false;
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
		void like();

		/**
		 * 上滑不喜欢
		 */
		void unlike();

		void cancel();
	}
}
