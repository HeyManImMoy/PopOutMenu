package com.lynn.popoutmenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.lynn.popoutmenu.R;

/**
 * @author Lynn
 * 
 */
public class PopOutMenu extends ViewGroup implements View.OnClickListener {

	public static final int POS_LEFT_TOP = 0;
	public static final int POS_LEFT_BOTTOM = 1;
	public static final int POS_RIGHT_TOP = 2;
	public static final int POS_RIGHT_BOTTOM = 3;
	public static final int TYPE_ROUND = 0;
	public static final int TYPE_STRAIGHT = 1;
	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_RIGHT = 2;
	public static final int DIRECTION_DOWN = 3;

	private OnMenuClickListener mOnMenuClickListener;
	private int mRadius;
	private View mMainButton;
	private State mState = State.CLOSE;
	private Position mPostion = Position.RIGHT_BOTTOM;
	private Type mType = Type.ROUND;// 若无设置菜单类型，则默认为扇形.
	private Direction mDirection = Direction.LEFT;// 若无设置方向，则默认为向左弹射.

	public enum Position {
		LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
	}

	public enum State {
		OPEN, CLOSE
	}

	public enum Type {
		ROUND, STRAIGHT
	}

	public enum Direction {
		LEFT, UP, RIGHT, DOWN
	}

	/**
	 * @author Lynn callback
	 */
	public interface OnMenuClickListener {
		/**
		 * 
		 * @param view
		 *            点击的view
		 * @param position
		 *            位置
		 */
		void onClick(View view, int position);
	}

	/**
	 * 设置menu监听器 set a listener for it
	 */
	public void setOnMenuClickListener(OnMenuClickListener listener) {
		this.mOnMenuClickListener = listener;
	}

	public PopOutMenu(Context context) {
		this(context, null);
	}

	public PopOutMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PopOutMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 未半径设置默认值100dp set radius default value 100 dp
		mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources()
				.getDisplayMetrics());

		// 获取自定义属性的值 get declare-styleable value
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PopOutMenu,
				defStyle, 0);
		// 1.获取自定义属性值
		int pos = array.getInt(R.styleable.PopOutMenu_position, POS_RIGHT_BOTTOM);
		switch (pos) {
		case POS_LEFT_TOP:
			mPostion = Position.LEFT_TOP;
			break;
		case POS_LEFT_BOTTOM:
			mPostion = Position.LEFT_BOTTOM;
			break;
		case POS_RIGHT_TOP:
			mPostion = Position.RIGHT_TOP;
			break;
		case POS_RIGHT_BOTTOM:
			mPostion = Position.RIGHT_BOTTOM;
			break;
		}

		int t = array.getInt(R.styleable.PopOutMenu_type, TYPE_ROUND);
		switch (t) {
		case TYPE_ROUND:
			mType = Type.ROUND;
			break;
		case TYPE_STRAIGHT:
			mType = Type.STRAIGHT;
			break;
		}

		int d = array.getInt(R.styleable.PopOutMenu_direction, DIRECTION_LEFT);
		switch (d) {
		case DIRECTION_LEFT:
			mDirection = Direction.LEFT;
			break;
		case DIRECTION_UP:
			mDirection = Direction.UP;
			break;
		case DIRECTION_RIGHT:
			mDirection = Direction.RIGHT;
			break;
		case DIRECTION_DOWN:
			mDirection = Direction.DOWN;
			break;
		}

		// 2.获取自定义属性radius的值
		mRadius = (int) array.getDimension(R.styleable.PopOutMenu_radius, TypedValue
				.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources()
						.getDisplayMetrics()));
		Log.e("PopOutMenu", "ridius=" + mRadius + "  position=" + mPostion + "  type=" + mType
				+ "  direction=" + mDirection);
		array.recycle();// 释放 release

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int count = getChildCount();
		// 测量子view
		for (int i = 0; i < count; i++)
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			layoutMainButton();

			if (mType == Type.ROUND)
				layoutRoundMenuButtons();
			else if (mType == Type.STRAIGHT)
				layoutStraightMenuButtons();
		}
	}

	/**
	 * layout直线菜单
	 */
	private void layoutStraightMenuButtons() {
		int cCount = getChildCount();
		for (int i = 1; i < cCount; i++) {
			View cView = getChildAt(i);
			cView.setVisibility(View.GONE);
			int cWidth = cView.getMeasuredWidth();
			int cHeight = cView.getMeasuredHeight();

			int offset = mRadius / (cCount - 1) * i;
			int originL = 0;
			int originT = 0;
			int cl = 0;
			int ct = 0;
			int cr = 0;
			int cb = 0;

			switch (mPostion) {
			case LEFT_TOP:
				originL = 0;
				originT = 0;
				break;
			case RIGHT_TOP:
				originL = getMeasuredWidth() - cWidth;
				originT = 0;
				break;
			case LEFT_BOTTOM:
				originL = 0;
				originT = getMeasuredHeight() - cHeight;
				break;
			case RIGHT_BOTTOM:
				originL = getMeasuredWidth() - cWidth;
				originT = getMeasuredHeight() - cHeight;
				break;
			}

			switch (mDirection) {
			case LEFT:
				cl = originL - offset;
				ct = originT;
				break;
			case UP:
				cl = originL;
				ct = originT - offset;
				break;
			case RIGHT:
				cl = offset;
				ct = originT;
				break;
			case DOWN:
				cl = originL;
				ct = offset;
				break;
			}
			cr = cl + cWidth;
			cb = ct + cHeight;
			cView.layout(cl, ct, cr, cb);
		}
	}

	/**
	 * layout扇形菜单
	 */
	@SuppressWarnings("incomplete-switch")
	private void layoutRoundMenuButtons() {
		int count = getChildCount();
		for (int i = 1; i < count; i++) {
			View cView = getChildAt(i);
			cView.setVisibility(View.GONE);
			int cWidth = cView.getMeasuredWidth();
			int cHeight = cView.getMeasuredHeight();

			int cl = (int) (Math.sin((Math.PI / 2) / (count - 2) * (i - 1)) * mRadius);
			int ct = (int) (Math.cos((Math.PI / 2) / (count - 2) * (i - 1)) * mRadius);

			switch (mPostion) {
			case LEFT_BOTTOM:
				ct = getMeasuredHeight() - cHeight - ct;
				break;
			case RIGHT_TOP:
				cl = getMeasuredWidth() - cWidth - cl;
				break;
			case RIGHT_BOTTOM:
				ct = getMeasuredHeight() - cHeight - ct;
				cl = getMeasuredWidth() - cWidth - cl;
				break;
			}

			cView.layout(cl, ct, cl + cWidth, ct + cHeight);

		}
	}

	/**
	 * layout主button
	 */
	private void layoutMainButton() {

		if (getChildCount() <= 3) {
			throw new RuntimeException("you must put 3 child view at least!");
		}
		mMainButton = getChildAt(0);
		mMainButton.setOnClickListener(this);
		int l = 0;// left坐标
		int t = 0;// top坐标
		int width = mMainButton.getMeasuredWidth();// 主button的宽
		int height = mMainButton.getMeasuredHeight();// 主button的高
		switch (mPostion) {
		case LEFT_TOP:
			l = 0;
			t = 0;
			break;
		case LEFT_BOTTOM:
			l = 0;
			t = getMeasuredHeight() - height;
			break;
		case RIGHT_TOP:
			l = getMeasuredWidth() - width;
			t = 0;
			break;
		case RIGHT_BOTTOM:
			l = getMeasuredWidth() - width;
			t = getMeasuredHeight() - height;
			break;
		}
		mMainButton.layout(l, t, l + width, t + height);
	}

	@Override
	public void onClick(View v) {
		// 主button的点击事件
		rotateMainButton(0, 360, 300);
		toggleMenu(300);
	}

	// 设置menu的动画：旋转+平移
	/**
	 * 
	 * @param duration
	 *            时长
	 */
	public void toggleMenu(int duration) {
		if (mType == Type.ROUND)
			toggleRoundMenu(duration);
		else if (mType == Type.STRAIGHT)
			toggleStraightMenu(duration);
		changeState();

	}

	private void toggleStraightMenu(int duration) {
		int cCount = getChildCount();
		for (int i = 1; i < cCount; i++) {
			final View cView = getChildAt(i);
			cView.setVisibility(View.VISIBLE);
			AnimationSet animSet = new AnimationSet(true);

			RotateAnimation rotateAnim = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF,
					0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnim.setDuration(duration);
			rotateAnim.setFillAfter(true);

			TranslateAnimation translateAnim = null;
			int offset = mRadius / (cCount - 1) * i;
			if (mState == State.CLOSE) {// TO OPEN
				switch (mDirection) {
				case LEFT:
					translateAnim = new TranslateAnimation(offset, 0, 0, 0);
					break;
				case UP:
					translateAnim = new TranslateAnimation(0, 0, offset, 0);
					break;
				case RIGHT:
					translateAnim = new TranslateAnimation(-offset, 0, 0, 0);
					break;
				case DOWN:
					translateAnim = new TranslateAnimation(0, 0, -offset, 0);
					break;
				}
				cView.setClickable(true);
				cView.setFocusable(true);
				final int pos = i;
				cView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mOnMenuClickListener != null)
							mOnMenuClickListener.onClick(cView, pos);
						menuItemAnim(pos);
						changeState();
					}
				});
			} else if (mState == State.OPEN) {// TO CLOSE
				switch (mDirection) {
				case LEFT:
					translateAnim = new TranslateAnimation(0, offset, 0, 0);
					break;
				case UP:
					translateAnim = new TranslateAnimation(0, 0, 0, offset);
					break;
				case RIGHT:
					translateAnim = new TranslateAnimation(0, -offset, 0, 0);
					break;
				case DOWN:
					translateAnim = new TranslateAnimation(0, 0, 0, -offset);
					break;
				}
				cView.setClickable(false);
				cView.setFocusable(false);
			}
			translateAnim.setDuration(duration);
			translateAnim.setFillAfter(true);
			translateAnim.setStartOffset((i * 100) / cCount);
			animSet.addAnimation(rotateAnim);
			animSet.addAnimation(translateAnim);
			animSet.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if (mState == State.CLOSE)
						cView.setVisibility(View.GONE);
				}
			});
			cView.startAnimation(animSet);
		}
	}

	private void toggleRoundMenu(int duration) {
		int count = getChildCount();
		for (int i = 1; i < count; i++) {
			final View cView = getChildAt(i);
			cView.setVisibility(View.VISIBLE);

			int cl = (int) (Math.sin((Math.PI / 2) / (count - 2) * (i - 1)) * mRadius);
			int ct = (int) (Math.cos((Math.PI / 2) / (count - 2) * (i - 1)) * mRadius);

			int xFlag = 1;
			int yFlag = 1;

			if (mPostion == Position.LEFT_TOP || mPostion == Position.LEFT_BOTTOM) {
				xFlag = -1;
			}
			if (mPostion == Position.LEFT_TOP || mPostion == Position.RIGHT_TOP) {
				yFlag = -1;
			}

			AnimationSet set = new AnimationSet(true);
			// 1.旋转动画
			RotateAnimation rotateAnim = new RotateAnimation(0, 720,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			rotateAnim.setDuration(duration);
			rotateAnim.setFillAfter(true);
			// 2.平移动画
			TranslateAnimation translateAnim = null;

			if (mState == State.CLOSE) {// 关闭状态时，弹出menu
				translateAnim = new TranslateAnimation(cl * xFlag, 0, ct * yFlag, 0);
				cView.setClickable(true);
				cView.setFocusable(true);
				// 为menu设置点击事件
				final int position = i;
				cView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mOnMenuClickListener != null)
							mOnMenuClickListener.onClick(cView, position);
						menuItemAnim(position);
						changeState();
					}
				});
			} else {// 打开状态时，收回menu
				translateAnim = new TranslateAnimation(0, cl * xFlag, 0, ct * yFlag);
				cView.setClickable(false);
				cView.setFocusable(false);
			}
			translateAnim.setDuration(duration);
			translateAnim.setFillAfter(true);
			set.addAnimation(rotateAnim);
			set.addAnimation(translateAnim);
			set.setStartOffset((i * 100) / count);
			set.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if (mState == State.CLOSE)
						cView.setVisibility(View.GONE);
				}
			});
			cView.startAnimation(set);

		}
	}

	/**
	 * 点击item时动画
	 * 
	 * @param position
	 */
	protected void menuItemAnim(int position) {
		for (int i = 1; i < getChildCount(); i++) {
			View cView = getChildAt(i);
			if (i == position)
				cView.startAnimation(scaleBigAnim(300));
			else
				cView.startAnimation(scaleSmallAnim(300));
			cView.setClickable(false);
			cView.setFocusable(false);
		}
	}

	private Animation scaleSmallAnim(int duration) {
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);
		animSet.addAnimation(alphaAnim);
		animSet.addAnimation(scaleAnim);
		animSet.setDuration(duration);
		animSet.setFillAfter(true);
		return animSet;
	}

	private Animation scaleBigAnim(int duration) {
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);
		animSet.addAnimation(alphaAnim);
		animSet.addAnimation(scaleAnim);
		animSet.setDuration(duration);
		animSet.setFillAfter(true);
		return animSet;
	}

	private void changeState() {
		mState = (mState == State.CLOSE ? State.OPEN : State.CLOSE);
	}

	// 使主button旋转
	private void rotateMainButton(int fromDegree, int toDegree, int duration) {
		RotateAnimation animation = new RotateAnimation(fromDegree, toDegree,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		mMainButton.startAnimation(animation);
	}

	/**
	 * @return 是否处于打开状态
	 */
	public boolean isOpen() {
		return mState == State.OPEN;
	}
}
