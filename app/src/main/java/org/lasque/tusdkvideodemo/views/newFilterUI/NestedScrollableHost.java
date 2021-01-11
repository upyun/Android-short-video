package org.lasque.tusdkvideodemo.views.newFilterUI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL;

/**
 * TuSDK
 * $
 *
 * @author H.ys
 * @Date $ $
 * @Copyright (c) 2019 tusdk.com. All rights reserved.
 */
public class NestedScrollableHost extends FrameLayout {

    private int touchSlop = 0;
    private float initialX = 0f;
    private float initialY = 0f;
    private ViewPager2 parentViewPager;
    private View child;

    public NestedScrollableHost(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NestedScrollableHost(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        View view = (View) getParent();
        while (view != null && !(view instanceof ViewPager2)){
            view = (View) view.getParent();
        }
        parentViewPager = (ViewPager2) view;
        if (getChildCount() > 0){
            child = getChildAt(0);
        }
    }

    private boolean canChildScroll(int orientation,float delta){
        int direction = ((int) delta);
        switch (orientation){
            case 0:
                return child == null ? false : child.canScrollHorizontally(direction);
            case 1:
                return child == null ? false: child.canScrollVertically(direction);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleInterceptTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    private void handleInterceptTouchEvent(MotionEvent event){
        if (parentViewPager == null) return;
        int orientation = parentViewPager.getOrientation();

        if (!canChildScroll(orientation,-1f) && !canChildScroll(orientation,1f)){
            return;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            initialX = event.getX();
            initialY = event.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE){
            float dx = event.getX() - initialX;
            float dy = event.getY() - initialY;
            boolean isVpHorizontal = orientation == ORIENTATION_HORIZONTAL;

            float scaledDx = dx * (isVpHorizontal ? 0.5f : 1f);
            float scaledDy = dy * (isVpHorizontal ? 1f : 0.5f);

            if (scaledDx > touchSlop || scaledDy > touchSlop){
                if (isVpHorizontal == (scaledDy > scaledDx)){
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    if (canChildScroll(orientation,(isVpHorizontal ? dx : dy))){
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
            }

        }
    }
}
