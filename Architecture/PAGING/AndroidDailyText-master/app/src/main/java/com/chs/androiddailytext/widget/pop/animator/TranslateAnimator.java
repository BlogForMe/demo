package com.chs.androiddailytext.widget.pop.animator;

import android.view.View;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.chs.androiddailytext.widget.pop.XPopup;


/**
 * Description: 平移动画，不带渐变
 * Create by dance, at 2018/12/9
 */
public class TranslateAnimator extends PopupAnimator {
    //动画起始坐标
    private float startTranslationX, startTranslationY;
    private int oldWidth, oldHeight;
    private float defTranslationX, defTranslationY;

    public TranslateAnimator(View target, PopupAnimation popupAnimation) {
        super(target, popupAnimation);
    }

    private boolean isUseZero = false;

    public TranslateAnimator forceUseZero(boolean b) {
        isUseZero = b;
        return this;
    }

    @Override
    public void initAnimator() {
        defTranslationX = targetView.getTranslationX();
        defTranslationY = targetView.getTranslationY();
        // 设置起始坐标
        applyTranslation();
        startTranslationX = targetView.getTranslationX();
        startTranslationY = targetView.getTranslationY();
        oldWidth = targetView.getMeasuredWidth();
        oldHeight = targetView.getMeasuredHeight();
    }

    private void applyTranslation() {
        switch (popupAnimation) {
            case TranslateFromLeft:
                targetView.setTranslationX(-targetView.getRight());
                break;
            case TranslateFromTop:
                targetView.setTranslationY(-targetView.getBottom());
                break;
            case TranslateFromRight:
                targetView.setTranslationX(((View) targetView.getParent()).getMeasuredWidth() - targetView.getLeft());
                break;
            case TranslateFromBottom:
                targetView.setTranslationY(((View) targetView.getParent()).getMeasuredHeight() - targetView.getTop());
                break;
        }
    }

    @Override
    public void animateShow() {
        targetView.animate().translationX(isUseZero ? 0 : defTranslationX).translationY(isUseZero ? 0 : defTranslationY)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(XPopup.getAnimationDuration()).start();
    }

    @Override
    public void animateDismiss() {
        //执行消失动画的时候，宽高可能改变了，所以需要修正动画的起始值
        switch (popupAnimation) {
            case TranslateFromLeft:
                startTranslationX -= targetView.getMeasuredWidth() - oldWidth;
                break;
            case TranslateFromTop:
                startTranslationY -= targetView.getMeasuredHeight() - oldHeight;
                break;
            case TranslateFromRight:
                startTranslationX += targetView.getMeasuredWidth() - oldWidth;
                break;
            case TranslateFromBottom:
                startTranslationY += targetView.getMeasuredHeight() - oldHeight;
                break;
        }
        targetView.animate().translationX(startTranslationX).translationY(startTranslationY)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(XPopup.getAnimationDuration()).start();
    }
}
