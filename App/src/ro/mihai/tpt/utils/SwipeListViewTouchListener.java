/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011  Mihai Balint

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 
*/
package ro.mihai.tpt.utils;

import android.graphics.Rect;
import android.view.*;
import android.widget.AbsListView;
import android.widget.ListView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;


public class SwipeListViewTouchListener implements View.OnTouchListener {
    // Cached ViewConfiguration and system-wide constant values
    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;

    // Fixed properties
    private ViewGroup mListView;
    private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero

    // Transient properties
    private float mDownX;
    private boolean mSwiping;
    private VelocityTracker mVelocityTracker;
    private View mDownView;
    private boolean mPaused;

    /**
     * The callback interface used by {@link SwipeListViewTouchListener} to inform its client
     * about a successful swipe of one or more list item positions.
     */
    public interface OnSwipeCallback {
        /**
         * Called when the user has swiped the list item to the left.
         *
         * @param listView               The originating {@link ListView}.
         * @param reverseSortedPositions An array of positions to dismiss, sorted in descending
         *                               order for convenience.
         */
        void onSwipeLeft(ViewGroup listView, int[] reverseSortedPositions);

        void onSwipeRight(ViewGroup listView, int[] reverseSortedPositions);
    }

    /**
     * Constructs a new swipe-to-action touch listener for the given list view.
     *
     * @param listView The list view whose items should be dismissable.
     * @param callback The callback to trigger when the user has indicated that she would like to
     *                 dismiss one or more list items.
     */
    public SwipeListViewTouchListener(ViewGroup listView, OnSwipeCallback callback) {
        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = listView.getContext().getResources().getInteger(
            android.R.integer.config_shortAnimTime);
        mListView = listView;
    }

    /**
     * Constructs a new swipe-to-action touch listener for the given list view.
     * 
     * @param listView The list view whose items should be dismissable.
     * @param callback The callback to trigger when the user has indicated that she would like to
     *                 dismiss one or more list items.
     * @param dismissLeft set if the dismiss animation is up when the user swipe to the left
     * @param dismissRight set if the dismiss animation is up when the user swipe to the right
     * @see #SwipeListViewTouchListener(ListView, OnSwipeCallback, boolean, boolean)
     */
    public SwipeListViewTouchListener(ViewGroup listView, OnSwipeCallback callback, boolean dismissLeft, boolean dismissRight) {
        this(listView, callback);
    }

    /**
     * Enables or disables (pauses or resumes) watching for swipe-to-dismiss gestures.
     *
     * @param enabled Whether or not to watch for gestures.
     */
    public void setEnabled(boolean enabled) {
        mPaused = !enabled;
    }

    /**
     * Returns an {@link android.widget.AbsListView.OnScrollListener} to be added to the
     * {@link ListView} using
     * {@link ListView#setOnScrollListener(android.widget.AbsListView.OnScrollListener)}.
     * If a scroll listener is already assigned, the caller should still pass scroll changes
     * through to this listener. This will ensure that this
     * {@link SwipeListViewTouchListener} is paused during list view scrolling.</p>
     *
     * @see {@link SwipeListViewTouchListener}
     */
    public AbsListView.OnScrollListener makeScrollListener() {
        return new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }

            public void onScroll(AbsListView absListView, int i, int i1, int i2) {}
        };
    }
    
    public final int getActionMasked(MotionEvent motionEvent) {
    	return motionEvent.getAction() & MotionEvent.ACTION_MASK;
    }
    public final int getActionIndex(MotionEvent motionEvent) {
    	return (motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mViewWidth < 2) {
            mViewWidth = mListView.getWidth();
        }

        switch (getActionMasked(motionEvent)) {
        case MotionEvent.ACTION_DOWN:
            {
                if (mPaused) {
                    return false;
                }

                // TODO: ensure this is a finger, and set a flag

                // Find the child view that was touched (perform a hit test)
                Rect rect = new Rect();
                int childCount = mListView.getChildCount();
                int[] listViewCoords = new int[2];
                mListView.getLocationOnScreen(listViewCoords);
                int x = (int) motionEvent.getRawX() - listViewCoords[0];
                int y = (int) motionEvent.getRawY() - listViewCoords[1];
                View child;
                for (int i = 0; i < childCount; i++) {
                    child = mListView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        mDownView = child;
                        break;
                    }
                }

                if (mDownView != null) {
                    mDownX = motionEvent.getRawX();
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(motionEvent);
                }
                view.onTouchEvent(motionEvent);
                return true;
            }

        case MotionEvent.ACTION_UP:
            {
                if (mVelocityTracker == null) {
                    break;
                }

                float deltaX = motionEvent.getRawX() - mDownX;
                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(500); // 1000 by defaut but it was too much
                float velocityX = Math.abs(mVelocityTracker.getXVelocity());
                float velocityY = Math.abs(mVelocityTracker.getYVelocity());
                boolean swipe = false;
                boolean swipeRight = false;

                if (Math.abs(deltaX) > mViewWidth / 2) {
                    swipe = true;
                    swipeRight = deltaX > 0;
                } else if (mMinFlingVelocity <= velocityX && velocityX <= mMaxFlingVelocity && velocityY < velocityX) {
                    swipe = true;
                    swipeRight = mVelocityTracker.getXVelocity() > 0;
                }
                if (swipe) {
                    // sufficent swipe value
                	ViewPropertyAnimator.animate(mDownView)
                        .translationX(swipeRight ? mViewWidth : -mViewWidth)
                        .alpha(0)
                        .setDuration(mAnimationTime)
                        .setListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
                        	@Override
                        	public void onAnimationEnd(Animator animation) {
                        		// performSwipeAction(downView, downPosition, toTheRight, toTheRight ? dismissRight : dismissLeft);
                        	}
                        })
                        .start();
                } else {
                    // cancel
                	ViewPropertyAnimator.animate(mDownView)
                        .translationX(0)
                        .alpha(1)
                        .setDuration(mAnimationTime)
                        .setListener(null)
                        .start();
                }
                mVelocityTracker = null;
                mDownX = 0;
                mDownView = null;
                mSwiping = false;
                break;
            }

        case MotionEvent.ACTION_MOVE:
            {
                if (mVelocityTracker == null || mPaused) {
                    break;
                }

                mVelocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - mDownX;
                if (Math.abs(deltaX) > mSlop) {
                    mSwiping = true;
                    mListView.requestDisallowInterceptTouchEvent(true);

                    // Cancel ListView's touch (un-highlighting the item)
                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                        (getActionIndex(motionEvent) << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mListView.onTouchEvent(cancelEvent);
                }

                if (mSwiping) {
                	float alpha = Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(deltaX) / mViewWidth));
                	ViewPropertyAnimator.animate(mDownView)
                    	.translationX(deltaX)
                    	.alpha(alpha)
                    	.setDuration(0)
                    	.start();
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
	public static void zuzu(ListView listView) {
		// Create a ListView-specific touch listener. ListViews are given special treatment because
		// by default they handle touches for their list items... i.e. they're in charge of drawing
		// the pressed state (the list selector), handling list item clicks, etc.
		SwipeListViewTouchListener touchListener =
		    new SwipeListViewTouchListener(
		        listView,
		        new SwipeListViewTouchListener.OnSwipeCallback() {

		            public void onSwipeLeft(ViewGroup listView, int [] reverseSortedPositions) {
		                //  Log.i(this.getClass().getName(), "swipe left : pos="+reverseSortedPositions[0]);
		                // TODO : YOUR CODE HERE FOR LEFT ACTION
		            }

		            public void onSwipeRight(ViewGroup listView, int [] reverseSortedPositions) {
		                //  Log.i(ProfileMenuActivity.class.getClass().getName(), "swipe right : pos="+reverseSortedPositions[0]);
		                // TODO : YOUR CODE HERE FOR RIGHT ACTION
		            }
		        },
		        true, // example : left action = dismiss
		        false); // example : right action without dismiss animation
		listView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during ListView scrolling,
		// we don't look for swipes.
		listView.setOnScrollListener(touchListener.makeScrollListener());		
	}    
}