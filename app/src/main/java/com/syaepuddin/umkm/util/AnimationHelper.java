package com.syaepuddin.umkm.util;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class AnimationHelper {

    public static void animateSpringClick(View view, Runnable onClickAction) {
        HapticUtil.performClickFeedback(view);
        SpringAnimation animX = new SpringAnimation(view, SpringAnimation.SCALE_X, 0.92f);
        SpringAnimation animY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 0.92f);

        animX.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH);
        animX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        animY.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH);
        animY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);

        animX.start();
        animY.start();

        view.postDelayed(() -> {
            SpringAnimation restoreX = new SpringAnimation(view, SpringAnimation.SCALE_X, 1.0f);
            SpringAnimation restoreY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 1.0f);
            restoreX.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
            restoreX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
            restoreY.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
            restoreY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);

            restoreX.start();
            restoreY.start();

            if (onClickAction != null) {
                onClickAction.run();
            }
        }, 80);
    }

    public static void bounceBadge(View view) {
        SpringAnimation animX = new SpringAnimation(view, SpringAnimation.SCALE_X, 1.4f);
        SpringAnimation animY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 1.4f);

        animX.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH);
        animX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        animY.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH);
        animY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);

        animX.start();
        animY.start();

        view.postDelayed(() -> {
            SpringAnimation resetX = new SpringAnimation(view, SpringAnimation.SCALE_X, 1.0f);
            SpringAnimation resetY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 1.0f);
            resetX.start();
            resetY.start();
        }, 120);
    }

    public static void animateCountUpCurrency(TextView textView, double startValue, double endValue, long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat((float) startValue, (float) endValue);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float val = (float) animation.getAnimatedValue();
            textView.setText(CurrencyFormatter.formatRupiah(val));
        });
        animator.start();
    }

    public static void animateCountUpInteger(TextView textView, int startValue, int endValue, long duration) {
        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int val = (int) animation.getAnimatedValue();
            textView.setText(String.valueOf(val));
        });
        animator.start();
    }

    public static void fadeIn(View view, long duration) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(duration).setListener(null).start();
    }

    public static void fadeOut(View view, long duration) {
        view.animate().alpha(0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                view.setVisibility(View.GONE);
            }
        }).start();
    }
}
