package com.zs.droid.widget.singlenewsview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by zhangshao on 2016/12/26.
 * 单条咨询轮播图
 */

public class SingleNewsView extends RelativeLayout {

    @IntDef({VERTICAL, HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    private @interface OrientationType {
    }

    public static final int VERTICAL = 0x01;//纵向滚动
    public static final int HORIZONTAL = 0x02;//横向滚动
    private int mOrientation = VERTICAL;
    private long mAnimationDuration = 400;//动画执行时间
    private long mInterval = 3000;//默认间隔时间
    private boolean isMoveing = false;//正在执行动画
    SingleNewsViewAdapter mAdapter;
    View showingView, gongingView;//两个view循环使用
    private int moveHeight = 0;
    private int moveWidth = 0;
    private int currentPosition = 0;
    private int gongPosition = 1;

    private Handler mHandler = null;
    private Runnable autoRunnable = null;
    ObjectAnimator objectAnimator = null;

    public SingleNewsView(Context context) {
        super(context);
    }

    public SingleNewsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleNewsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(SingleNewsViewAdapter adapter) {
        stopAutoPlay();
        final SingleNewsViewAdapter oldAdapter = mAdapter;
        showingView = null;
        gongingView = null;
        mAdapter = adapter;
        createViews();
        if (oldAdapter != null) {
            oldAdapter.destroy();
        }
    }

    public void setOrientation(@OrientationType int orientation) {
        mOrientation = orientation;
    }


    /**
     * 设置需要展示的item 位置
     *
     * @param pos
     */
    public void setCurrentPosition(int pos) {
        currentPosition = pos;
        gongPosition = currentPosition + 1;
        changePosition();
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    private void changePosition() {
        if (mAdapter != null) {
            final int totalCount = mAdapter.getCount();
            if (totalCount == 0) {
                currentPosition = 0;
                gongPosition = 0;
                return;
            }
            currentPosition = currentPosition % totalCount;
            gongPosition = (currentPosition + 1) % totalCount;
        }
    }

    private void createViews() {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            changePosition();
            showingView = mAdapter.getView(currentPosition, showingView);
            gongingView = mAdapter.getView(gongPosition, gongingView);
            removeAllViews();
            if (showingView != null) {
                addView(showingView);
            }
            if (gongingView != null) {
                addView(gongingView);
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mOrientation == VERTICAL) {
            final int parentViewH = getMeasuredHeight();
            int childViewH, top, bottom;
            if (showingView != null) {
                childViewH = showingView.getMeasuredHeight();
                top = (parentViewH - childViewH) / 2 - moveHeight;
                bottom = top + childViewH;
                showingView.layout(getPaddingLeft(), top, getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), bottom);
            }
            if (gongingView != null) {
                childViewH = gongingView.getMeasuredHeight();
                top = (parentViewH - childViewH) / 2 + parentViewH - moveHeight;
                bottom = top + childViewH;
                gongingView.layout(getPaddingLeft(), top, getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), bottom);
            }
        } else {
            final int parentViewW = getMeasuredWidth();
            int childViewW, left, right;
            int top, bottom;
            if (showingView != null) {
                childViewW = showingView.getMeasuredWidth();
                left = (parentViewW - childViewW) / 2 - moveWidth;
                right = left + childViewW;
                top = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - showingView.getMeasuredHeight()) / 2;
                bottom = top + showingView.getMeasuredHeight();
                showingView.layout(left, top, right, bottom);
            }
            if (gongingView != null) {
                childViewW = gongingView.getMeasuredWidth();
                left = (parentViewW - childViewW) / 2 + parentViewW - moveWidth;
                right = left + childViewW;
                top = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - gongingView.getMeasuredHeight()) / 2;
                bottom = top + gongingView.getMeasuredHeight();
                gongingView.layout(left, top, right, bottom);
            }
        }

    }

    /**
     * 设置轮播时间
     *
     * @param interval
     */
    public void setInterval(long interval) {
        this.mInterval = interval;
    }

    /**
     * 动画持续时间
     *
     * @param duration
     */
    public void setAnimationDuration(long duration) {
        mAnimationDuration = duration;
    }

    public void autoPlay() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        if (autoRunnable == null) {
            autoRunnable = new Runnable() {
                @Override
                public void run() {
                    if (!isMoveing) {
                        animation();
                        mHandler.postDelayed(this, mInterval);
                    }
                }
            };
        }
        mHandler.postDelayed(autoRunnable, mInterval);
    }

    private void stopAutoPlay() {
        if (mHandler != null && autoRunnable != null) {
            mHandler.removeCallbacks(autoRunnable);
        }
        if (objectAnimator != null && objectAnimator.isRunning()) {
            objectAnimator.cancel();
        }
    }

    public void onPause() {
        stopAutoPlay();
    }

    public void onResume() {
        if (mAdapter != null && mAdapter.getCount() > 1) {
            autoPlay();
        }

    }

    /**
     * 销毁
     */
    public void onDestroy() {
        stopAutoPlay();
        if (mAdapter != null) {
            mAdapter.destroy();
        }
    }

    /**
     * 动画使用,混淆时候需要keep
     *
     * @param height
     */
    private void setMoveHeight(int height) {
        moveHeight = height;
        requestLayout();
    }
    private int getMoveHeight(){
        return moveHeight;
    }

    /**
     * 动画使用,混淆时候需要keep
     *
     * @param width
     */
    private void setMoveWidth(int width) {
        moveWidth = width;
        requestLayout();
    }
    private int getMoveWidth(){
        return moveWidth;
    }

    private void animation() {
        if (objectAnimator == null) {
            if (mOrientation == VERTICAL) {
                objectAnimator = ObjectAnimator.ofInt(this, "moveHeight", getMeasuredHeight());
            } else {
                objectAnimator = ObjectAnimator.ofInt(this, "moveWidth", getMeasuredWidth());
            }
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isMoveing = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    changeViews();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    changeViews();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (objectAnimator.isRunning()) {
            objectAnimator.cancel();
        }
        objectAnimator.setDuration(mAnimationDuration);
        objectAnimator.start();
    }

    private void changeViews() {
        isMoveing = false;
        currentPosition = gongPosition;
        gongPosition++;
        changePosition();
        View nextView = mAdapter.getView(gongPosition, showingView);
        removeView(showingView);
        showingView = gongingView;
        gongingView = nextView;
        addView(gongingView);
        moveHeight = 0;
        moveWidth = 0;
    }
}
