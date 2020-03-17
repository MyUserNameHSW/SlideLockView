package com.hsw.slidelockview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author heshuai
 * created on: 2020-03-16 14:51
 * description: 垂直滑动解锁
 *
 *  ！！！此控件仅处理滑动事件，不要在里面进行其他的UI绘制处理，否则可能会引起动画异常，通过listener回调即可
 */
public class SlideVerLockView extends RelativeLayout {

    private View ivBlock;
    private TextView tvStatus;

    //view最终为位置Y
    private int lastY;

    private int slideSize;
    private boolean isUnLocked = false;

    /**
     * 可配置项，距离顶部多少时可设置isUnLocked = true，单位为dp
     *
     */
    private int endDimen = 50;

    private OnLockListener onLockListener;

    public SlideVerLockView(Context context) {
        this(context, null);
    }

    public SlideVerLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideVerLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.ver_slide_view, this);
        ivBlock = findViewById(R.id.iv_block);
        tvStatus = findViewById(R.id.tv_status);
        tvStatus.setText("上\n划\n解\n锁");
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
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int mTop = view.getTop();
                        int mBottom = view.getBottom();
                        if (mTop < 0) {
                            mTop = 0;
                            mBottom = view.getHeight();
                        }

                        if (mBottom > getHeight()) {
                            mBottom = getHeight();
                            mTop = getHeight() - view.getHeight();
                        }
                        slideSize = (int) (event.getRawY() - lastY);
                        view.layout(view.getLeft(), mTop + slideSize, view.getRight(), mBottom + slideSize);
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //在最边缘的时候动画所需最大执行时间
                        int maxDuration = 500;
                        int maxSlide = getHeight() - view.getHeight();
                        int duration;

                        if (view.getTop() < endDimen) {
                            duration = maxDuration * view.getTop() / maxSlide;
                            animSlide(view, view.getTop(), 0, duration);
                        } else {
                            duration = maxDuration * (getHeight() - view.getBottom()) / maxSlide;
                            animSlide(view, view.getTop(), getHeight() - view.getHeight(), duration);
                        }
                        break;
                        default:break;
                }
                return true;
            }
        });
    }

    private void animSlide(final View view, final int topFrom, int topTo, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(topFrom, topTo);
        valueAnimator.removeAllListeners();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int viewTop = (int) valueAnimator.getAnimatedValue();
                if (viewTop <= 0) {
                    isUnLocked = true;
                    if (null != onLockListener) {
                        onLockListener.locked(isUnLocked);
                    }
                }
                view.layout(view.getLeft(), viewTop, view.getRight(), viewTop + view.getHeight());
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
