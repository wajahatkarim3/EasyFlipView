package com.wajahatkarim3.easyflipview;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Wajahat on 1/4/2017.
 */

public class EasyFlipView extends FrameLayout {

    public static final int DEFAULT_FLIP_DURATION = 400;

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;
    private View mCardFrontLayout;
    private View mCardBackLayout;

    private boolean flipOnTouch;
    private int flipDuration;
    private boolean flipEnabled;

    private Context context;
    private float x1;
    private float y1;

    public EasyFlipView(Context context) {
        super(context);
        this.context = context;
        init(context, null);
    }

    public EasyFlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        // Setting Defaul Values
        flipOnTouch = true;
        flipDuration = DEFAULT_FLIP_DURATION;
        flipEnabled = true;

        // Check for the attributes
        if (attrs != null)
        {
            // Attribute initialization
            final TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.easy_flip_view, 0, 0);
            try {
                flipOnTouch = attrArray.getBoolean(R.styleable.easy_flip_view_flipOnTouch, true);
                flipDuration = attrArray.getInt(R.styleable.easy_flip_view_flipDuration, DEFAULT_FLIP_DURATION);
                flipEnabled = attrArray.getBoolean(R.styleable.easy_flip_view_flipEnabled, true);
            } finally {
                attrArray.recycle();
            }

        }
    }

    @Override
    protected void onFinishInflate ()
    {
        super.onFinishInflate();

        if (getChildCount() > 2) {
            throw new IllegalStateException("EasyFlipView can host only two direct children!");
        }

        findViews();
        loadAnimations();
        changeCameraDistance();
    }

    private void findViews() {
        mCardFrontLayout = getChildAt(0);
        mCardBackLayout = getChildAt(1);
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this.context, R.animator.animation_flip_in);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this.context, R.animator.animation_flip_out);

        setFlipDuration(flipDuration);
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardFrontLayout.setCameraDistance(scale);
        mCardBackLayout.setCameraDistance(scale);
    }

    public void flipCard() {

        if (!flipEnabled)
            return;

        if (!mIsBackVisible) {
            mSetRightOut.setTarget(mCardFrontLayout);
            mSetLeftIn.setTarget(mCardBackLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = true;
        } else {
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isEnabled() && flipOnTouch)
        {
            this.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                    float x2 = event.getX();
                    float y2 = event.getY();
                    float dx = x2 -x1;
                    float dy = y2 -y1;
                    float MAX_CLICK_DISTANCE = 0.5f;
                    if(dx < MAX_CLICK_DISTANCE && dy < MAX_CLICK_DISTANCE && dx >= 0 && dy >= 0)
                    {
                        flipCard();
                    }
                    return true;
            }
        }

        return false;
    }

    public boolean isFlipOnTouch() {
        return flipOnTouch;
    }

    public void setFlipOnTouch(boolean flipOnTouch) {
        this.flipOnTouch = flipOnTouch;
    }

    public int getFlipDuration() {
        return flipDuration;
    }

    public void setFlipDuration(int flipDuration) {
        this.flipDuration = flipDuration;
        mSetRightOut.setDuration(flipDuration);
        mSetLeftIn.setDuration(flipDuration);
    }

    public boolean isFlipEnabled() {
        return flipEnabled;
    }

    public void setFlipEnabled(boolean flipEnabled) {
        this.flipEnabled = flipEnabled;
    }
}
