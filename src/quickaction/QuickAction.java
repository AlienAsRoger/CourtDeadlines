package quickaction;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.alien_roger.court_deadlines.R;

/**
 * Quickaction window.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 */
public class QuickAction extends PopupWindows {
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private Animation mTrackAnim;
	private LayoutInflater inflater;
	private ViewGroup mTrack;
	private OnActionItemClickListener mListener;
	private View mAnchorView;

	private int animStyle;
	private int mChildPos;
	private boolean animateTrack;

	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_AUTO = 4;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context
	 */
	public QuickAction(Context context) {
		super(context);

		setInflater((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));

		setTrackAnim(AnimationUtils.loadAnimation(context, R.anim.rail));

		getTrackAnim().setInterpolator(new Interpolator() {
			@Override
			public float getInterpolation(float t) {
				// Pushes past the target area, then snaps back into place.
				// Equation for graphing: 1.2-((x*1.6)-1.1)^2
				final float inner = t * 1.55f - 1.1f;

				return 1.2f - inner * inner;
			}
		});

		setRootViewId(getRootViewID());

		animStyle = ANIM_AUTO;
		setAnimateTrack(true);
		setChildPos(0);
	}

	protected int getRootViewID() {
		return R.layout.quickaction;
	}

	/**
	 * Set root view.
	 * 
	 * @param id
	 *            Layout resource id
	 */
	public void setRootViewId(int id) {
		mRootView = getInflater().inflate(id, (ViewGroup) mWindow.getContentView(), true);
		setTrack((ViewGroup) mRootView.findViewById(R.id.tracks));

		mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);
		mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);

		// setContentView(mRootView);
	}

	/**
	 * Animate track.
	 * 
	 * @param animateTrack
	 *            flag to animate track
	 */
	public void animateTrack(boolean animateTrack) {
		this.setAnimateTrack(animateTrack);
	}

	/**
	 * Set animation style.
	 * 
	 * @param animStyle
	 *            animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int animStyle) {
		this.animStyle = animStyle;
	}

	/**
	 * Add action Item
	 * 
	 * @param action
	 *            {@link ActionItem}
	 */
	public void addActionItem(ActionItem action) {

		action.getTitle();
		action.getIcon();

		View container = getInflater().inflate(R.layout.action_item, null, false);

		ImageView img = (ImageView) container.findViewById(R.id.iv_icon);
		TextView text = (TextView) container.findViewById(R.id.tv_title);
		container.setTag(R.id.iv_icon, img);
		container.setTag(R.id.tv_title, text);
		container.setTag(R.id.tag_action, action);

		final int pos = getChildPos();

		container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getListener() != null) {
					getListener().onItemClick(getAnchorView(), pos);
				}

				dismiss();
			}
		});

		container.setFocusable(true);
		container.setClickable(true);

		getTrack().addView(container, getChildPos());

		setChildPos(getChildPos() + 1);
	}

	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		setListener(listener);
	}

	/**
	 * Show popup mWindow
	 */
	public void show(View anchor) {
		this.setAnchorView(anchor);
		preShow();

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1]
				+ anchor.getHeight());

		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = mRootView.getMeasuredWidth();
		int rootHeight = mRootView.getMeasuredHeight();

		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		// int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

		int xPos = (screenWidth - rootWidth) / 2;
		int yPos = anchorRect.top - rootHeight;

		boolean onTop = true;

		// display on bottom
		if (rootHeight > anchor.getTop()) {
			yPos = anchorRect.bottom;
			onTop = false;
		}

		showArrow((onTop ? R.id.arrow_down : R.id.arrow_up), anchorRect.centerX());

		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);

		for (int i = 0; i < getChildPos(); i++) {
			View v = getTrack().getChildAt(i);
			ImageView img = (ImageView) v.getTag(R.id.iv_icon);
			TextView text = (TextView) v.getTag(R.id.tv_title);
			ActionItem ai = (ActionItem) v.getTag(R.id.tag_action);

			Drawable icon = ai.getIcon();
			String title = ai.getTitle();
			if (icon != null) {
				img.setImageDrawable(icon);
			} else {
				img.setVisibility(View.GONE);
			}

			if (title != null) {
				text.setText(title);
			} else {
				text.setVisibility(View.GONE);
			}
		}

		if (isAnimateTrack()) {
			getTrack().startAnimation(getTrackAnim());
		}
	}

	/**
	 * Set animation style
	 * 
	 * @param screenWidth
	 *            Screen width
	 * @param requestedX
	 *            distance from left screen
	 * @param onTop
	 *            flag to indicate where the popup should be displayed. Set TRUE
	 *            if displayed on top of anchor and vice versa
	 */
	protected void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;

		switch (animStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle(onTop ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			break;

		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle(onTop ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			break;

		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle(onTop ? R.style.Animations_PopUpMenu_Center
					: R.style.Animations_PopDownMenu_Center);
			break;

		case ANIM_AUTO:
			if (arrowPos <= screenWidth / 4) {
				mWindow.setAnimationStyle(onTop ? R.style.Animations_PopUpMenu_Left
						: R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
				mWindow.setAnimationStyle(onTop ? R.style.Animations_PopUpMenu_Center
						: R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle(onTop ? R.style.Animations_PopDownMenu_Right
						: R.style.Animations_PopDownMenu_Right);
			}

			break;
		}
	}

	/**
	 * Show arrow
	 * 
	 * @param whichArrow
	 *            arrow type resource id
	 * @param requestedX
	 *            distance from left screen
	 */
	protected void showArrow(int whichArrow, int requestedX) {
		final View showArrow = whichArrow == R.id.arrow_up ? mArrowUp : mArrowDown;
		final View hideArrow = whichArrow == R.id.arrow_up ? mArrowDown : mArrowUp;

		final int arrowWidth = mArrowUp.getMeasuredWidth();

		showArrow.setVisibility(View.VISIBLE);

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow.getLayoutParams();

		param.leftMargin = requestedX - arrowWidth / 2;

		hideArrow.setVisibility(View.INVISIBLE);
	}

	/**
	 * @param mAnchorView
	 *            the mAnchorView to set
	 */
	public void setAnchorView(View mAnchorView) {
		this.mAnchorView = mAnchorView;
	}

	/**
	 * @return the mAnchorView
	 */
	public View getAnchorView() {
		return mAnchorView;
	}

	/**
	 * @param animateTrack
	 *            the animateTrack to set
	 */
	public void setAnimateTrack(boolean animateTrack) {
		this.animateTrack = animateTrack;
	}

	/**
	 * @return the animateTrack
	 */
	public boolean isAnimateTrack() {
		return animateTrack;
	}

	/**
	 * @param mTrack
	 *            the mTrack to set
	 */
	public void setTrack(ViewGroup mTrack) {
		this.mTrack = mTrack;
	}

	/**
	 * @return the mTrack
	 */
	public ViewGroup getTrack() {
		return mTrack;
	}

	/**
	 * @param mTrackAnim
	 *            the mTrackAnim to set
	 */
	public void setTrackAnim(Animation mTrackAnim) {
		this.mTrackAnim = mTrackAnim;
	}

	/**
	 * @return the mTrackAnim
	 */
	public Animation getTrackAnim() {
		return mTrackAnim;
	}

	/**
	 * @param mChildPos
	 *            the mChildPos to set
	 */
	public void setChildPos(int mChildPos) {
		this.mChildPos = mChildPos;
	}

	/**
	 * @return the mChildPos
	 */
	public int getChildPos() {
		return mChildPos;
	}

	public LayoutInflater getInflater() {
		return inflater;
	}

	public void setInflater(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	public OnActionItemClickListener getListener() {
		return mListener;
	}

	public void setListener(OnActionItemClickListener mListener) {
		this.mListener = mListener;
	}

	/**
	 * Listener for Item click
	 * 
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick(View anchoredView, int pos);
	}
}