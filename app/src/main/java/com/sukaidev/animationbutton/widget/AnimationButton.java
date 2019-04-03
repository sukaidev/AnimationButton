package com.sukaidev.animationbutton.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by sukaidev on 2019/03/28.
 * 动画效果按钮.
 */
public class AnimationButton extends View {

    // 宽高
    private int mWidth;
    private int mHeight;
    // 背景颜色
    private int mBgColor = 0xffbc7d53;
    // 文字
    private String ButtonText = "确认完成";
    // 圆角半径
    private int mRectRadius = 5;
    // 总共缩小距离
    private int mDefaultDistance;
    // 目前缩小的距离
    private int mCurrentDistance;
    // 上移距离
    private int mUpDistance = 300;
    // 按钮矩形
    private RectF mRectF = new RectF();
    // 文字矩形
    private RectF mTextRect = new RectF();

    // 圆角矩形画笔
    private Paint mRectPaint;
    // 文字画笔
    private Paint mTextPaint;
    // 对勾画笔
    private Paint mOkPaint;

    // 圆角矩形过渡到半圆矩形动画
    private ValueAnimator mToAngleAnimator;
    // 半圆矩形过渡到圆形按钮
    private ValueAnimator mToCircleAnimator;
    // View上移动画
    private ObjectAnimator mMoveToUpAnimator;
    // 绘制勾（✔）动画
    private ValueAnimator mOkAnimator;
    // 过渡动画播放时间
    private int mDuration = 1000;
    // 动画集
    private AnimatorSet mAnimatorSet = new AnimatorSet();


    // 绘制勾标志
    private boolean startDrawOk = false;
    // 绘制勾的路径
    private Path mPath = new Path();
    // 路径的长度
    private PathMeasure mPathMeasure;
    // 对路径实现绘制动画效果
    private PathEffect mPathEffect;

    // 监听器
    private AnimationButtonListener mListener;

    public void setAnimationButtonListener(AnimationButtonListener listener) {
        mListener = listener;
    }

    public AnimationButton(Context context) {
        this(context, null);
    }

    public AnimationButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });

        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mDefaultDistance = (mWidth - mHeight) / 2;

        initOk();
        initAnimators();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawRoundRect(canvas);
        drawText(canvas);

        if (startDrawOk) {
            canvas.drawPath(mPath, mOkPaint);
        }
    }

    /**
     * 初始化画笔.
     */
    private void initPaint() {
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setStrokeWidth(4);
        mRectPaint.setStyle(Paint.Style.FILL);
        mRectPaint.setAntiAlias(true);
        mRectPaint.setColor(mBgColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(40);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);

        mOkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOkPaint.setStrokeWidth(10);
        mOkPaint.setStyle(Paint.Style.STROKE);
        mOkPaint.setAntiAlias(true);
        mOkPaint.setColor(Color.WHITE);
    }

    /**
     * 绘制一个圆角矩形.
     */
    private void drawRoundRect(Canvas canvas) {
        mRectF.left = mCurrentDistance;
        mRectF.top = 0;
        mRectF.right = mWidth - mCurrentDistance;
        mRectF.bottom = mHeight;

        canvas.drawRoundRect(mRectF, mRectRadius, mRectRadius, mRectPaint);
    }

    /**
     * 绘制文字.
     */
    private void drawText(Canvas canvas) {
        mTextRect.left = 0;
        mTextRect.top = 0;
        mTextRect.right = mWidth;
        mTextRect.bottom = mHeight;
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline = (int) ((mTextRect.bottom + mTextRect.top - fontMetrics.bottom - fontMetrics.top) / 2);
        //文字绘制到整个布局的中心位置
        canvas.drawText(ButtonText, mTextRect.centerX(), baseline, mTextPaint);
    }

    /**
     * 圆角矩形过渡到半圆矩形.
     */
    private void rectToAngleAnimation() {
        mToAngleAnimator = ValueAnimator.ofInt(0, mHeight / 2);
        mToAngleAnimator.setDuration(mDuration);
        mToAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRectRadius = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    /**
     * 半圆矩形过渡到圆形按钮.
     */
    private void rectToCircleAnimation() {
        mToCircleAnimator = ValueAnimator.ofInt(0, mDefaultDistance);
        mToCircleAnimator.setDuration(mDuration);
        mToCircleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentDistance = (int) animation.getAnimatedValue();

                // 在靠拢的过程中设置文字的透明度，使文字逐渐消失的效果
                int alpha = 255 - (mCurrentDistance * 255) / mDefaultDistance;

                mTextPaint.setAlpha(alpha);

                invalidate();
            }
        });
    }

    /**
     * 按钮上移.
     */
    private void moveToUpAnimation() {
        final float curTranslationY = this.getTranslationY();
        mMoveToUpAnimator = ObjectAnimator.ofFloat(this, "translationY", curTranslationY, curTranslationY - mUpDistance);
        mMoveToUpAnimator.setDuration(mDuration);
        mMoveToUpAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    /**
     * 绘制勾（✔）.
     */
    private void drawOkAnimation() {
        mOkAnimator = ValueAnimator.ofFloat(1, 0);
        mOkAnimator.setDuration(mDuration);
        mOkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startDrawOk = true;

                float value = (float) animation.getAnimatedValue();

                mPathEffect = new DashPathEffect(new float[]{mPathMeasure.getLength(), mPathMeasure.getLength()}, value * mPathMeasure.getLength());
                mOkPaint.setPathEffect(mPathEffect);
                invalidate();
            }
        });
    }

    /**
     * 初始化动画并播放.
     */
    private void initAnimators() {
        rectToAngleAnimation();
        rectToCircleAnimation();
        moveToUpAnimation();
        drawOkAnimation();

        mAnimatorSet
                .play(mMoveToUpAnimator)
                .before(mOkAnimator)
                .after(mToCircleAnimator)
                .after(mToAngleAnimator);
    }

    /**
     * 设置勾的路径.
     */
    private void initOk() {
        //勾的路径
        mPath.moveTo(mDefaultDistance + (float) mHeight / 8 * 3, (float) mHeight / 2);
        mPath.lineTo(mDefaultDistance + (float) mHeight / 2, (float) mHeight / 5 * 3);
        mPath.lineTo(mDefaultDistance + (float) mHeight / 3 * 2, (float) mHeight / 5 * 2);

        mPathMeasure = new PathMeasure(mPath, true);

    }

    /**
     * 开启动画.
     */
    public void start(){
        mAnimatorSet.start();
    }

    /**
     * 还原动画.
     */
    public void reset() {
        startDrawOk = false;
        mRectRadius = 0;
        mCurrentDistance = 0;
        mDefaultDistance = (mWidth - mHeight) / 2;
        mTextPaint.setAlpha(255);
        setTranslationY(getTranslationY() + mUpDistance);
        invalidate();
    }

    /**
     * 监听器
     */
    public interface AnimationButtonListener {

        // 点击回调
        void onClick();

        // 动画完成回调
        void onFinish();
    }
}
