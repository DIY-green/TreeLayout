package com.cheng.treelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.cheng.treelayout.exception.ItemConfusionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diygreen on 2017/5/28.
 */

public class TreeLayout extends ViewGroup {

    private static final String TAG     = "TreeLayout";

    private static final int TRUNK_WIDTH          = 1;                // 默认主干宽度
    private static final int BRANCH_WIDTH         = 40;               // 默认横枝宽度
    private static final int BRANCH_HEIGHT        = 1;                // 默认横枝高度/厚度
    private static final int TRUNK_COLOR          = 0XFF909090;       // 默认主干颜色
    private static final int BRANCH_COLOR         = 0XFF909090;       // 默认横枝颜色
    private static final int DEFAULT_RES          = 0;                // 默认 Res 值
    private static final int LEAF_WIDTH           = 30;               // 默认叶子的宽度
    private static final int LEAF_HEIGHT          = 30;               // 默认叶子的高度
    private static final int NODE_WIDTH           = 10;               // 默认枝节点宽度
    private static final int NODE_HEIGHT          = 10;               // 默认枝节点高度
    private static final int DEFAULT_PADDING      = 0;                // 默认缩进
    private static final int MARGIN_RIGHT_ITEM    = 0;                // 默认与右侧条目间距
    private static final int INDENT_VALUE         = 30;               // 默认条目的缩进值
    private static final int INDENT_LEVEL         = 1;                // 默认条目的缩进级别
    private static final boolean LINK_INDENTED    = false;            // 默认不连接到缩进的条目
    private static final int DEFAULT_TRUNK_TYPE   = 0;                // 默认主干类型为 normal
    private static final int TRUNK_TYPE_NORMAL    = 0;                // 主干类型 normal
    private static final int TRUNK_TYPE_FULL      = 1;                // 主干类型 full
    private static final int ITEM_TYPE_NORMAL     = 0;                // 默认条目类型
    private static final int ITEM_TYPE_LABEL      = 1;                // 标签条目类型
    private static final int ITEM_TYPE_HEADER     = 2;                // Header 条目类型
    private static final int ITEM_TYPE_FOOTER     = 3;                // Footer 条目类型

    //==========自定义属性==========//
    private int mBackground;                    // TreeLayout 的背景图片/颜色
    private int mLayoutGravity;                 // TreeLayout 的 LayoutGravity
    private int mTrunkWidth;                    // 主干宽度
    private int mTrunkColor;                    // 主干颜色
    private int mBranchWidth;                   // 横枝宽度
    private int mBranchHeight;                  // 横枝高度/厚度
    private int mBranchColor;                   // 横枝颜色
    private int mLeafRes;                       // 树叶的 Res
    private Drawable mLeafDrawable;             // 树叶的 Res 转换后的 Drawable
    private int mLeafWidth;                     // 树叶的宽度
    private int mLeafHeight;                    // 树叶的高度
    private int mNodeRes;                       // 枝节点的 Res
    private Drawable mNodeDrawable;             // 枝节点的 Res 转换后的 Drawable
    private int mNodeWidth;                     // 枝节点的宽度
    private int mNodeHeight;                    // 枝节点的高度
    private int mPadding;                       // TreeLayout 缩进
    private int mPaddingLeft;                   // TreeLayout 左缩进
    private int mPaddingTop;                    // TreeLayout 上缩进
    private int mPaddingRight;                  // TreeLayout 右缩进
    private int mPaddingBottom;                 // TreeLayout 下缩进
    private int mTreePadding;                   // TreeLayout 内部的树形结构缩进
    private int mTreePaddingLeft;               // TreeLayout 内部的树形结构左缩进
    private int mTreePaddingTop;                // TreeLayout 内部的树形结构上缩进
    private int mTreePaddingRight;              // TreeLayout 内部的树形结构右缩进
    private int mTreePaddingBottom;             // TreeLayout 内部的树形结构下缩进
    private int mMarginRightItem;               // TreeLayout 树形结构与右侧 Item 的间距
    private int mIndentValue;                   // TreeLayout 中条目的缩进值
    private boolean mIsLinkIndented;            // TreeLayout 的横枝是否连接到缩进的条目
    private int mTrunkType;                     // TreeLayout 主干类型：普通的目录树、通栏

    //==========坐标计算相关==========//
    private List<Integer> mItemTypeList;        // 保存所有条目类型的集合
    private List<ItemVO> mItemVOList;           // 保存所有条目类型 VO 的集合
    private int mBranchCount;                   // 所有横枝（也是枝节点）的数量
    private int mTotalHeight;                   // 所有条目占据的总高度
    private int mHeaderTotalHeight;             // 所有头部条目占据的总高度
    private int mFooterTotalHeight;             // 所有底部条目占据的总高度
    private int mTreeWidth;                     // TreeLayout 左侧树形结构所占宽度
    private boolean mHasNormalItem;             // 是否含有普通条目
    private float[] mBranchPointArr;
    private float[] mTrunkLine;
    private float mBranchStartX;                // 所有的横枝起点X
    private float mBranchEndX;                  // 所有的横枝终点X
    private int mFirstItemViewIndex;            // 第一个条目 View 的位置
    private int mLastItemViewIndex;             // 最后一个条目 View 的位置
    private float mTrunkStartX;                 // 主干起点X
    private float mTrunkStartY;                 // 主干起点Y
    private float mTrunkEndX;                   // 主干终点X
    private float mTrunkEndY;                   // 主干终点Y

    //==========绘制相关==========//
    private Paint mPaint;                       // 全局画笔绘制的时候按需修改
//    private Paint mTrunkPaint;                  // 绘制树干的画笔
//    private Paint mBranchPaint;                 // 绘制横枝的画笔

    public TreeLayout(Context context) {
        this(context, null);
    }

    public TreeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.TreeLayoutDefaultStyle);
    }

    public TreeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // AttributeSet 保存的是该 View 声明的所有的属性，
        //              可以通过它去获取（自定义的）属性
        // TypedArray 其实是用来简化工作的，如果布局中的属性的值是引用类型
        //              （比如：@dimen/dp100），如果使用 AttributeSet
        //              去获得最终的像素值，那么需要第一步拿到 id，第二步
        //              再去解析 id。而 TypedArray 正帮我们简化了这个过程
        initAttrs(context, attrs, defStyleAttr);
        initData();
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        // TODO 打印获取到的自定义属性值
        int count = attrs.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attrName = attrs.getAttributeName(i);
            String attrVal = attrs.getAttributeValue(i);
            Log.e(TAG, "attrName = " + attrName + " , attrVal = " + attrVal);
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TreeLayout, defStyleAttr, R.style.TreeLayoutDefaultStyle);
        if (typedArray != null) {
            mBackground = typedArray.getColor(R.styleable.TreeLayout_android_background, Color.WHITE);
            mLayoutGravity = typedArray.getInt(R.styleable.TreeLayout_android_layout_gravity, Gravity.LEFT);
            mTrunkWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_trunkWidth, TRUNK_WIDTH);
            mTrunkColor = typedArray.getColor(R.styleable.TreeLayout_trunkColor, TRUNK_COLOR);
            mBranchWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_branchWidth, BRANCH_WIDTH);
            mBranchHeight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_branchHeight, BRANCH_HEIGHT);
            mBranchColor = typedArray.getColor(R.styleable.TreeLayout_branchColor, BRANCH_COLOR);
            mLeafRes = typedArray.getResourceId(R.styleable.TreeLayout_leafSrc, DEFAULT_RES);
            mLeafDrawable = ContextCompat.getDrawable(getContext(), mLeafRes);
            mLeafWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_leafWidth, LEAF_WIDTH);
            mLeafHeight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_leafHeight, LEAF_HEIGHT);
            mNodeRes = typedArray.getResourceId(R.styleable.TreeLayout_nodeSrc, DEFAULT_RES);
            mNodeDrawable = ContextCompat.getDrawable(getContext(), mNodeRes);
            mNodeWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_nodeWidth, NODE_WIDTH);
            mNodeHeight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_nodeWidth, NODE_HEIGHT);
            mPadding = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_android_padding, DEFAULT_PADDING);
            mPaddingLeft = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_android_padding, DEFAULT_PADDING);
            mPaddingTop = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_android_padding, DEFAULT_PADDING);
            mPaddingRight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_android_padding, DEFAULT_PADDING);
            mPaddingBottom = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_android_padding, DEFAULT_PADDING);
            mTreePadding = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_treePadding, DEFAULT_PADDING);
            mTreePaddingLeft = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_treePaddingLeft, DEFAULT_PADDING);
            mTreePaddingTop = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_treePaddingTop, DEFAULT_PADDING);
            mTreePaddingRight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_treePaddingRight, DEFAULT_PADDING);
            mTreePaddingBottom = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_treePaddingBottom, DEFAULT_PADDING);
            mMarginRightItem = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_marginRightItem, MARGIN_RIGHT_ITEM);
            mIndentValue = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_indentValue, INDENT_VALUE);
            mIsLinkIndented = typedArray.getBoolean(R.styleable.TreeLayout_isLinkIndented, LINK_INDENTED);
            mTrunkType = typedArray.getInt(R.styleable.TreeLayout_trunkType, DEFAULT_TRUNK_TYPE);
            Log.e(TAG, "mBackground : " + mBackground + "\n" +
                       "mLayoutGravity : " + mLayoutGravity + "\n" +
                       "mTrunkWidth : " + mTrunkWidth + "\n" +
                       "mTrunkColor : " + mTrunkColor + "\n" +
                       "mBranchWidth : " + mBranchWidth + "\n" +
                       "mBranchHeight : " + mBranchHeight + "\n" +
                       "mBranchColor : " + mBranchColor + "\n" +
                       "mLeafRes : " + mLeafRes + "\n" +
                       "mLeafWidth : " + mLeafWidth + "\n" +
                       "mLeafHeight : " + mLeafHeight + "\n" +
                       "mNodeRes : " + mNodeRes + "\n" +
                       "mNodeWidth : " + mNodeWidth + "\n" +
                       "mNodeHeight : " + mNodeHeight + "\n" +
                       "mPadding : " + mPadding + "\n" +
                       "mPaddingLeft : " + mPaddingLeft + "\n" +
                       "mPaddingTop : " + mPaddingTop + "\n" +
                       "mPaddingRight : " + mPaddingRight + "\n" +
                       "mPaddingBottom : " + mPaddingBottom + "\n" +
                       "mTreePadding : " + mTreePadding + "\n" +
                       "mTreePaddingLeft : " + mTreePaddingLeft + "\n" +
                       "mTreePaddingTop : " + mTreePaddingTop + "\n" +
                       "mTreePaddingRight : " + mTreePaddingRight + "\n" +
                       "mTreePaddingBottom : " + mTreePaddingBottom + "\n" +
                       "mMarginRightItem : " + mMarginRightItem + "\n" +
                       "mIndentValue : " + mIndentValue + "\n" +
                       "mTrunkType : " + mTrunkType);
            typedArray.recycle();
        }
    }

    private void initData() {
        mItemTypeList = new ArrayList<>();
        mItemVOList = new ArrayList<>();
        mTreeWidth = mTreePaddingLeft + mTreePaddingRight + mTrunkWidth + mBranchWidth + mMarginRightItem;
        mBranchStartX = mPaddingLeft + mTreePaddingLeft;
        mBranchEndX = mBranchStartX + mTrunkWidth + mBranchWidth;
        mTrunkStartX = mBranchStartX;
        mTrunkEndX = mBranchStartX;
        mHasNormalItem = false;
        mTrunkLine = new float[4];
    }

    private void initPaint() {
        mPaint = new Paint();
        // Style有3种类型：
        // 类型1：Paint.Style.FILL_AND_STROKE（描边+填充）
        // 类型2：Paint.Style.FILL（只填充不描边）
        // 类型3：Paint.Style.STROKE（只描边不填充）
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setDither(true); // 设置防抖动
    }

    /**
     * 准备绘制主干的画笔
     * 通过调整全局画笔
     */
    private void prepareTrunkPaint() {
        mPaint.setColor(mTrunkColor);
        mPaint.setStrokeWidth(mTrunkWidth);
    }

    /**
     * 准备绘制横枝的画笔
     * 通过调整全局画笔
     */
    private void prepareBranchPaint() {
        mPaint.setColor(mBranchColor);
        mPaint.setStrokeWidth(mBranchHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        vertifyItemType();
        mTotalHeight = 0; // 由于可能进行多次 measure，所以每次测量前先把 mTotalHeight 清空
        int childCount = getChildCount();
        int maxWidth = 0; // 最大的宽度，将作为布局的宽度
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == GONE) continue;
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, mTotalHeight);
            int totalHeight = mTotalHeight;
            mTotalHeight = Math.max(
                    totalHeight,
                    mTotalHeight += (childView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin)
            );
            // 选出宽度最大一个的宽度作为布局的宽度
            maxWidth = Math.max(
                    maxWidth,
                    childView.getMeasuredWidth()
                            + layoutParams.leftMargin
                            + layoutParams.rightMargin
                            + layoutParams.indentLevel * mIndentValue
            );
        }
        // 将宽度加上 padding、左侧树形结构占据的宽度，得到最终宽度
        maxWidth += (mPaddingLeft + mPaddingRight + mTreeWidth);
        mTotalHeight += (mPaddingTop + mPaddingBottom);
        int heightSize = mTotalHeight;
        heightSize = Math.max(heightSize, getSuggestedMinimumHeight());
        // 对高度进行纠正，如果布局高度为 wrapContent，则最终高度为 mTotalHeight 的大小
        // 如果为布局高度为精确值(Exactly)，则最终高度为精确值
        int heightSizeAndState = resolveSizeAndState(heightSize, heightMeasureSpec, 0);
        heightSize = heightSizeAndState & MEASURED_SIZE_MASK;
        setMeasuredDimension(maxWidth, heightSize);
        Log.e(TAG, "Width = " + maxWidth + "Height = " + heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "boolean changed = " + changed + ", int l = " + l + ", int t = " + t + ", int r = " + r + ", int b = " + b);
        int childCount = getChildCount();
        if (childCount < 1) return;
        int childTop = mPaddingTop;
        for (int i = 0, j = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            if (childView.getVisibility() == GONE) continue;
            childTop = layoutChildByItemType(childTop, childView, layoutParams);

            // TODO 在 Layout 的时候确定所有需要绘制的坐标
            ItemVO itemVO = ItemVO.newInstance(childView, layoutParams);
            mItemVOList.add(itemVO);
            Log.e(TAG, mItemVOList.toString());
            if (layoutParams.itemType == ITEM_TYPE_NORMAL) {
                mBranchCount++;
                int branchY = calculateChildViewLeftCenterY(childView);
                mBranchPointArr[j++] = mBranchStartX;
                mBranchPointArr[j++] = branchY;
                mBranchPointArr[j++] = mIsLinkIndented ? childView.getLeft() - mMarginRightItem - mTreePaddingRight : mBranchEndX;
                mBranchPointArr[j++] = branchY;
                if (i == mFirstItemViewIndex) {
                    mTrunkStartY = branchY;
                } else if (i == mLastItemViewIndex) {
                    mTrunkEndY = branchY;
                }
            }
        }
        if (mTrunkStartY < 0) {
            View firstChildView = getChildAt(0);
            View lastChildView = getChildAt(childCount - 1);
            mTrunkStartY = calculateChildViewLeftCenterY(firstChildView);
            mTrunkEndY = calculateChildViewLeftCenterY(lastChildView);
        }
        if (mTrunkType == TRUNK_TYPE_FULL) {
            mTrunkStartY -= mTreePaddingTop;
            mTrunkEndY -= mTreePaddingBottom;
        }
        mTrunkLine[0] = mTrunkStartX;
        mTrunkLine[1] = mTrunkStartY;
        mTrunkLine[2] = mTrunkEndX;
        mTrunkLine[3] = mTrunkEndY;
    }

    private int calculateChildViewLeftCenterY(View childView) {
        return childView.getTop() + childView.getMeasuredHeight() / 2;
    }

    private int layoutChildByItemType(int childTop, View childView, LayoutParams layoutParams) {
        int childLeft;
        int childRight;
        int childBottom;
        int childWidth = childView.getMeasuredWidth();
        int childHeight = childView.getMeasuredHeight();
        int itemType = layoutParams.itemType;
        childLeft = mTreeWidth + mPaddingLeft + layoutParams.leftMargin;
        childTop += layoutParams.topMargin;
        switch (itemType) {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                // Header 布局受下面四个因素
                //      paddingLeft
                //      paddingTop
                //      leftMargin
                //      topMargin
                // 的影响
                childLeft -= mTreeWidth;
                break;
            case ITEM_TYPE_NORMAL:
            case ITEM_TYPE_LABEL:
                childLeft += calculateIndent(layoutParams.indentLevel);
                break;
        }
        childRight = childLeft + childWidth;
        childBottom = childTop + childHeight;
        childView.layout(childLeft, childTop, childRight, childBottom);
        childTop += (layoutParams.bottomMargin + childView.getMeasuredHeight());
        return childTop;
    }

    private int calculateIndent(int indentLevel) {
        return indentLevel * mIndentValue;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 如果不包含普通 Item 就不需要绘制的左侧树形结构
        if (!mHasNormalItem) return;
        int itemSize = mItemVOList.size();
        if (itemSize < 1) return;
        int childCount = getChildCount();
        if (childCount <= 1) return;

        //==========开始绘制==========//

        prepareTrunkPaint();
        canvas.drawLines(mTrunkLine, mPaint);
        prepareBranchPaint();
        // 指定跳过前 offset 个数据，取出 count 个数据绘制直线
        int dataCount = mBranchCount * 4;
        canvas.drawLines(mBranchPointArr, 0, dataCount, mPaint);               //有选择地绘制直线
        drawNodeLeaf(mBranchPointArr, dataCount, canvas);

        /*// TODO 下面是一些测试代码
        if (mLeafDrawable != null) {
            mLeafDrawable.setBounds(60, 60, 100, 100);
            mNodeDrawable.setBounds(90, 90, 150, 150);
            mLeafDrawable.draw(canvas);
            mNodeDrawable.draw(canvas);
        }*/
    }

    private void drawNodeLeaf(float[] branchPointArr, int dataCount, Canvas canvas) {
        int nodeCenterX; // 枝节点中心 X
        int nodeCenterY; // 枝节点中心 Y
        int leafCenterX; // 叶子中心 X
        int leafCenterY; // 叶子中心 Y
        int nodeHRadius = mNodeWidth / 2;
        int nodeVRadius = mNodeHeight / 2;
        int leafHRadius = mLeafWidth / 2;
        int leafVRadius = mLeafHeight / 2;
        for (int i = 0; i < dataCount; i+=4) {
            nodeCenterX = (int) branchPointArr[i];
            nodeCenterY = (int) branchPointArr[i+1];
            leafCenterX = (int) branchPointArr[i+2];
            leafCenterY = (int) branchPointArr[i+3];
            if (mNodeDrawable != null) {
                mNodeDrawable.setBounds(
                        nodeCenterX - nodeHRadius,
                        nodeCenterY - nodeVRadius,
                        nodeCenterX + nodeHRadius,
                        nodeCenterY + nodeVRadius);
                mNodeDrawable.draw(canvas);
            }
            if (mLeafDrawable != null) {
                mLeafDrawable.setBounds(
                        leafCenterX - leafHRadius,
                        leafCenterY - leafVRadius,
                        leafCenterX + leafHRadius,
                        leafCenterY + leafVRadius);
                mLeafDrawable.draw(canvas);
            }
        }
    }

    /**
     * 对添加到其中的子 View 进行合法校验
     * 规则如下：
     * Header 可以有多个，但是 Header 必须在顶部，也就是 Header 中间和上面不能有其他类型的 Item
     * Footer 可以有多个，但是 Footer 必须在底部，也就是 Footer 中间和下面不能有其他类型的 Item
     */
    private void vertifyItemType() {
        mFirstItemViewIndex = -1;
        mLastItemViewIndex = -1;
        int count = getChildCount();
        if (count < 1) {
            mHasNormalItem = false;
            return;
        }
        mBranchPointArr = new float[count<<2];
        int firstHeaderIndex = -1;
        int firstFooterIndex = -1;
        int lastHeaderIndex = -1;
        int lastFooterIndex = -1;
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            int itemType = layoutParams.itemType;
            if (itemType == ITEM_TYPE_HEADER) {
                if (firstHeaderIndex < 0) {
                    firstHeaderIndex = i;
                    lastHeaderIndex = i;
                } else {
                    lastHeaderIndex = i;
                }
            } else if (itemType == ITEM_TYPE_FOOTER) {
                if (firstFooterIndex < 0) {
                    firstFooterIndex = i;
                    lastFooterIndex = i;
                } else {
                    lastFooterIndex = i;
                }
            } else {
                if (itemType == ITEM_TYPE_NORMAL) {
                    if (mFirstItemViewIndex < 0) {
                        mFirstItemViewIndex = i;
                    }
                    mLastItemViewIndex = i;
                }
                mHasNormalItem = true;
                // 有其他条目在 Header 中，或者 Header 并没有全部集中在顶部
                if (i < firstHeaderIndex
                        || (lastHeaderIndex > 0 && i < lastHeaderIndex)
                        || (firstFooterIndex > 0 && i > firstFooterIndex && i < lastFooterIndex)
                        || (lastFooterIndex > 0 && i > lastFooterIndex)) {
                    throw ItemConfusionException.newInstance(getContext().getString(R.string.treelayout_item_confusion_message));
                }
            }
        }
        // 这种方式不靠谱，如果最后一个 Header 后面跟的不是 normal 类型的 Item 则计算错误
//        mFirstItemViewIndex = lastHeaderIndex + 1;
        // 如果定位到的第一条和最后一条 ItemView 越界，则置为 -1
        if (mFirstItemViewIndex >= count) {
            mFirstItemViewIndex = -1;
        }
        if (mLastItemViewIndex < 0 || mLastItemViewIndex >= count) {
            mLastItemViewIndex = -1;
        }
    }

    /**
     * 改写生成LayoutParams的方法
     */
    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width,p.height);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        private static final int DEFAULT_ITEM_TYPE = 0;     // 条目类型默认值：NORMAL
        private static final int DEFAULT_INDENT_LEVEL = 0;  // 条目缩进级别默认值：0

        public int itemType;                               // 条目类型
        public int indentLevel;                            // 缩进级别

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TreeLayout_Layout);
            if (typedArray != null) {
                itemType = typedArray.getInt(R.styleable.TreeLayout_Layout_itemType, DEFAULT_ITEM_TYPE);
                indentLevel = typedArray.getInt(R.styleable.TreeLayout_Layout_indentLevel, DEFAULT_INDENT_LEVEL);
                typedArray.recycle();
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

}
