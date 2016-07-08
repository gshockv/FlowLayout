package io.github.gshockv.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {

    private int deviceWidth;
//    private int leftWidth;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.d("FlowLayout", "FlowLayout::onMeasure()");

        int childCount = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int rowCount = 0;
        int childState = 0;
        int leftWidth = 0;

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentPaddingLeft = getPaddingLeft();
        int parentPaddingTop = getPaddingTop();
        int parentPaddingRight = getPaddingRight();
        int parentPaddingBottom = getPaddingBottom();

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            leftWidth += child.getMeasuredWidth();

            if ((leftWidth / parentWidth) > rowCount) {
                maxHeight += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                rowCount++;
            } else {
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight()) + parentPaddingTop + parentPaddingBottom;
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth()) + parentPaddingLeft + parentPaddingRight;

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int ieft, int top, int right, int bottom) {
        final int childCount = getChildCount();
        int currentWidth;
        int currentHeight;
        int currentLeft;
        int currentTop;
        int maxHeight;

        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();

        final int childWidth = childRight - childLeft;
        final int childHeight = childBottom - childTop;

        maxHeight = 0;
        currentLeft = childLeft;
        currentTop = childTop;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            currentWidth = child.getMeasuredWidth() + lp.rightMargin;
            currentHeight = child.getMeasuredHeight() + lp.bottomMargin;

            if (currentLeft + currentWidth >= childRight) {
                currentLeft = childLeft;
                currentTop += maxHeight;
                maxHeight = 0;
            }

            child.layout(
                    currentLeft + lp.leftMargin,
                    currentTop + lp.topMargin,
                    currentLeft + currentWidth,
                    currentTop + currentHeight);

            if (maxHeight < currentHeight) {
                maxHeight = currentHeight;
            }
            currentLeft += currentWidth;
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
