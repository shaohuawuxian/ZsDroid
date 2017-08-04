package com.zs.droid.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * 本类继承了ViewGroup类来实现视图的左右侧滑，可以单侧滑动也可以双侧滑动。 该类中有几个方法供外部调用。 1、initScreenSize(View
 * mainView,View leftView,View rightView) 初始化视图的方法，该方法中有三个参数分别
 * 代表了主视图和侧视图，初始化时候必须传入一个主视图和至少一个侧视图的非空对象。 2、getNowState()
 * 返回当前视图状态的方法，外部根据返回值来判断如何处理界面 3、getIsMoved()
 * 返回布尔值，表示当前的移动状态,,供外部使用，如果返回true，则表示正在移动，其他界面 应该写触控失效的代码，如点击、长按不管用。
 * 4、showLeftOrMain()、showLeftOrMain()、showMain()
 * 三个方法是供外面控制界面的状态，有时候根据getNowState 方法的判断来使用。
 * 
 * @author zhangshao
 */
public class SlidingView extends ViewGroup {
	// 界面停留的三个状态。
	public final int MAIN = 0;
	public final int LEFT = 1;
	public final int RIGHT = 2;

	// 界面侧滑的类型，1代表左侧子项，2代表右侧子项，3代表双侧子项
	private final static int LEFTTYPE = 1;
	private final static int RIGHTTYPE = 2;
	private final static int ALLTYPE = 3;

	private int now_state = MAIN;

	private final float WIDTH_RATE = 0.659f;// 停留的边界百分比
	// 手势
	private final int ACTION_WAIT = 0;
	private final int ACTION_LEFT = 1;
	private final int ACTION_RIGHT = 2;
	private int move_action = ACTION_WAIT;

	// 左中右三个界面
	private View mainView;
	private View leftView;
	private View rightView;

	private int sonType;// // 表示侧滑的类型，1代表左侧子项，2代表右侧子项，3代表双侧子项

	private int min_distance = 100;
	private int edge = 200;

	private int screen_w;
	private int screen_h;

	private boolean isAimationMoving = false;

	onStateChangeListener stateChangeListener;

	private boolean canFlip = true;


	public SlidingView(Context context, int width, int height) {
		this(context);
		this.screen_w = width;
		this.screen_h = height;
		edge = (int) (screen_w * WIDTH_RATE);
	}

	private SlidingView(Context context) {
		super(context);
	}

	private SlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private SlidingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void initView() {
		if (leftView == null) {
			sonType = RIGHTTYPE;
			this.addView(rightView, 0);
			this.addView(mainView);
		} else if (rightView == null) {
			sonType = LEFTTYPE;
			this.addView(leftView, 0);
			this.addView(mainView, 1);
		} else {
			sonType = ALLTYPE;
			this.addView(rightView, 0);
			this.addView(leftView, 1);
			this.addView(mainView, 2);
		}
	}

	private void newMove(int start) {
		int left = mainView.getLeft();
		if (now_state == MAIN) {
			if (move_action == ACTION_LEFT) {
				if (leftView != null) {
					leftView.setVisibility(View.VISIBLE);
				}
				if (rightView != null) {
					rightView.setVisibility(View.GONE);
				}
			}
			if (move_action == ACTION_RIGHT) {
				if (leftView != null) {
					leftView.setVisibility(View.GONE);
				}
				if (rightView != null) {
					rightView.setVisibility(View.VISIBLE);
				}
			}
			mainView.layout(start, 0, start + screen_w, screen_h);
		} else {
			left = edge;
			if (now_state == RIGHT) {
				left = -1 * left;
			}
			left = left + start;
			mainView.layout(left, 0, left + screen_w, screen_h);
		}
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		if (now_state == MAIN) {
			int mainLeft_x = mainView.getLeft();
			int mainRight_x = mainView.getRight();
			mainView.layout(mainLeft_x, 0, mainRight_x, screen_h);
			if (leftView != null) {
				int leftView_left = leftView.getLeft();
				int leftView_right = leftView.getRight();
				leftView.layout(leftView_left, 0, leftView_right, screen_h);
				// leftView.layout(0, 0, w, screen_h);
			}
			if (rightView != null) {
				int rightView_left = rightView.getLeft();
				int rightView_right = rightView.getRight();
				rightView.layout(rightView_left, 0, rightView_right, screen_h);
			}
		} else if (now_state == LEFT) {
			moveToShowLeft(0,false);
		} else {
			moveToShowRight(0,false);
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int w = edge;
		int width = MeasureSpec.makeMeasureSpec(w,
				MeasureSpec.EXACTLY);
		mainView.measure(widthMeasureSpec, heightMeasureSpec);
		if (leftView != null) {
			leftView.measure(width, heightMeasureSpec);
		}
		if (rightView != null) {
			rightView.measure(width, heightMeasureSpec);
		}
	}

	private int start_x;// 启始时候的x，y值
	private int start_y;
	private boolean move_first = true;// 第一次移动的标志，移动之后值变为false；
	private boolean isMoved;// 是否移动的标志

	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 如果返回true，将直接走对应的touch方法，不会再走true事件之后的事件。比如，down的是 返回true，将直接去走touch中的方法。
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (!canFlip) {
			return super.onInterceptTouchEvent(event);
		}
		if (isAimationMoving) {
			return true;
		} else {
			// int action=event.getAction();
			int x = (int) event.getX();
			int y = (int) event.getY();
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				start_x = x;// 设置按下时候的start_x的值
				start_y = y;// 设置按下时候的start_y的值
				isMoved = false;
				move_first = true;
				if (now_state == LEFT && start_x >= edge) {
					return !super.onInterceptTouchEvent(event);
				}
				if (now_state == RIGHT && start_x <= screen_w - edge) {
					return !super.onInterceptTouchEvent(event);
				}
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				int last_x = x;
				int last_y = y;
				int move_x = last_x - start_x;
				int move_y = last_y - start_y;
				int mainView_left = mainView.getLeft();
				int mainView_right = mainView.getRight();
				if (move_first) {
					if (Math.abs(move_y) > Math.abs(move_x)) {
						move_first = false;
						return super.onInterceptTouchEvent(event);// 小米手机直接返回false,不能截取子项
					}
					if (Math.abs(move_x) < 20) {
						return super.onInterceptTouchEvent(event);// 小米手机直接返回false,不能截取子项
					}
					// 点击在侧栏上
					if (start_x > mainView_right || start_x < mainView_left) {
						return super.onInterceptTouchEvent(event);// 小米手机直接返回false,不能截取子项
					}
					return true;
				}
				if (!move_first) {
					return super.onInterceptTouchEvent(event);
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
			}
			return super.onInterceptTouchEvent(event);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!canFlip) {
			return true;
		}
		if (isAimationMoving) {
			return true;
		} else {
			int x = (int) event.getX();
			int y = (int) event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				start_x = x;// 设置按下时候的start_x的值
				start_y = y;// 设置按下时候的start_y的值
				isMoved = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int last_x = x;
				int last_y = y;
				int move_x = last_x - start_x;
				int move_y = last_y - start_y;
				;
				/**
				 * 判断 move_x不能0的原因是：小米手机点击也会走ACTION_MOVE事件。原生系统不会走
				 */
				if (move_x != 0 && move_x * move_x + move_y * move_y > 300) {
					// 移动之前的设置
					if (sonType == LEFTTYPE) { // 只有左侧边栏
						if (now_state == MAIN) {
							if (move_x > 20) {
								move_action = ACTION_LEFT;
							}
							if (move_x <= 0) {
								move_x = 0;
							}
							if (move_x >= edge) {
								move_x = edge;
							}
						} else if (now_state == LEFT) {// 左侧界面状态
							if (start_x < edge) {
								isMoved = false;
								break;
							}
							if (move_x >= 0) {
								move_x = 0;
							}
							if (move_x < -edge) {
								move_x = -edge;
							}
						}
					} else if (sonType == RIGHTTYPE) {// 只有右侧边栏
						if (now_state == MAIN) {
							if (move_x < -20) {
								move_action = ACTION_RIGHT;
							}
							if (move_x >= 0) {
								move_x = 0;
							}
							if (move_x <= -edge) {
								move_x = -edge;
							}

						} else if (now_state == RIGHT) {// 右侧界面状态
							if (start_x > screen_w - edge) {
								isMoved = false;
								break;
							}
							if (move_x <= 0) {
								move_x = 0;
							}
							if (move_x >= edge) {
								move_x = edge;
							}
						}
					} else {// 有两个侧边栏
						if (now_state == MAIN) { // 主界面状态̬
							if (move_x > 20) {
								move_action = ACTION_LEFT;
							}
							if (move_x < -20) {
								move_action = ACTION_RIGHT;
							}
							if (move_action == ACTION_LEFT) {
								if (move_x >= edge) {
									move_x = edge;
								}
							}
							if (move_action == ACTION_RIGHT) {
								if (move_x <= -edge) {
									move_x = -edge;
								}
							}
						} else if (now_state == LEFT) {// 左侧界面状态
							if (start_x < edge) {
								isMoved = false;
								break;
							}
							if (move_x >= 0) {
								move_x = 0;
							}
							if (move_x <= -edge) {
								move_x = -edge;
							}
						} else if (now_state == RIGHT) {// 右侧界面状态
							if (start_x > screen_w - edge) {
								isMoved = false;
								break;
							}
							if (move_x <= 0) {
								move_x = 0;
							}
							if (move_x >= edge) {
								move_x = edge;
							}
						}
					}
					isMoved = true;
					newMove(move_x);
				} else {
					isMoved = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				int up_x = (int) event.getX();
				int up_move_x = up_x - start_x;
				if (isMoved) {
					if (now_state == MAIN) {
						if (move_action == ACTION_LEFT) {
							if (up_move_x > min_distance) {
								this.moveToShowLeft(edge-up_move_x,false);
							} else {
								this.moveToShowMain(up_move_x,false);
							}
						}
						if (move_action == ACTION_RIGHT) {
							if (up_move_x < -min_distance) {
								this.moveToShowRight(edge+up_move_x,false);
							} else {
								this.moveToShowMain(up_move_x,false);
							}
						}
					} else if (now_state == LEFT) {
						if (up_move_x < -min_distance) {
							this.moveToShowMain(edge+up_move_x,false);
						} else {
							this.moveToShowLeft(-up_move_x,false);
						}
					} else if (now_state == RIGHT) {
						if (up_move_x > min_distance) {
							this.moveToShowMain(up_move_x-edge,false);
						} else {
							this.moveToShowRight(up_move_x,false);
						}
					}
					isMoved = false;
				} else {
					if (now_state == LEFT && start_x >= edge) {
						this.showLeftOrMain();
					}
					if (now_state == RIGHT && start_x <= screen_w - edge) {
						this.showRightOrMain();
					}
				}
				break;
			}
			return true;// 返回true，否则上面的返回true，就不走其他类型了
		}
	}

	/**
	 * 向右滑动，出现左边菜单栏 ，参数b为boolean类型，false代表手动滑动，true代表直接跳到目标位置
	 * 
	 * @param b
	 */
	private void moveToShowLeft(int currentX,boolean b) {
		int move_x = edge;
		if (!b) {
			if (leftView != null) {
				if (leftView.getVisibility() == View.GONE) {
					leftView.setVisibility(View.VISIBLE);
				}
				leftView.layout(0, 0, move_x, screen_h);
				mainView.layout(move_x, 0, move_x + screen_w, screen_h);
				now_state = LEFT;
				AnimationForMove animation = new AnimationForMove(-currentX, 0);
				mainView.setAnimation(animation);
				// this.requestLayout();
				if (this.stateChangeListener != null) {
					this.stateChangeListener.stateChange();
				}
			}
			if (rightView != null) {
				rightView.setVisibility(View.GONE);
				rightView.layout(screen_w, 0, move_x + screen_w, screen_h);
			}
		} else {
			leftView.setVisibility(View.VISIBLE);
			if (rightView != null) {
				rightView.setVisibility(View.GONE);
				rightView.layout(screen_w, 0, move_x + screen_w, screen_h);
			}
			now_state = LEFT;
			mainView.layout(move_x, 0, screen_w + move_x, screen_h);

		}
	}

	/**
	 * 向左滑动，出现左边菜单栏 ，参数b为boolean类型，false代表手动滑动，true代表直接跳到目标位置
	 * 
	 * @param b
	 */
	private void moveToShowRight(int currentX,boolean b) {
		int move_x = edge * -1;
		if (!b) {
			if (leftView != null) {
				leftView.setVisibility(View.GONE);
				leftView.layout(screen_w + 2 * move_x, 0, move_x + screen_w,
						screen_h);
			}
			if (rightView != null) {
				if (rightView.getVisibility() == View.GONE) {
					rightView.setVisibility(View.VISIBLE);
				}
				rightView.layout(screen_w + move_x, 0, screen_w, screen_h);
				mainView.layout(move_x, 0, move_x + screen_w, screen_h);
				now_state = RIGHT;
				// this.requestLayout();
				if (this.stateChangeListener != null) {
					this.stateChangeListener.stateChange();
				}
				AnimationForMove animation = new AnimationForMove(currentX, 0);
				mainView.setAnimation(animation);
			}

		} else {
			if (leftView != null) {
				leftView.setVisibility(View.GONE);
				leftView.layout(screen_w + 2 * move_x, 0, move_x + screen_w,
						screen_h);
			}
			rightView.setVisibility(View.VISIBLE);
			now_state = RIGHT;
			mainView.layout(move_x, 0, screen_w + move_x, screen_h);

		}
	}

	/**
	 * 关闭菜单栏，回到主界面，参数b为boolean类型，false代表手动滑动，true代表直接跳到目标位置
	 * 
	 * @param b
	 */
	private void moveToShowMain(int currentX,boolean b) {
		final int w = edge;
		if (!b) {
			mainView.layout(0, 0, screen_w, screen_h);
			now_state = MAIN;
			AnimationForMove animation = new AnimationForMove(currentX, 0);
			mainView.setAnimation(animation);
			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if (leftView != null) {
						leftView.setVisibility(View.VISIBLE);
						leftView.layout(0, 0, w, screen_h);
					}
					if (rightView != null) {
						rightView.setVisibility(View.VISIBLE);
						rightView.layout(screen_w - w, 0, screen_w, screen_h);
					}
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});


			if (this.stateChangeListener != null) {
				this.stateChangeListener.stateChange();
			}

		} else {
			if (rightView != null) {
				rightView.layout(screen_w - w, 0, screen_w, screen_h);
			}
			if (leftView != null) {
				leftView.layout(0, 0, w, screen_h);
			}
			now_state = MAIN;
			mainView.layout(0, 0, screen_w, screen_h);

		}
	}

	/**
	 * 初始化侧滑控件，必须传入主界面和至少一个子View。 参数分别为主View，左边子View和右边子view,
	 * 
	 * @param mainView
	 * @param leftView
	 * @param rightView
	 */
	public void initScreenSize(View mainView, View leftView, View rightView) {

		this.leftView = leftView;
		this.rightView = rightView;
		this.mainView = mainView;
		this.setKeepScreenOn(true);
		min_distance = (int) (screen_w / 5.0);
		initView();
		moveToShowMain(0,false);
		// 给主界面添加监听，防止往下传事件
		mainView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}

	/**
	 * 初始化侧滑控件，必须传入主界面和至少一个子View。 参数分别为主View，左边子View和右边子view,
	 * 
	 * @param mainView
	 * @param leftView
	 * @param rightView
	 */
	public void initScreenSize(View mainView, View leftView, View rightView,
			int flag) {
		this.leftView = leftView;
		this.rightView = rightView;
		this.mainView = mainView;
		this.setKeepScreenOn(true);
		min_distance = (int) (screen_w / 5.0);
		initView();
		if (flag == 0) {
			moveToShowMain(0,false);
		} else if (flag == 1) {
			moveToShowLeft(edge,false);
		} else {
			moveToShowRight(edge,false);
		}

		// 给主界面添加监听，防止往下传事件
		mainView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}

	public void initRightView(View rView) {
		if (rView == null && rightView == null) {
			return;
		}
		if (leftView == null) {
			if (rView == null) {
				return;
			}
			rightView = rView;
			this.removeViewAt(0);
			this.addView(rightView, 0);
			sonType = RIGHTTYPE;
		} else {
			if (rightView == null) {
				rightView = rView;
				this.addView(rightView, 0);
				sonType = ALLTYPE;
			} else {
				if (rView == null) {
					this.removeViewAt(0);
					rightView = null;
					sonType = LEFTTYPE;
				} else {
					this.removeViewAt(0);
					rightView = rView;
					this.addView(rightView, 0);
					sonType = ALLTYPE;
				}
			}
		}
	}

	/**
	 * 界面左滑和回到主界面的方法，供外部调用
	 */
	public void showLeftOrMain() {
		if (now_state == MAIN) {
			moveToShowLeft(edge,true);
		} else {
			moveToShowMain(0,true);
		}
	}

	/**
	 * 界面右滑和回到主界面的方法，供外部使用
	 */
	public void showRightOrMain() {
		if (now_state == MAIN) {
			moveToShowRight(edge,true);
		} else {
			moveToShowMain(0,true);
		}
	}

	/**
	 * 回到主界面的方法，供外部使用
	 */
	public void showMain() {
		moveToShowMain(0,true);
	}

	/**
	 * 获取当前界面的状态，有三个状态： MAIN代表主界面状态 LEFT代表左边菜单被拉开 RIGHT代表右侧菜单被拉开 获取状态来判断界面接下来的操作
	 * 
	 * @return int
	 */
	public int getNowState() {
		return this.now_state;
	}

	public void setCanFlip(boolean can) {
		// canFlip=can;
		canFlip = true;
	}


	public boolean getCanFlip() {
		return canFlip;
	}

	/**
	 * 返回界面是否在移动的状态。主要用于长按按钮的一些判断
	 * 
	 * @return 布尔值
	 */
	public boolean getIsMoved() {
		return isMoved;
	}

	public void setOnStateChangeListener(
			onStateChangeListener stateChangeListener) {
		this.stateChangeListener = stateChangeListener;
	}

	// 内部接口
	public interface onStateChangeListener {
		boolean stateChange();// 检测状态改变的方法
	}

	public void clearChild() {
		if (this.getChildCount() != 0) {
			this.removeAllViews();
		}
	}

	private static class AnimationForMove extends Animation {

		private float fromX;
		private float toX;

		public AnimationForMove(float fromX, float toX) {
			this.toX = toX;
			this.fromX = fromX;
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
				int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
			setDuration(80);
			setFillAfter(true);
			setInterpolator(new LinearInterpolator());
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			final Matrix matrix = t.getMatrix();
			float dx = (toX - fromX) * interpolatedTime + fromX;
			matrix.postTranslate(dx, 0);
		}

	}// 动画类结束

}
