package com.wajahatkarim3.easyflipview;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;


/**
 * A quick and easy flip view through which you can create views with two sides like credit cards,
 * poker cards, flash cards etc.
 * <p>
 * Add com.wajahatkarim3.easyflipview.EasyFlipView into your XML layouts with two direct children
 * views and you are done!
 * For more information, check http://github.com/wajahatkarim3/EasyFlipView
 *
 * @author Wajahat Karim (http://wajahatkarim.com)
 * @version 3.0.0 28/03/2019
 */
public class EasyFlipView extends FrameLayout {

    public static final String TAG = EasyFlipView.class.getSimpleName();

    public static final int DEFAULT_FLIP_DURATION = 400;
    public static final int DEFAULT_AUTO_FLIP_BACK_TIME = 1000;
    private int animFlipHorizontalOutId = R.animator.animation_horizontal_flip_out;
    private int animFlipHorizontalInId = R.animator.animation_horizontal_flip_in;
    private int animFlipHorizontalRightOutId = R.animator.animation_horizontal_right_out;
    private int animFlipHorizontalRightInId = R.animator.animation_horizontal_right_in;
    private int animFlipVerticalOutId = R.animator.animation_vertical_flip_out;
    private int animFlipVerticalInId = R.animator.animation_vertical_flip_in;
    private int animFlipVerticalFrontOutId = R.animator.animation_vertical_front_out;
    private int animFlipVerticalFrontInId = R.animator.animation_vertical_flip_front_in;

    public enum FlipState {
        FRONT_SIDE, BACK_SIDE
    }

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private AnimatorSet mSetTopOut;
    private AnimatorSet mSetBottomIn;
    private boolean mIsBackVisible = false;
    private View mCardFrontLayout;
    private View mCardBackLayout;
    private String flipType = "vertical";
    private String flipTypeFrom = "right";


    private boolean flipOnTouch;
    private int flipDuration;
    private boolean flipEnabled;
    private boolean flipOnceEnabled;
    private boolean autoFlipBack;
    private int autoFlipBackTime;

    private Context context;
    private float x1;
    private float y1;

    private FlipState mFlipState = FlipState.FRONT_SIDE;

    private OnFlipAnimationListener onFlipListener = null;

    private GestureDetectorCompat gestureDetector;

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

    private void init(Context context, AttributeSet attrs) {
        // Setting Default Values
        flipOnTouch = true;
        flipDuration = DEFAULT_FLIP_DURATION;
        flipEnabled = true;
        flipOnceEnabled = false;
        autoFlipBack = false;
        autoFlipBackTime = DEFAULT_AUTO_FLIP_BACK_TIME;

        // Check for the attributes
        if (attrs != null) {
            // Attribute initialization
            final TypedArray attrArray =
                    context.obtainStyledAttributes(attrs, R.styleable.easy_flip_view, 0, 0);
            try {
                flipOnTouch = attrArray.getBoolean(R.styleable.easy_flip_view_flipOnTouch, true);
                flipDuration = attrArray.getInt(R.styleable.easy_flip_view_flipDuration, DEFAULT_FLIP_DURATION);
                flipEnabled = attrArray.getBoolean(R.styleable.easy_flip_view_flipEnabled, true);
                flipOnceEnabled = attrArray.getBoolean(R.styleable.easy_flip_view_flipOnceEnabled, false);
                autoFlipBack = attrArray.getBoolean(R.styleable.easy_flip_view_autoFlipBack, false);
                autoFlipBackTime = attrArray.getInt(R.styleable.easy_flip_view_autoFlipBackTime, DEFAULT_AUTO_FLIP_BACK_TIME);
                flipType = attrArray.getString(R.styleable.easy_flip_view_flipType);
                flipTypeFrom = attrArray.getString(R.styleable.easy_flip_view_flipFrom);

                if (TextUtils.isEmpty(flipType)) {
                    flipType = "vertical";
                }
                if (TextUtils.isEmpty(flipTypeFrom)) {
                    flipTypeFrom = "left";
                }
                //animFlipInId = attrArray.getResourceId(R.styleable.easy_flip_view_animFlipInId, R.animator.animation_horizontal_flip_in);
                //animFlipOutId = attrArray.getResourceId(R.styleable.easy_flip_view_animFlipOutId, R.animator.animation_horizontal_flip_out);
            } finally {
                attrArray.recycle();
            }
        }

        loadAnimations();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() > 2) {
            throw new IllegalStateException("EasyFlipView can host only two direct children!");
        }

        findViews();
        changeCameraDistance();
        setupInitializations();
        initGestureDetector();
    }

    @Override
    public void addView(View v, int pos, ViewGroup.LayoutParams params) {
        if (getChildCount() == 2) {
            throw new IllegalStateException("EasyFlipView can host only two direct children!");
        }

        super.addView(v, pos, params);

        findViews();
        changeCameraDistance();
    }

    @Override
    public void removeView(View v) {
        super.removeView(v);

        findViews();
    }

    @Override
    public void removeAllViewsInLayout() {
        super.removeAllViewsInLayout();

        // Reset the state
        mFlipState = FlipState.FRONT_SIDE;

        findViews();
    }

    private void findViews() {
        // Invalidation since we use this also on removeView
        mCardBackLayout = null;
        mCardFrontLayout = null;

        int childs = getChildCount();
        if (childs < 1) {
            return;
        }

        if (childs < 2) {
            // Only invalidate flip state if we have a single child
            mFlipState = FlipState.FRONT_SIDE;

            mCardFrontLayout = getChildAt(0);
        } else if (childs == 2) {
            mCardFrontLayout = getChildAt(1);
            mCardBackLayout = getChildAt(0);
        }

        if (!isFlipOnTouch()) {
            mCardFrontLayout.setVisibility(VISIBLE);

            if (mCardBackLayout != null) {
                mCardBackLayout.setVisibility(GONE);
            }
        }
    }

    private void setupInitializations()
    {
        mCardBackLayout.setVisibility(View.GONE);
    }

    private void initGestureDetector() {
        gestureDetector = new GestureDetectorCompat(this.context, new SwipeDetector());
    }

    private void loadAnimations() {
        if (flipType.equalsIgnoreCase("horizontal")) {

            if (flipTypeFrom.equalsIgnoreCase("left")) {
                mSetRightOut =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipHorizontalOutId);
                mSetLeftIn =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipHorizontalInId);
            } else {
                mSetRightOut =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipHorizontalRightOutId);
                mSetLeftIn =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipHorizontalRightInId);
            }


            if (mSetRightOut == null || mSetLeftIn == null) {
                throw new RuntimeException(
                        "No Animations Found! Please set Flip in and Flip out animation Ids.");
            }

            mSetRightOut.removeAllListeners();
            mSetRightOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    if (mFlipState == FlipState.FRONT_SIDE) {
                        mCardBackLayout.setVisibility(GONE);
                        mCardFrontLayout.setVisibility(VISIBLE);

                        if (onFlipListener != null)
                            onFlipListener.onViewFlipCompleted(EasyFlipView.this, FlipState.FRONT_SIDE);
                    } else {
                        mCardBackLayout.setVisibility(VISIBLE);
                        mCardFrontLayout.setVisibility(GONE);

                        if (onFlipListener != null)
                            onFlipListener.onViewFlipCompleted(EasyFlipView.this, FlipState.BACK_SIDE);

                        // Auto Flip Back
                        if (autoFlipBack == true)
                        {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    flipTheView();
                                }
                            }, autoFlipBackTime);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            setFlipDuration(flipDuration);
        } else {

            if (!TextUtils.isEmpty(flipTypeFrom) && flipTypeFrom.equalsIgnoreCase("front")) {
                mSetTopOut = (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipVerticalFrontOutId);
                mSetBottomIn =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipVerticalFrontInId);
            } else {
                mSetTopOut = (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipVerticalOutId);
                mSetBottomIn =
                        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipVerticalInId);
            }

            if (mSetTopOut == null || mSetBottomIn == null) {
                throw new RuntimeException(
                        "No Animations Found! Please set Flip in and Flip out animation Ids.");
            }

            mSetTopOut.removeAllListeners();
            mSetTopOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    if (mFlipState == FlipState.FRONT_SIDE) {
                        mCardBackLayout.setVisibility(GONE);
                        mCardFrontLayout.setVisibility(VISIBLE);

                        if (onFlipListener != null)
                            onFlipListener.onViewFlipCompleted(EasyFlipView.this, FlipState.FRONT_SIDE);
                    } else {
                        mCardBackLayout.setVisibility(VISIBLE);
                        mCardFrontLayout.setVisibility(GONE);

                        if (onFlipListener != null)
                            onFlipListener.onViewFlipCompleted(EasyFlipView.this, FlipState.BACK_SIDE);

                        // Auto Flip Back
                        if (autoFlipBack == true)
                        {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    flipTheView();
                                }
                            }, autoFlipBackTime);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            setFlipDuration(flipDuration);
        }
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;

        if (mCardFrontLayout != null) {
            mCardFrontLayout.setCameraDistance(scale);
        }
        if (mCardBackLayout != null) {
            mCardBackLayout.setCameraDistance(scale);
        }
    }

    /**
     * Play the animation of flipping and flip the view for one side!
     */
    public void flipTheView() {
        if (!flipEnabled || getChildCount() < 2) return;

        if (flipOnceEnabled && mFlipState == FlipState.BACK_SIDE)
            return;

        if (flipType.equalsIgnoreCase("horizontal")) {
            if (mSetRightOut.isRunning() || mSetLeftIn.isRunning()) return;

            mCardBackLayout.setVisibility(VISIBLE);
            mCardFrontLayout.setVisibility(VISIBLE);

            if (mFlipState == FlipState.FRONT_SIDE) {
                // From front to back
                mSetRightOut.setTarget(mCardFrontLayout);
                mSetLeftIn.setTarget(mCardBackLayout);
                mSetRightOut.start();
                mSetLeftIn.start();
                mIsBackVisible = true;
                mFlipState = FlipState.BACK_SIDE;
            } else {
                // from back to front
                mSetRightOut.setTarget(mCardBackLayout);
                mSetLeftIn.setTarget(mCardFrontLayout);
                mSetRightOut.start();
                mSetLeftIn.start();
                mIsBackVisible = false;
                mFlipState = FlipState.FRONT_SIDE;
            }
        } else {
            if (mSetTopOut.isRunning() || mSetBottomIn.isRunning()) return;

            mCardBackLayout.setVisibility(VISIBLE);
            mCardFrontLayout.setVisibility(VISIBLE);

            if (mFlipState == FlipState.FRONT_SIDE) {
                // From front to back
                mSetTopOut.setTarget(mCardFrontLayout);
                mSetBottomIn.setTarget(mCardBackLayout);
                mSetTopOut.start();
                mSetBottomIn.start();
                mIsBackVisible = true;
                mFlipState = FlipState.BACK_SIDE;
            } else {
                // from back to front
                mSetTopOut.setTarget(mCardBackLayout);
                mSetBottomIn.setTarget(mCardFrontLayout);
                mSetTopOut.start();
                mSetBottomIn.start();
                mIsBackVisible = false;
                mFlipState = FlipState.FRONT_SIDE;
            }
        }
    }

    /**
     * Flip the view for one side with or without animation.
     *
     * @param withAnimation true means flip view with animation otherwise without animation.
     */
    public void flipTheView(boolean withAnimation) {
        if (getChildCount() < 2) return;

        if (flipType.equalsIgnoreCase("horizontal")) {
            if (!withAnimation) {
                mSetLeftIn.setDuration(0);
                mSetRightOut.setDuration(0);
                boolean oldFlipEnabled = flipEnabled;
                flipEnabled = true;

                flipTheView();

                mSetLeftIn.setDuration(flipDuration);
                mSetRightOut.setDuration(flipDuration);
                flipEnabled = oldFlipEnabled;
            } else {
                flipTheView();
            }
        } else {
            if (!withAnimation) {
                mSetBottomIn.setDuration(0);
                mSetTopOut.setDuration(0);
                boolean oldFlipEnabled = flipEnabled;
                flipEnabled = true;

                flipTheView();

                mSetBottomIn.setDuration(flipDuration);
                mSetTopOut.setDuration(flipDuration);
                flipEnabled = oldFlipEnabled;
            } else {
                flipTheView();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return gestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Error in dispatchTouchEvent: ", throwable);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled() && flipOnTouch) {
            return gestureDetector.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    /**
     * Whether view is set to flip on touch or not.
     *
     * @return true or false
     */
    public boolean isFlipOnTouch() {
        return flipOnTouch;
    }

    /**
     * Set whether view should be flipped on touch or not!
     *
     * @param flipOnTouch value (true or false)
     */
    public void setFlipOnTouch(boolean flipOnTouch) {
        this.flipOnTouch = flipOnTouch;
    }

    /**
     * Returns duration of flip in milliseconds!
     *
     * @return duration in milliseconds
     */
    public int getFlipDuration() {
        return flipDuration;
    }

    /**
     * Sets the flip duration (in milliseconds)
     *
     * @param flipDuration duration in milliseconds
     */
    public void setFlipDuration(int flipDuration) {
        this.flipDuration = flipDuration;
        if (flipType.equalsIgnoreCase("horizontal")) {
            //mSetRightOut.setDuration(flipDuration);
            mSetRightOut.getChildAnimations().get(0).setDuration(flipDuration);
            mSetRightOut.getChildAnimations().get(1).setStartDelay(flipDuration / 2);

            //mSetLeftIn.setDuration(flipDuration);
            mSetLeftIn.getChildAnimations().get(1).setDuration(flipDuration);
            mSetLeftIn.getChildAnimations().get(2).setStartDelay(flipDuration / 2);
        } else {
            mSetTopOut.getChildAnimations().get(0).setDuration(flipDuration);
            mSetTopOut.getChildAnimations().get(1).setStartDelay(flipDuration / 2);

            mSetBottomIn.getChildAnimations().get(1).setDuration(flipDuration);
            mSetBottomIn.getChildAnimations().get(2).setStartDelay(flipDuration / 2);
        }
    }

    /**
     * Returns whether view can be flipped only once!
     *
     * @return true or false
     */
    public boolean isFlipOnceEnabled() {
        return flipOnceEnabled;
    }

    /**
     * Enable / Disable flip only once feature.
     *
     * @param flipOnceEnabled true or false
     */
    public void setFlipOnceEnabled(boolean flipOnceEnabled) {
        this.flipOnceEnabled = flipOnceEnabled;
    }


    /**
     * Returns whether flip is enabled or not!
     *
     * @return true or false
     */
    public boolean isFlipEnabled() {
        return flipEnabled;
    }

    /**
     * Enable / Disable flip view.
     *
     * @param flipEnabled true or false
     */
    public void setFlipEnabled(boolean flipEnabled) {
        this.flipEnabled = flipEnabled;
    }

    /**
     * Returns which flip state is currently on of the flip view.
     *
     * @return current state of flip view
     */
    public FlipState getCurrentFlipState() {
        return mFlipState;
    }

    /**
     * Returns true if the front side of flip view is visible.
     *
     * @return true if the front side of flip view is visible.
     */
    public boolean isFrontSide() {
        return (mFlipState == FlipState.FRONT_SIDE);
    }

    /**
     * Returns true if the back side of flip view is visible.
     *
     * @return true if the back side of flip view is visible.
     */
    public boolean isBackSide() {
        return (mFlipState == FlipState.BACK_SIDE);
    }

    public OnFlipAnimationListener getOnFlipListener() {
        return onFlipListener;
    }

    public void setOnFlipListener(OnFlipAnimationListener onFlipListener) {
        this.onFlipListener = onFlipListener;
    }

    /*
    public @AnimatorRes int getAnimFlipOutId() {
        return animFlipOutId;
    }

    public void setAnimFlipOutId(@AnimatorRes int animFlipOutId) {
        this.animFlipOutId = animFlipOutId;
        loadAnimations();
    }

    public @AnimatorRes int getAnimFlipInId() {
        return animFlipInId;
    }

    public void setAnimFlipInId(@AnimatorRes int animFlipInId) {
        this.animFlipInId = animFlipInId;
        loadAnimations();
    }
    */

    /**
     * Returns true if the Flip Type of animation is Horizontal?
     */
    public boolean isHorizontalType() {
        return flipType.equals("horizontal");
    }

    /**
     * Returns true if the Flip Type of animation is Vertical?
     */
    public boolean isVerticalType() {
        return flipType.equals("vertical");
    }

    /**
     * Sets the Flip Type of animation to Horizontal
     */
    public void setToHorizontalType() {
        flipType = "horizontal";
        loadAnimations();
    }

    /**
     * Sets the Flip Type of animation to Vertical
     */
    public void setToVerticalType() {
        flipType = "vertical";
        loadAnimations();
    }

    /**
     * Sets the flip type from direction to right
     */
    public void setFlipTypeFromRight() {
        if (flipType.equals("horizontal"))
            flipTypeFrom = "right";
        else flipTypeFrom = "front";
        loadAnimations();
    }

    /**
     * Sets the flip type from direction to left
     */
    public void setFlipTypeFromLeft() {
        if (flipType.equals("horizontal"))
            flipTypeFrom = "left";
        else flipTypeFrom = "back";
        loadAnimations();
    }

    /**
     * Sets the flip type from direction to front
     */
    public void setFlipTypeFromFront() {
        if (flipType.equals("vertical"))
            flipTypeFrom = "front";
        else flipTypeFrom = "right";
        loadAnimations();
    }

    /**
     * Sets the flip type from direction to back
     */
    public void setFlipTypeFromBack() {
        if (flipType.equals("vertical"))
            flipTypeFrom = "back";
        else flipTypeFrom = "left";
        loadAnimations();
    }

    /**
     * Returns the flip type from direction. For horizontal, it will be either right or left and for vertical, it will be front or back.
     */
    public String getFlipTypeFrom() {
        return flipTypeFrom;
    }

    /**
     * Returns true if Auto Flip Back is enabled
     */
    public boolean isAutoFlipBack() {
        return autoFlipBack;
    }

    /**
     * Set if the card should be flipped back to original front side.
     * @param autoFlipBack true if card should be flipped back to froont side
     */
    public void setAutoFlipBack(boolean autoFlipBack) {
        this.autoFlipBack = autoFlipBack;
    }

    /**
     * Return the time in milliseconds to auto flip back to original front side.
     * @return
     */
    public int getAutoFlipBackTime() {
        return autoFlipBackTime;
    }

    /**
     * Set the time in milliseconds to auto flip back the view to the original front side
     * @param autoFlipBackTime The time in milliseconds
     */
    public void setAutoFlipBackTime(int autoFlipBackTime) {
        this.autoFlipBackTime = autoFlipBackTime;
    }

    /**
     * The Flip Animation Listener for animations and flipping complete listeners
     */
    public interface OnFlipAnimationListener {
        /**
         * Called when flip animation is completed.
         *
         * @param newCurrentSide After animation, the new side of the view. Either can be
         *                       FlipState.FRONT_SIDE or FlipState.BACK_SIDE
         */
        void onViewFlipCompleted(EasyFlipView easyFlipView, FlipState newCurrentSide);
    }

    private class SwipeDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isEnabled() && flipOnTouch) {
                flipTheView();
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (isEnabled() && flipOnTouch) {
                return true;
            }
            return super.onDown(e);
        }
    }
}
