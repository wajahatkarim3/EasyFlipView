package com.wajahatkarim3.easyflipview;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * A quick and easy flip view through which you can create views with two sides like credit cards,
 * poker cards, flash cards etc.
 *
 * Add com.wajahatkarim3.easyflipview.EasyFlipView into your XML layouts with two direct children
 * views and you are done!
 * For more information, check http://github.com/wajahatkarim3/EasyFlipView
 *
 * @author Wajahat Karim (http://wajahatkarim.com)
 * @version 1.0.1 01/11/2017
 */
public class EasyFlipView extends FrameLayout {

  public static final String TAG = EasyFlipView.class.getSimpleName();

  public static final int DEFAULT_FLIP_DURATION = 400;
  private int animFlipHorizontalOutId = R.animator.animation_horizontal_flip_out;
  private int animFlipHorizontalInId = R.animator.animation_horizontal_flip_in;
  private int animFlipVerticalOutId = R.animator.animation_vertical_flip_out;
  private int animFlipVerticalInId = R.animator.animation_vertical_flip_in;

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

  private boolean flipOnTouch;
  private int flipDuration;
  private boolean flipEnabled;

  private Context context;
  private float x1;
  private float y1;

  private FlipState mFlipState = FlipState.FRONT_SIDE;

  private OnFlipAnimationListener onFlipListener = null;

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
    // Setting Defaul Values
    flipOnTouch = true;
    flipDuration = DEFAULT_FLIP_DURATION;
    flipEnabled = true;

    // Check for the attributes
    if (attrs != null) {
      // Attribute initialization
      final TypedArray attrArray =
        context.obtainStyledAttributes(attrs, R.styleable.easy_flip_view, 0, 0);
      try {
        flipOnTouch = attrArray.getBoolean(R.styleable.easy_flip_view_flipOnTouch, true);
        flipDuration =
          attrArray.getInt(R.styleable.easy_flip_view_flipDuration, DEFAULT_FLIP_DURATION);
        flipEnabled = attrArray.getBoolean(R.styleable.easy_flip_view_flipEnabled, true);
        flipType = attrArray.getString(R.styleable.easy_flip_view_flipType);
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

  private void loadAnimations() {
    if (flipType.equalsIgnoreCase("horizontal")) {
      mSetRightOut =
        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipHorizontalOutId);
      mSetLeftIn =
        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipHorizontalInId);
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

            if (onFlipListener != null) onFlipListener.onViewFlipCompleted(FlipState.FRONT_SIDE);
          } else {
            mCardBackLayout.setVisibility(VISIBLE);
            mCardFrontLayout.setVisibility(GONE);

            if (onFlipListener != null) onFlipListener.onViewFlipCompleted(FlipState.BACK_SIDE);
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
      mSetTopOut = (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipVerticalOutId);
      mSetBottomIn =
        (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animFlipVerticalInId);

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

            if (onFlipListener != null) onFlipListener.onViewFlipCompleted(FlipState.FRONT_SIDE);
          } else {
            mCardBackLayout.setVisibility(VISIBLE);
            mCardFrontLayout.setVisibility(GONE);

            if (onFlipListener != null) onFlipListener.onViewFlipCompleted(FlipState.BACK_SIDE);
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
  public boolean onTouchEvent(MotionEvent event) {

    if (isEnabled() && flipOnTouch) {
      this.getParent().requestDisallowInterceptTouchEvent(true);
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          x1 = event.getX();
          y1 = event.getY();
          return true;
        case MotionEvent.ACTION_UP:
          float x2 = event.getX();
          float y2 = event.getY();
          float dx = x2 - x1;
          float dy = y2 - y1;
          float MAX_CLICK_DISTANCE = 0.5f;
          if ((dx >= 0 && dx < MAX_CLICK_DISTANCE) && (dy >= 0 && dy < MAX_CLICK_DISTANCE)) {
            flipTheView();
          }
          return true;
      }
    } else {
      return super.onTouchEvent(event);
    }
    return super.onTouchEvent(event);
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
   * The Flip Animation Listener for animations and flipping complete listeners
   */
  public interface OnFlipAnimationListener {
    /**
     * Called when flip animation is completed.
     *
     * @param newCurrentSide After animation, the new side of the view. Either can be
     * FlipState.FRONT_SIDE or FlipState.BACK_SIDE
     */
    void onViewFlipCompleted(FlipState newCurrentSide);
  }
}
