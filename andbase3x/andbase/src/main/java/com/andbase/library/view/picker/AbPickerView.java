
package com.andbase.library.view.picker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.andbase.library.util.AbGraphicUtil;
import com.andbase.library.util.AbViewUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 轮子选择View
 */
public class AbPickerView extends View {
	
	/** The m context. */
	private Context context = null;

	/** Scrolling duration. */
	private static final int SCROLLING_DURATION = 400;

	/** Minimum delta for scrolling. */
	private static final int MIN_DELTA_FOR_SCROLLING = 1;

	/** Items text color. */
	private static final int ITEMS_TEXT_COLOR = 0xFF000000;

	/** Label offset. */
	private static final int LABEL_OFFSET = 8;

	/** Left and right padding value. */
	private static final int PADDING = 5;
	
	/** Default count of visible items. */
	private static final int DEF_VISIBLE_ITEMS = 5;

	/** The adapter. */
	private AbPickerAdapter adapter = null;

	/** The current item. */
	private int currentItem = 0;

	/** The items width. */
	private int itemsWidth = 0;

	/** The label width. */
	private int labelWidth = 0;

	/** The visible items. */
	private int visibleItems = DEF_VISIBLE_ITEMS;

	/** The item height. */
	private int itemHeight = 0;

	/** The items paint. */
	private TextPaint itemsPaint;

	/** The value paint. */
	private TextPaint valuePaint;
	
	/** The label paint. */
	private TextPaint labelPaint;

	/** The items layout. */
	private StaticLayout itemsLayout;

	/** The label layout. */
	private StaticLayout labelLayout;

	/** The value layout. */
	private StaticLayout valueLayout;

	/** The label. */
	private String label;

	/** The is scrolling performed. */
	private boolean isScrollingPerformed;

	/** The scrolling offset. */
	private int scrollingOffset;

	/** The gesture detector. */
	private GestureDetector gestureDetector;

	/** The scroller. */
	private Scroller scroller;

	/** The last scroll y. */
	private int lastScrollY;

	/** The is cyclic. */
	boolean isCyclic = false;

	/** The changing listeners. */
	private List<AbOnPickerChangedListener> changingListeners = new LinkedList<AbOnPickerChangedListener>();

	/** The scrolling listeners. */
	private List<AbOnPickerScrollListener> scrollingListeners = new LinkedList<AbOnPickerScrollListener>();
	
	/** 中间覆盖条的背景图. */
	private Drawable centerSelectDrawable;
	
	/** 中间覆盖条的颜色，如果没有设置centerDrawable时才生效. */
	private int[] centerSelectGradientColors = new int[] {0x706ED9FF,0x7065C7EA, 0x706ED9FF};
	
	/** The center select stroke width. */
	private int centerSelectStrokeWidth  = 1;
	
	/** The center select stroke color. */
	private int centerSelectStrokeColor  = 0x706ED9FF;
	
    /** Current value. */
	private int valueTextColor = 0xF0000000;
	
	/** Current label text color. */
	private int labelTextColor = 0x616161;
	
	/** 值的文字大小. */
	private int valueTextSize = 15;
	
	/** 标签的文字大小. */
	private int labelTextSize = 15;

	/** 单行文字的高度. */
	private int itemOffset = valueTextSize / 5;
	
	/** 中间覆盖条高度. */
	private int additionalItemHeight = 30;
	

	public AbPickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData(context);
	}


	public AbPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}


	public AbPickerView(Context context) {
		super(context);
		initData(context);
	}


	private void initData(Context context) {
		this.context = context;
		gestureDetector = new GestureDetector(context, gestureListener);
		gestureDetector.setIsLongpressEnabled(false);
		scroller = new Scroller(context);

		valueTextSize = AbViewUtil.scaleValue(this.context,15);
		labelTextSize = AbViewUtil.scaleValue(this.context,15);
	}


	public AbPickerAdapter getAdapter() {
		return adapter;
	}


	public void setAdapter(AbPickerAdapter adapter) {
		this.adapter = adapter;
		invalidateLayouts();
		// 重绘
		invalidate();
	}


	public void setInterpolator(Interpolator interpolator) {
		scroller.forceFinished(true);
		scroller = new Scroller(getContext(), interpolator);
	}


	public int getVisibleItems() {
		return visibleItems;
	}


	public void setVisibleItems(int count) {
		visibleItems = count;
		invalidate();
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String newLabel) {
		if (label == null || !label.equals(newLabel)) {
			label = newLabel;
			labelLayout = null;
			invalidate();
		}
	}


	public void addChangingListener(AbOnPickerChangedListener listener) {
		changingListeners.add(listener);
	}


	public void removeChangingListener(AbOnPickerChangedListener listener) {
		changingListeners.remove(listener);
	}


	protected void notifyChangingListeners(int oldValue, int newValue) {
		for (AbOnPickerChangedListener listener : changingListeners) {
			listener.onChanged(this, oldValue, newValue);
		}
	}


	public void addScrollingListener(AbOnPickerScrollListener listener) {
		scrollingListeners.add(listener);
	}


	public void removeScrollingListener(AbOnPickerScrollListener listener) {
		scrollingListeners.remove(listener);
	}


	protected void notifyScrollingListenersAboutStart() {
		for (AbOnPickerScrollListener listener : scrollingListeners) {
			listener.onScrollingStarted(this);
		}
	}


	protected void notifyScrollingListenersAboutEnd() {
		for (AbOnPickerScrollListener listener : scrollingListeners) {
			listener.onScrollingFinished(this);
		}
	}


	public int getCurrentItem() {
		return currentItem;
	}


	public void setCurrentItem(int index, boolean animated) {
		if (adapter == null || adapter.getItemsCount() == 0) {
			return; // throw?
		}
		if (index < 0 || index >= adapter.getItemsCount()) {
			if (isCyclic) {
				while (index < 0) {
					index += adapter.getItemsCount();
				}
				index %= adapter.getItemsCount();
			} else {
				return; // throw?
			}
		}
		if (index != currentItem) {
			if (animated) {
				scroll(index - currentItem, SCROLLING_DURATION);
			} else {
				invalidateLayouts();

				int old = currentItem;
				currentItem = index;

				notifyChangingListeners(old, currentItem);

				invalidate();
			}
		}
	}

	/**
	 * Sets the current item w/o animation. Does nothing when index is wrong.
	 * @param index the item index
	 */
	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}

	/**
	 * Tests if wheel is cyclic. That means before the 1st item there is shown
	 * the last one
	 * @return true if wheel is cyclic
	 */
	public boolean isCyclic() {
		return isCyclic;
	}

	/**
	 * Set wheel cyclic flag.
	 *
	 * @param isCyclic the flag to set
	 */
	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;

		invalidate();
		invalidateLayouts();
	}

	/**
	 * Invalidates layouts.
	 */
	private void invalidateLayouts() {
		itemsLayout = null;
		valueLayout = null;
		scrollingOffset = 0;
	}

	/**
	 * Initializes resources.
	 */
	private void initResourcesIfNecessary() {
		if (itemsPaint == null) {
			itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
			itemsPaint.setTextSize(valueTextSize);
		}

		if (valuePaint == null) {
			valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
			valuePaint.setTextSize(valueTextSize);
		}
		
		if (labelPaint == null) {
			labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
			labelPaint.setTextSize(labelTextSize);
		}

		//如果没设置中间的选中条用默认的颜色
		if (centerSelectDrawable == null) {
			GradientDrawable mGradientDrawable = new GradientDrawable(Orientation.BOTTOM_TOP, centerSelectGradientColors);
			mGradientDrawable.setStroke(centerSelectStrokeWidth, centerSelectStrokeColor);
			centerSelectDrawable = mGradientDrawable;
		}

		/* Android中提供了Shader类专门用来渲染图像以及一些几何图形，
		 Shader下面包括几个直接子类，分别是BitmapShader、 ComposeShader、LinearGradient、
		 RadialGradient、SweepGradient。 BitmapShader主要用来渲染图像，
		 LinearGradient 用来进行梯度渲染，RadialGradient 用来进行环形渲染，
		 SweepGradient 
		 用来进行梯度渲染，ComposeShader则是一个 混合渲染，可以和其它几个子类组合起来使用。 */
		
	}

	/**
	 * Calculates desired height for layout.
	 *
	 * @param layout the source layout
	 * @return the desired layout height
	 */
	private int getDesiredHeight(Layout layout) {
		if (layout == null) {
			return 0;
		}

		int desired = getItemHeight() * visibleItems - itemOffset * 2 - additionalItemHeight;

		// Check against our minimum height
		desired = Math.max(desired, getSuggestedMinimumHeight());

		return desired;
	}

	/**
	 * Returns text item by index.
	 *
	 * @param index the item index
	 * @return the item or null
	 */
	private String getTextItem(int index) {
		if (adapter == null || adapter.getItemsCount() == 0) {
			return null;
		}
		int count = adapter.getItemsCount();
		if ((index < 0 || index >= count) && !isCyclic) {
			return null;
		} else {
			while (index < 0) {
				index = count + index;
			}
		}

		index %= count;
		return adapter.getItem(index);
	}

	/**
	 * Builds text depending on current value.
	 *
	 * @param useCurrentValue the use current value
	 * @return the text
	 */
	private String buildText(boolean useCurrentValue) {
		StringBuilder itemsText = new StringBuilder();
		int addItems = visibleItems / 2 + 1;

		for (int i = currentItem - addItems; i <= currentItem + addItems; i++) {
			if (useCurrentValue || i != currentItem) {
				String text = getTextItem(i);
				if (text != null) {
					itemsText.append(text);
				}
			}
			if (i < currentItem + addItems) {
				itemsText.append("\n");
			}
		}

		return itemsText.toString();
	}

	/**
	 * Returns the max item length that can be present.
	 *
	 * @return the max length
	 */
	private int getMaxTextLength() {
		AbPickerAdapter adapter = getAdapter();
		if (adapter == null) {
			return 0;
		}

		int adapterLength = adapter.getMaximumLength();
		if (adapterLength > 0) {
			return adapterLength;
		}else{
			return 0;
		}
	}

	/**
	 * Returns height of wheel item.
	 *
	 * @return the item height
	 */
	private int getItemHeight() {
		if (itemHeight != 0) {
			return itemHeight;
		} else if (itemsLayout != null && itemsLayout.getLineCount() > 2) {
			itemHeight = itemsLayout.getLineTop(2) - itemsLayout.getLineTop(1);
			return itemHeight;
		}

		return getHeight() / visibleItems;
	}

	/**
	 * Calculates control width and creates text layouts.
	 *
	 * @param widthSize the input layout width
	 * @param mode the layout mode
	 * @return the calculated control width
	 */
	private int calculateLayoutWidth(int widthSize, int mode) {
		initResourcesIfNecessary();

		int width = widthSize;

		int maxLength = getMaxTextLength();
		if (maxLength > 0) {
			//一个字符宽度
			float textWidth = (int)AbGraphicUtil.getStringWidth("0", labelPaint);
			//不算lable的宽度
			itemsWidth = (int) (maxLength * textWidth);
		} else {
			itemsWidth = 0;
		}
		
		//空隙宽度
		itemsWidth += LABEL_OFFSET; 

		//label宽度的计算
		labelWidth = 0;
		if (label != null && label.length() > 0) {
			labelWidth = (int) AbGraphicUtil.getStringWidth(label, labelPaint);
		}

		boolean recalculate = false;
		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
			recalculate = true;
		} else {
			width = itemsWidth + labelWidth + 2 * PADDING;
			//有Label，添加一个间距
			if (labelWidth > 0) {
				width += LABEL_OFFSET;
			}

			// Check against our minimum width
			width = Math.max(width, getSuggestedMinimumWidth());

			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
				width = widthSize;
				recalculate = true;
			}
		}

		if (recalculate) {
			// recalculate width
			int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
			if (pureWidth <= 0) {
				itemsWidth = labelWidth = 0;
			}
			if (labelWidth > 0) {
				//对半分后再调整下
				int newItemsWidth = (pureWidth+itemsWidth)/2-LABEL_OFFSET;
				int newLabelWidth = pureWidth - newItemsWidth;
				if(newItemsWidth < itemsWidth){
					//放不下了,看label有空余宽度没有
					itemsWidth = pureWidth-labelWidth;
				}else{
					labelWidth = newLabelWidth;
					itemsWidth = newItemsWidth;
				}
			} else {
				// no label
				itemsWidth = pureWidth + LABEL_OFFSET; 
			}
		}
		
		if (itemsWidth > 0) {
			createLayouts(itemsWidth, labelWidth);
		}

		return width;
	}

	/**
	 * Creates layouts.
	 *
	 * @param widthItems width of items layout
	 * @param widthLabel width of label layout
	 */
	private void createLayouts(int widthItems, int widthLabel) {
		//190 94
		//if(D)Log.d(TAG, "widthItems:"+widthItems);
		//if(D)Log.d(TAG, "widthLabel:"+widthLabel);
		
		if (itemsLayout == null || itemsLayout.getWidth() > widthItems) {
			itemsLayout = new StaticLayout(buildText(isScrollingPerformed), itemsPaint, widthItems,
					widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_CENTER, 1,
					additionalItemHeight, false);
		} else {
			itemsLayout.increaseWidthTo(widthItems);
		}

		if (!isScrollingPerformed && (valueLayout == null || valueLayout.getWidth() > widthItems)) {
			String text = getAdapter() != null ? getAdapter().getItem(currentItem) : null;
			valueLayout = new StaticLayout(text != null ? text : "", valuePaint, widthItems,
					widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_CENTER, 1,
					additionalItemHeight, false);
		} else if (isScrollingPerformed) {
			valueLayout = null;
		} else {
			valueLayout.increaseWidthTo(widthItems);
		}

		if (widthLabel > 0) {
			if (labelLayout == null || labelLayout.getWidth() > widthLabel) {
				labelLayout = new StaticLayout(label, labelPaint, widthLabel, Layout.Alignment.ALIGN_NORMAL, 1,
						additionalItemHeight, false);
			} else {
				labelLayout.increaseWidthTo(widthLabel);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width = calculateLayoutWidth(widthSize, widthMode);

		int height;
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getDesiredHeight(itemsLayout);

			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (itemsLayout == null) {
			if (itemsWidth == 0) {
				calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
			} else {
				createLayouts(itemsWidth, labelWidth);
			}
		}

		if (itemsWidth > 0) {
			canvas.save();
			canvas.translate(PADDING, -itemOffset);
			drawItems(canvas);
			drawValue(canvas);
			canvas.restore();
		}

		drawCenterRect(canvas);
	}

	private void drawValue(Canvas canvas) {
		valuePaint.setColor(valueTextColor);
		valuePaint.drawableState = getDrawableState();
		
		labelPaint.setColor(labelTextColor);
		labelPaint.drawableState = getDrawableState();

		Rect bounds = new Rect();
		itemsLayout.getLineBounds(visibleItems / 2, bounds);

		// draw label
		if (labelLayout != null) {
			canvas.save();
			canvas.translate(itemsLayout.getWidth() + LABEL_OFFSET, bounds.top);
			labelLayout.draw(canvas);
			canvas.restore();
		}

		// draw current value
		if (valueLayout != null) {
			canvas.save();
			canvas.translate(0, bounds.top + scrollingOffset);
			valueLayout.draw(canvas);
			canvas.restore();
		}
	}

	/**
	 * Draws items.
	 *
	 * @param canvas the canvas for drawing
	 */
	private void drawItems(Canvas canvas) {
		canvas.save();

		int top = itemsLayout.getLineTop(1);
		canvas.translate(0, -top + scrollingOffset);

		itemsPaint.setColor(ITEMS_TEXT_COLOR);
		itemsPaint.drawableState = getDrawableState();
		itemsLayout.draw(canvas);

		canvas.restore();
	}

	/**
	 * Draws rect for current value.
	 *
	 * @param canvas the canvas for drawing
	 */
	private void drawCenterRect(Canvas canvas) {
		int center = getHeight() / 2;
		int offset = getItemHeight() / 2;
		centerSelectDrawable.setBounds(0, center - offset, getWidth(), center + offset);
		centerSelectDrawable.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		AbPickerAdapter adapter = getAdapter();
		if (adapter == null) {
			return true;
		}

		if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
			justify();
		}
		return true;
	}

	/**
	 * Scrolls the wheel.
	 *
	 * @param delta the scrolling value
	 */
	private void doScroll(int delta) {
		scrollingOffset += delta;

		int count = scrollingOffset / getItemHeight();
		int pos = currentItem - count;
		if (isCyclic && adapter.getItemsCount() > 0) {
			// fix position by rotating
			while (pos < 0) {
				pos += adapter.getItemsCount();
			}
			pos %= adapter.getItemsCount();
		} else if (isScrollingPerformed) {
			//
			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= adapter.getItemsCount()) {
				count = currentItem - adapter.getItemsCount() + 1;
				pos = adapter.getItemsCount() - 1;
			}
		} else {
			// fix position
			pos = Math.max(pos, 0);
			pos = Math.min(pos, adapter.getItemsCount() - 1);
		}

		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}

		// update offset
		scrollingOffset = offset - count * getItemHeight();
		if (scrollingOffset > getHeight()) {
			scrollingOffset = scrollingOffset % getHeight() + getHeight();
		}
	}

	// gesture listener
	/** The gesture listener. */
	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {

		public boolean onDown(MotionEvent e) {
			if (isScrollingPerformed) {
				scroller.forceFinished(true);
				clearMessages();
				return true;
			}
			return false;
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			startScrolling();
			doScroll((int) -distanceY);
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			lastScrollY = currentItem * getItemHeight() + scrollingOffset;
			int maxY = isCyclic ? 0x7FFFFFFF : adapter.getItemsCount() * getItemHeight();
			int minY = isCyclic ? -maxY : 0;
			scroller.fling(0, lastScrollY, 0, (int) -velocityY / 2, 0, 0, minY, maxY);
			setNextMessage(MESSAGE_SCROLL);
			return true;
		}
	};

	// Messages
	/** The message scroll. */
	private final int MESSAGE_SCROLL = 0;

	/** The message justify. */
	private final int MESSAGE_JUSTIFY = 1;

	/**
	 * Set next message to queue. Clears queue before.
	 * @param message the message to set
	 */
	private void setNextMessage(int message) {
		clearMessages();
		animationHandler.sendEmptyMessage(message);
	}

	/**
	 * Clears messages from queue.
	 */
	private void clearMessages() {
		animationHandler.removeMessages(MESSAGE_SCROLL);
		animationHandler.removeMessages(MESSAGE_JUSTIFY);
	}

	// animation handler
	/** The animation handler. */
	private Handler animationHandler = new Handler() {

		public void handleMessage(Message msg) {
			scroller.computeScrollOffset();
			int currY = scroller.getCurrY();
			int delta = lastScrollY - currY;
			lastScrollY = currY;
			if (delta != 0) {
				doScroll(delta);
			}

			// scrolling is not finished when it comes to final Y
			// so, finish it manually
			if (Math.abs(currY - scroller.getFinalY()) < MIN_DELTA_FOR_SCROLLING) {
				currY = scroller.getFinalY();
				scroller.forceFinished(true);
			}
			if (!scroller.isFinished()) {
				animationHandler.sendEmptyMessage(msg.what);
			} else if (msg.what == MESSAGE_SCROLL) {
				justify();
			} else {
				finishScrolling();
			}
		}
	};

	/**
	 * Justifies wheel.
	 */
	private void justify() {
		if (adapter == null) {
			return;
		}

		lastScrollY = 0;
		int offset = scrollingOffset;
		int itemHeight = getItemHeight();
		boolean needToIncrease = offset > 0 ? currentItem < adapter.getItemsCount() : currentItem > 0;
		if ((isCyclic || needToIncrease) && Math.abs((float) offset) > (float) itemHeight / 2) {
			if (offset < 0)
				offset += itemHeight + MIN_DELTA_FOR_SCROLLING;
			else
				offset -= itemHeight + MIN_DELTA_FOR_SCROLLING;
		}
		if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
			scroller.startScroll(0, 0, 0, offset, SCROLLING_DURATION);
			setNextMessage(MESSAGE_JUSTIFY);
		} else {
			finishScrolling();
		}
	}

	/**
	 * Starts scrolling.
	 */
	private void startScrolling() {
		if (!isScrollingPerformed) {
			isScrollingPerformed = true;
			notifyScrollingListenersAboutStart();
		}
	}

	/**
	 * Finishes scrolling.
	 */
	void finishScrolling() {
		if (isScrollingPerformed) {
			notifyScrollingListenersAboutEnd();
			isScrollingPerformed = false;
		}
		invalidateLayouts();
		invalidate();
	}

	/**
	 * Scroll the wheel.
	 *
	 * @param itemsToScroll the items to scroll
	 * @param time scrolling duration
	 */
	public void scroll(int itemsToScroll, int time) {
		scroller.forceFinished(true);
		lastScrollY = scrollingOffset;
		int offset = itemsToScroll * getItemHeight();
		scroller.startScroll(0, lastScrollY, 0, offset - lastScrollY, time);
		setNextMessage(MESSAGE_SCROLL);
		startScrolling();
	}

	

	/**
	 * Sets the value text size.
	 *
	 * @param textSize the new value text size
	 */
	public void setValueTextSize(int textSize) {
		this.valueTextSize = AbViewUtil.scaleValue(context, textSize);
		this.itemOffset = valueTextSize/5;
	}

	/**
	 * Gets the center select gradient colors.
	 *
	 * @return the center select gradient colors
	 */
	public int[] getCenterSelectGradientColors() {
		return centerSelectGradientColors;
	}

	/**
	 * Sets the center select gradient colors.
	 *
	 * @param centerSelectGradientColors the new center select gradient colors
	 */
	public void setCenterSelectGradientColors(int[] centerSelectGradientColors) {
		this.centerSelectGradientColors = centerSelectGradientColors;
	}

	/**
	 * Gets the center select stroke width.
	 *
	 * @return the center select stroke width
	 */
	public int getCenterSelectStrokeWidth() {
		return centerSelectStrokeWidth;
	}

	/**
	 * Sets the center select stroke width.
	 *
	 * @param centerSelectStrokeWidth the new center select stroke width
	 */
	public void setCenterSelectStrokeWidth(int centerSelectStrokeWidth) {
		this.centerSelectStrokeWidth = centerSelectStrokeWidth;
	}

	/**
	 * Gets the center select stroke color.
	 *
	 * @return the center select stroke color
	 */
	public int getCenterSelectStrokeColor() {
		return centerSelectStrokeColor;
	}

	/**
	 * Sets the center select stroke color.
	 *
	 * @param centerSelectStrokeColor the new center select stroke color
	 */
	public void setCenterSelectStrokeColor(int centerSelectStrokeColor) {
		this.centerSelectStrokeColor = centerSelectStrokeColor;
	}

	/**
	 * 设置中间的选中层图片.
	 *
	 * @param centerSelectDrawable the new center select drawable
	 */
	public void setCenterSelectDrawable(Drawable centerSelectDrawable) {
		this.centerSelectDrawable = centerSelectDrawable;
	}

	/**
	 * Sets the value text color.
	 *
	 * @param valueTextColor the new value text color
	 */
	public void setValueTextColor(int valueTextColor) {
		this.valueTextColor = valueTextColor;
	}

	/**
	 * Sets the label text color.
	 *
	 * @param labelTextColor the new label text color
	 */
	public void setLabelTextColor(int labelTextColor) {
		this.labelTextColor = labelTextColor;
	}

	/**
	 * Sets the label text size.
	 *
	 * @param labelTextSize the new label text size
	 */
	public void setLabelTextSize(int labelTextSize) {
		this.labelTextSize = AbViewUtil.scaleValue(context, labelTextSize);
	}

	/**
	 * Sets the additional item height.
	 *
	 * @param additionalItemHeight the new additional item height
	 */
	public void setAdditionalItemHeight(int additionalItemHeight) {
		this.additionalItemHeight = additionalItemHeight;
	}
	

	public interface AbOnPickerScrollListener {

		void onScrollingStarted(AbPickerView wheel);

		void onScrollingFinished(AbPickerView wheel);
	}

	public interface AbOnPickerChangedListener {

		void onChanged(AbPickerView wheel, int oldValue, int newValue);
	}
	
}
