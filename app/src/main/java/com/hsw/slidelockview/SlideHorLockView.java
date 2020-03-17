package com.hsw.slidelockview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author heshuai
 * created on: 2020-03-16 14:51
 * description:  水平滑动解锁
 *
 *  ！！！此控件仅处理滑动事件，不要在里面进行其他的UI绘制处理，否则可能会引起动画异常，通过listener回调即可
 */
public class SlideHorLockView extends RelativeLayout {

    private View ivBlock;
    private TextView tvStatus;

    //view最终为位置X
    private int lastX;

    private int slideSize;
    private boolean isUnLocked = false;

    /**
     * 可配置项，距离顶部多少时可设置isUnLocked = true，单位为dp
     *
     */
    private int endDimen = 50;

    private OnLockListener onLockListener;

    public SlideHorLockView(Context context) {
        this(context, null);
    }

    public SlideHorLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideHorLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.hor_slide_view, this);
        ivBlock = findViewById(R.id.iv_block);
        tvStatus = findViewById(R.id.tv_status);
        tvStatus.setText("右划解锁");
        endDimen = dip2px(context, endDimen);
        initView();
    }

    public void setOnLockListener(OnLockListener onLockListener) {
        this.onLockListener = onLockListener;
    }

    private void initView() {
        ivBlock.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isUnLocked) {
                            return false;
                        }
                        lastX = (int) event.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int mLeft = view.getLeft();
                        int mRight = view.getRight();
                        if (mLeft < 0) {
                            mLeft = 0;
                            mRight = view.getWidth();
                        }

                        if (mRight > getWidth()) {
                            mRight = getWidth();
                            mLeft = getWidth() - view.getWidth();
                        }
                        slideSize = (int) (event.getRawX() - lastX);
                        view.layout(mLeft + slideSize, view.getTop(), mRight + slideSize, view.getBottom());
                        lastX = (int) event.getRawX();
                        break;
                    case MotionEvent.ACTION_UP:
                        //在最边缘的时候动画所需最大执行时间
                        int maxDuration = 500;
                        int maxSlide = getWidth() - view.getWidth();
                        int duration;

                        if (getWidth() - view.getRight() < endDimen) {
                            duration = maxDuration * (getWidth() - view.getRight()) / maxSlide;
                            animSlide(view, view.getLeft(), getWidth() - view.getWidth(), duration);
                        } else {
                            duration = maxDuration * view.getLeft() / maxSlide;
                            animSlide(view, view.getLeft(), 0, duration);
                        }
                        break;
                        default:break;
                }
                return true;
            }
        });
    }

    private void animSlide(final View view, final int leftFrom, int leftTo, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(leftFrom, leftTo);
        valueAnimator.removeAllListeners();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int viewLeft = (int) valueAnimator.getAnimatedValue();
                if (viewLeft >= getWidth() - view.getWidth()) {
                    isUnLocked = true;
                    if (null != onLockListener) {
                        onLockListener.locked(isUnLocked);
                    }
                }
                view.layout(viewLeft, view.getTop(), viewLeft + view.getWidth(), view.getBottom());
            }
        });
        //为防止溢出边界时，duration时间为负值，做下0判断
        valueAnimator.setDuration(duration < 0 ? 0 : duration);
        valueAnimator.start();

    }

    public int dip2px(Context paramContext, float paramFloat) {
        return (int) (0.5F + paramFloat
                * paramContext.getResources().getDisplayMetrics().density);
    }
}
