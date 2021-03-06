package com.cheng.treelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.cheng.treelayout.exception.ItemConfusionException;

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
    private static final boolean DEFAULT_FALSE    = false;            // 默认 false
    private static final boolean DEFAULT_TRUE     = true;             // 默认 true
    private static final int DEFAULT_RES          = 0;                // 默认 Res 值
    private static final int LEAF_WIDTH           = 30;               // 默认叶子的宽度
    private static final int LEAF_HEIGHT          = 30;               // 默认叶子的高度
    private static final int NODE_WIDTH           = 10;               // 默认枝节点宽度
    private static final int NODE_HEIGHT          = 10;               // 默认枝节点高度
    private static final int DEFAULT_PADDING      = 0;                // 默认缩进
    private static final int MARGIN_RIGHT_ITEM    = 0;                // 默认与右侧条目间距
    private static final int INDENT_VALUE         = 30;               // 默认条目的缩进值
    private static final int DEFAULT_INDENT_LEVEL = 0;                // 默认条目的缩进级别：不缩进
    private static final boolean LINK_INDENTED    = false;            // 默认不连接到缩进的条目
    private static final boolean USE_ANIMATION    = true;             // 默认使用默认的展开关闭动画
    private static final boolean TREE_EXPANDED    = true;             // 默认树是展开的
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
    private boolean mIsShowBranch;              // 是否显示横枝
    private boolean mIsCustomBranchEnable;      // 是否允许自定义Branch
    private int mBranchWidth;                   // 横枝宽度
    private int mBranchHeight;                  // 横枝高度/厚度
    private int mBranchColor;                   // 横枝颜色
    private boolean mIsShowNode;                // 是否显示枝节点
    private int mNodeSrc;                       // 枝节点的 Res
    private Drawable mNodeDrawable;             // 枝节点的 Res 转换后的 Drawable
    private int mNodeWidth;                     // 枝节点的宽度
    private int mNodeHeight;                    // 枝节点的高度
    private boolean mIsShowLeaf;                // 是否显示叶子
    private int mLeafSrc;                       // 树叶的 Res
    private Drawable mLeafDrawable;             // 树叶的 Res 转换后的 Drawable
    private int mLeafWidth;                     // 树叶的宽度
    private int mLeafHeight;                    // 树叶的高度
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
    private boolean mUseDefaultAnimation;       // TreeLayout 的展开收起是否使用默认动画
    private boolean mIsTreeExpanded;            // TreeLayout 的树形结构是否正在展示
    private boolean mIsToggleEnable;            // TreeLayout 的树形结构是否允许展开/折叠操作
    private int mTrunkType;                     // TreeLayout 主干类型：普通的目录树、通栏

    //==========坐标计算相关==========//
    private int mBranchCount;                   // 所有横枝（也是枝节点）的数量
    private int mTotalHeight;                   // 所有条目占据的总高度
    private int mTreeWidth;                     // TreeLayout 左侧树形结构所占宽度
    private int mFirstTreeItemIndex;            // 树形结构中第一个条目（非 Header/Footer）的位置
    private int mLastTreeItemIndex;             // 树形结构中最后一个条目（非 Header/Footer）的位置
    private int mFirstNormalItemIndex;          // 第一个普通条目的位置
    private int mLastNormalItemIndex;           // 最后一个普通条目的位置
    private int[] mBranchIndexArr;              // 所有横枝条目的在 Child 集合中的位置
    private float[] mBranchCoordinateArr;       // 所有横枝坐标数组
    private float[] mSingleBranchCoordinateArr; // 单条横枝坐标数组
    private float[] mTrunkCoordinateArr;        // 主干坐标数组
    private float mBranchStartX;                // 所有的横枝起点X
    private float mBranchEndX;                  // 所有的横枝终点X
    private float mTrunkStartX;                 // 主干起点X
    private float mTrunkStartY;                 // 主干起点Y
    private float mTrunkEndX;                   // 主干终点X
    private float mTrunkEndY;                   // 主干终点Y

    //==========绘制相关==========//
    private Paint mPaint;                       // 全局画笔绘制的时候按需修改

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
        initListener();
    }

    private void initListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsToggleEnable) return;
                if (mIsTreeExpanded) {
                    collapseTree();
                } else {
                    expandTree();
                }
                mIsTreeExpanded = !mIsTreeExpanded;
            }
        });
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TreeLayout, defStyleAttr, R.style.TreeLayoutDefaultStyle);
        if (typedArray != null) {
            mBackground = typedArray.getColor(R.styleable.TreeLayout_android_background, Color.WHITE);
            mLayoutGravity = typedArray.getInt(R.styleable.TreeLayout_android_layout_gravity, Gravity.LEFT);
            mTrunkWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_trunkWidth, TRUNK_WIDTH);
            mTrunkColor = typedArray.getColor(R.styleable.TreeLayout_trunkColor, TRUNK_COLOR);
            mIsShowBranch = typedArray.getBoolean(R.styleable.TreeLayout_isShowBranch, DEFAULT_TRUE);
            mIsCustomBranchEnable = typedArray.getBoolean(R.styleable.TreeLayout_isCustomBranchEnable, DEFAULT_FALSE);
            mBranchWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_branchWidth, BRANCH_WIDTH);
            mBranchHeight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_branchHeight, BRANCH_HEIGHT);
            mBranchColor = typedArray.getColor(R.styleable.TreeLayout_branchColor, BRANCH_COLOR);
            mIsShowNode = typedArray.getBoolean(R.styleable.TreeLayout_isShowLeaf, DEFAULT_FALSE);
            mNodeSrc = typedArray.getResourceId(R.styleable.TreeLayout_nodeSrc, DEFAULT_RES);
            if (mNodeSrc != 0) {
                mNodeDrawable = ContextCompat.getDrawable(context, mNodeSrc);
            }
            mNodeWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_nodeWidth, NODE_WIDTH);
            mNodeHeight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_nodeWidth, NODE_HEIGHT);
            mIsShowLeaf = typedArray.getBoolean(R.styleable.TreeLayout_isShowLeaf, DEFAULT_FALSE);
            mLeafSrc = typedArray.getResourceId(R.styleable.TreeLayout_leafSrc, DEFAULT_RES);
            if (mLeafSrc != 0) {
                mLeafDrawable = ContextCompat.getDrawable(context, mLeafSrc);
            }
            mLeafWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_leafWidth, LEAF_WIDTH);
            mLeafHeight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_leafHeight, LEAF_HEIGHT);
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
            mUseDefaultAnimation = typedArray.getBoolean(R.styleable.TreeLayout_useDefaultAnimation, USE_ANIMATION);
            mIsTreeExpanded = typedArray.getBoolean(R.styleable.TreeLayout_isTreeExpanded, TREE_EXPANDED);
            mIsToggleEnable = typedArray.getBoolean(R.styleable.TreeLayout_isToggleEnable, DEFAULT_TRUE);
            mTrunkType = typedArray.getInt(R.styleable.TreeLayout_trunkType, DEFAULT_TRUNK_TYPE);
            typedArray.recycle();
        }
    }

    private void initData() {
        mTreeWidth = mTreePaddingLeft + mTreePaddingRight + mTrunkWidth + mBranchWidth + mMarginRightItem;
        mBranchStartX = mPaddingLeft + mTreePaddingLeft;
        mBranchEndX = mBranchStartX + mTrunkWidth + mBranchWidth;
        mTrunkStartX = mBranchStartX;
        mTrunkEndX = mBranchStartX;
        mTrunkCoordinateArr = new float[4];
        mSingleBranchCoordinateArr = new float[4];
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

    private void prepareBranchPaint(int color, float strokeWidth) {
        if (color == LayoutParams.CUSTOM_DEFAULT_COLOR) {
            color = mBranchColor;
        }
        if (strokeWidth <= 0) {
            strokeWidth = mBranchHeight;
        }
        mPaint.setColor(color);
        mPaint.setStrokeWidth(strokeWidth);
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
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount < 1) return;
        int childTop = mPaddingTop;
        for (int i = 0, j = 0, z = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            if (childView.getVisibility() == GONE) continue;
            childTop = layoutChildByItemType(childTop, childView, layoutParams);
            // 在 onLayout 的时候确定所有需要绘制的坐标
            if (layoutParams.itemType == ITEM_TYPE_NORMAL) {
                int branchY = calculateChildViewLeftCenterY(childView);
                mBranchCoordinateArr[j++] = mBranchStartX;
                mBranchCoordinateArr[j++] = branchY;
                mBranchCoordinateArr[j++] = mIsLinkIndented ? childView.getLeft() - mMarginRightItem - mTreePaddingRight : mBranchEndX;
                mBranchCoordinateArr[j++] = branchY;
                if (i == mFirstNormalItemIndex) {
                    mTrunkStartY = branchY;
                } else if (i == mLastNormalItemIndex) {
                    mTrunkEndY = branchY;
                }
                mBranchIndexArr[z] = i;
                z++;
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
        mTrunkCoordinateArr[0] = mTrunkStartX;
        mTrunkCoordinateArr[1] = mTrunkStartY;
        mTrunkCoordinateArr[2] = mTrunkEndX;
        mTrunkCoordinateArr[3] = mTrunkEndY;
    }

    private int calculateChildViewLeftCenterY(View childView) {
        // 需要根据 childView 中指定的，横枝指向的 View 以及所指向 View 顶部的偏移值来确定
        // 如果没有明确指的指向 View，则取 childView 的第一个孩子，如果没有明确的偏移值，则取指向 View 垂直方向的中点
        View targetView = childView;
        LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
        int parentTop = 0;
        if (childView instanceof ViewGroup) {
            ViewGroup childViewGroup = (ViewGroup) childView;
            int childCount = childViewGroup.getChildCount();
            int targetIndex = 0;
            if (childCount > 1) {
                // 非法值
                if (layoutParams.branchTargetIndex < 0 || layoutParams.branchTargetIndex >= childCount) {
                    layoutParams.branchTargetIndex = 0; // 默认指向第一个孩子
                }
                targetIndex = layoutParams.branchTargetIndex;
            }
            targetView = childViewGroup.getChildAt(targetIndex);
            if (targetView != null) {
                parentTop = childView.getTop();
            } else {
                targetView = childView;
            }
        }
        int offsetTop = targetView.getMeasuredHeight() / 2;
        if (layoutParams.offsetTargetTop >= 0) {
            offsetTop = layoutParams.offsetTargetTop;
        }
        return parentTop + targetView.getTop() + offsetTop;
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
                // 的影响，Header 是在树形结构之上的
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
        //==========排除不需要绘制的情况==========//
        if (!mIsTreeExpanded) return;
        if (mBranchCount < 1) return; // 如果不包含普通 Item 就不需要绘制的左侧树形结构
        int childCount = getChildCount();
        if (childCount <= 1) return;

        //==========开始绘制==========//
        prepareTrunkPaint();
        drawTrunk(canvas);
        drawBranchNodeLeaf(mBranchCoordinateArr, canvas);
    }

    private void drawTrunk(Canvas canvas) {
        canvas.drawLines(mTrunkCoordinateArr, mPaint);
    }

    private void drawBranchNodeLeaf(float[] branchCoordinateArr, Canvas canvas) {
        int nodeCenterX; // 枝节点中心 X
        int nodeCenterY; // 枝节点中心 Y
        int leafCenterX; // 叶子中心 X
        int leafCenterY; // 叶子中心 Y
        int branchStartX; // 横枝起点 X
        int branchStartY; // 横枝起点 Y
        int branchEndX; // 横枝终点 X
        int branchEndY; // 横枝终点 Y
        int nodeHRadius = mNodeWidth / 2;
        int nodeVRadius = mNodeHeight / 2;
        int leafHRadius = mLeafWidth / 2;
        int leafVRadius = mLeafHeight / 2;
        for (int i = 0, count = branchCoordinateArr.length; i < count; i+=4) {
            branchStartX = nodeCenterX = (int) branchCoordinateArr[i];
            branchStartY = nodeCenterY = (int) branchCoordinateArr[i+1];
            branchEndX = leafCenterX = (int) branchCoordinateArr[i+2];
            branchEndY = leafCenterY = (int) branchCoordinateArr[i+3];
            // childIndex = mBranchIndexArr[i / 4];
            LayoutParams layoutParams = (LayoutParams) getChildAt(mBranchIndexArr[i / 4]).getLayoutParams();
            drawBranch(canvas, branchStartX, branchStartY, branchEndX, branchEndY, layoutParams);
            drawNode(canvas, nodeCenterX, nodeCenterY, nodeHRadius, nodeVRadius, layoutParams.customNodeDrawable);
            drawLeaf(canvas, leafCenterX, leafCenterY, leafHRadius, leafVRadius, layoutParams.customLeafDrawable);
        }
    }

    private void drawBranch(Canvas canvas, int branchStartX, int branchStartY, int branchEndX, int branchEndY, LayoutParams layoutParams) {
        if (!mIsShowBranch) return;
        if (!mIsCustomBranchEnable) {
            prepareBranchPaint();
            canvas.drawLines(mBranchCoordinateArr, mPaint);
            return;
        }
        prepareBranchPaint(layoutParams.customBranchColor, layoutParams.customBranchHeight);
        int customBranchWidth = layoutParams.customBranchWidth * (layoutParams.indentLevel + 1);
        int defaultBranchWidth = branchEndX - branchStartX;
        int branchWidth = defaultBranchWidth;
        if (customBranchWidth >= 0) {
            branchWidth = Math.min(customBranchWidth, defaultBranchWidth);
        }
        branchEndX = branchStartX + branchWidth;
        mSingleBranchCoordinateArr[0] = branchStartX;
        mSingleBranchCoordinateArr[1] = branchStartY;
        mSingleBranchCoordinateArr[2] = branchEndX;
        mSingleBranchCoordinateArr[3] = branchEndY;
        canvas.drawLines(mSingleBranchCoordinateArr, mPaint);
    }

    private void drawNode(Canvas canvas, int nodeCenterX, int nodeCenterY, int nodeHRadius, int nodeVRadius, Drawable customNodeDrawable) {
        if (!mIsShowNode) return;
        Drawable nodeDrawable;
        nodeDrawable = mNodeDrawable;
        if (customNodeDrawable != null) {
            nodeDrawable = customNodeDrawable;
        }
        drawDrawable(
                canvas,
                nodeCenterX - nodeHRadius,
                nodeCenterY - nodeVRadius,
                nodeCenterX + nodeHRadius,
                nodeCenterY + nodeVRadius,
                nodeDrawable);
    }

    private void drawLeaf(Canvas canvas, int leafCenterX, int leafCenterY, int leafHRadius, int leafVRadius, Drawable customLeafDrawable) {
        if (!mIsShowLeaf) return;
        Drawable leafDrawable;
        leafDrawable = mLeafDrawable;
        if (customLeafDrawable != null) {
            leafDrawable = customLeafDrawable;
        }
        drawDrawable(
                canvas,
                leafCenterX - leafHRadius,
                leafCenterY - leafVRadius,
                leafCenterX + leafHRadius,
                leafCenterY + leafVRadius,
                leafDrawable);
    }

    private void drawDrawable(Canvas canvas, int left, int top, int right, int bottom, Drawable leafDrawable) {
        if (leafDrawable == null) return;
        leafDrawable.setBounds(
                left,
                top,
                right,
                bottom);
        leafDrawable.draw(canvas);
    }

    /**
     * 对添加到其中的子 View 进行合法校验
     * 规则如下：
     * Header 可以有多个，但是 Header 必须在顶部，也就是 Header 中间和上面不能有其他类型的 Item
     * Footer 可以有多个，但是 Footer 必须在底部，也就是 Footer 中间和下面不能有其他类型的 Item
     */
    private void vertifyItemType() {
        mFirstNormalItemIndex = -1;
        mLastNormalItemIndex = -1;
        mFirstTreeItemIndex = -1;
        mLastTreeItemIndex = -1;
        mBranchCount = 0;
        int count = getChildCount();
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
                    mBranchCount++;
                    if (mFirstNormalItemIndex < 0) {
                        mFirstNormalItemIndex = i;
                    }
                    mLastNormalItemIndex = i;
                }
                // 有其他条目在 Header 中，或者 Header 并没有全部集中在顶部
                if (i < firstHeaderIndex
                        || (lastHeaderIndex > 0 && i < lastHeaderIndex)
                        || (firstFooterIndex > 0 && i > firstFooterIndex && i < lastFooterIndex)
                        || (lastFooterIndex > 0 && i > lastFooterIndex)) {
                    throw ItemConfusionException.newInstance(getContext().getString(R.string.treelayout_item_confusion_message));
                }
            }
        }
        mBranchCoordinateArr = new float[mBranchCount<<2];
        mBranchIndexArr = new int[mBranchCount];
        // 如果定位到的第一条和最后一条 ItemView 越界，则置为 -1
        if (mFirstNormalItemIndex >= count) {
            mFirstNormalItemIndex = -1;
        }
        if (mLastNormalItemIndex < 0 || mLastNormalItemIndex >= count) {
            mLastNormalItemIndex = -1;
        }
        mFirstTreeItemIndex = lastHeaderIndex + 1;
        mLastTreeItemIndex = firstFooterIndex - 1;
        if (mFirstTreeItemIndex < 0 || mFirstTreeItemIndex >= count) {
            mFirstTreeItemIndex = 0;
        }
        if (mLastTreeItemIndex < 0 || mLastTreeItemIndex >= count) {
            mLastTreeItemIndex = count;
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

    //==========逻辑方法==========//
    private void collapseTree() {
        if (checkTreeItemRange()) return;
        if (mUseDefaultAnimation) {

        } else {
            showTree(false);
        }
    }

    private boolean checkTreeItemRange() {
        if (mFirstTreeItemIndex < 0
                || mLastTreeItemIndex < 0
                || mLastTreeItemIndex <= mFirstTreeItemIndex) return true;
        return false;
    }

    private void showTree(boolean isShow) {
        for (int i = mFirstTreeItemIndex; i <= mLastTreeItemIndex; i++) {
            getChildAt(i).setVisibility(isShow ? VISIBLE : GONE);
        }
    }

    private void expandTree() {
        if (checkTreeItemRange()) return;
        if (mUseDefaultAnimation) {

        } else {
            showTree(true);
        }

    }

    public int getLayoutGravity() {
        return mLayoutGravity;
    }

    public void setLayoutGravity(int layoutGravity) {
        this.mLayoutGravity = layoutGravity;
    }

    public int getTrunkWidth() {
        return mTrunkWidth;
    }

    public void setTrunkWidth(int trunkWidth) {
        this.mTrunkWidth = trunkWidth;
    }

    public int getTrunkColor() {
        return mTrunkColor;
    }

    public void setTrunkColor(int trunkColor) {
        this.mTrunkColor = trunkColor;
    }

    public boolean isShowBranch() {
        return mIsShowBranch;
    }

    public void setIsShowBranch(boolean isShowBranch) {
        this.mIsShowBranch = isShowBranch;
    }

    public boolean isCustomBranchEnable() {
        return mIsCustomBranchEnable;
    }

    public void setIsCustomBranchEnable(boolean isCustomBranchEnable) {
        this.mIsCustomBranchEnable = isCustomBranchEnable;
    }

    public int getBranchWidth() {
        return mBranchWidth;
    }

    public void setBranchWidth(int branchWidth) {
        this.mBranchWidth = branchWidth;
    }

    public int getBranchHeight() {
        return mBranchHeight;
    }

    public void setBranchHeight(int branchHeight) {
        this.mBranchHeight = branchHeight;
    }

    public int getBranchColor() {
        return mBranchColor;
    }

    public void setBranchColor(int branchColor) {
        this.mBranchColor = branchColor;
    }

    public boolean isShowNode() {
        return mIsShowNode;
    }

    public void setIsShowNode(boolean isShowNode) {
        this.mIsShowNode = isShowNode;
    }

    public int getNodeSrc() {
        return mNodeSrc;
    }

    public void setNodeSrc(int nodeSrc) {
        this.mNodeSrc = nodeSrc;
    }

    public Drawable getNodeDrawable() {
        return mNodeDrawable;
    }

    public void setNodeDrawable(Drawable nodeDrawable) {
        this.mNodeDrawable = nodeDrawable;
    }

    public int getNodeWidth() {
        return mNodeWidth;
    }

    public void setNodeWidth(int nodeWidth) {
        this.mNodeWidth = nodeWidth;
    }

    public int getNodeHeight() {
        return mNodeHeight;
    }

    public void setNodeHeight(int nodeHeight) {
        this.mNodeHeight = nodeHeight;
    }

    public boolean isShowLeaf() {
        return mIsShowLeaf;
    }

    public void setIsShowLeaf(boolean isShowLeaf) {
        this.mIsShowLeaf = isShowLeaf;
    }

    public int getLeafSrc() {
        return mLeafSrc;
    }

    public void setLeafSrc(int leafSrc) {
        this.mLeafSrc = leafSrc;
    }

    public Drawable getLeafDrawable() {
        return mLeafDrawable;
    }

    public void setLeafDrawable(Drawable leafDrawable) {
        this.mLeafDrawable = leafDrawable;
    }

    public int getLeafWidth() {
        return mLeafWidth;
    }

    public void setLeafWidth(int leafWidth) {
        this.mLeafWidth = leafWidth;
    }

    public int getLeafHeight() {
        return mLeafHeight;
    }

    public void setLeafHeight(int leafHeight) {
        this.mLeafHeight = leafHeight;
    }

    public int getPadding() {
        return mPadding;
    }

    public void setPadding(int padding) {
        this.mPadding = padding;
    }

    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.mPaddingLeft = paddingLeft;
    }

    public int getPaddingTop() {
        return mPaddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.mPaddingTop = paddingTop;
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.mPaddingRight = paddingRight;
    }

    public int getPaddingBottom() {
        return mPaddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.mPaddingBottom = paddingBottom;
    }

    public int getTreePadding() {
        return mTreePadding;
    }

    public void setTreePadding(int treePadding) {
        this.mTreePadding = treePadding;
    }

    public int getTreePaddingLeft() {
        return mTreePaddingLeft;
    }

    public void setTreePaddingLeft(int treePaddingLeft) {
        this.mTreePaddingLeft = treePaddingLeft;
    }

    public int getTreePaddingTop() {
        return mTreePaddingTop;
    }

    public void setTreePaddingTop(int treePaddingTop) {
        this.mTreePaddingTop = treePaddingTop;
    }

    public int getTreePaddingRight() {
        return mTreePaddingRight;
    }

    public void setTreePaddingRight(int treePaddingRight) {
        this.mTreePaddingRight = treePaddingRight;
    }

    public int getTreePaddingBottom() {
        return mTreePaddingBottom;
    }

    public void setTreePaddingBottom(int treePaddingBottom) {
        this.mTreePaddingBottom = treePaddingBottom;
    }

    public int getMarginRightItem() {
        return mMarginRightItem;
    }

    public void setMarginRightItem(int marginRightItem) {
        this.mMarginRightItem = marginRightItem;
    }

    public int getIndentValue() {
        return mIndentValue;
    }

    public void setIndentValue(int indentValue) {
        this.mIndentValue = indentValue;
    }

    public boolean isLinkIndented() {
        return mIsLinkIndented;
    }

    public void setIsLinkIndented(boolean isLinkIndented) {
        this.mIsLinkIndented = isLinkIndented;
    }

    public boolean isUseDefaultAnimation() {
        return mUseDefaultAnimation;
    }

    public void setUseDefaultAnimation(boolean useDefaultAnimation) {
        this.mUseDefaultAnimation = useDefaultAnimation;
    }

    public boolean isTreeExpanded() {
        return mIsTreeExpanded;
    }

    public void setIsTreeExpanded(boolean isTreeExpanded) {
        this.mIsTreeExpanded = isTreeExpanded;
    }

    public boolean isToggleEnable() {
        return mIsToggleEnable;
    }

    public void setIsToggleEnable(boolean isToggleEnable) {
        this.mIsToggleEnable = isToggleEnable;
    }

    public int getTrunkType() {
        return mTrunkType;
    }

    public void setTrunkType(int trunkType) {
        this.mTrunkType = trunkType;
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        static final int DEFAULT_ITEM_TYPE = 0;                 // 条目类型默认值：NORMAL
        static final int DEFAULT_INDENT_LEVEL = 0;              // 条目缩进级别默认值：0
        static final int CUSTOM_DEFAULT_INT = -1;               // 自定义默认值都设置为 -1
        static final float CUSTOM_DEFAULT_FLOAT = -1;           // 自定义默认值都设置为 -1
        static final int CUSTOM_DEFAULT_COLOR = -1;             // 自定义默认颜色值都设置为 -1，方便检测是否赋值
        static final int CUSTOM_DEFAULT_RES_ID = 0;             // 自定义默认资源 ID 都设置为 0
        static final int BRANCH_TARGET_INDEX = 0;               // 默认横枝指向的子 View 中的第一孩子

        public int customBranchWidth;                           // 自定义横枝宽度 <= 默认横枝宽度
        public int customBranchHeight;                          // 自定义横枝高度
        public int customBranchColor;                           // 自定义横枝颜色
        public int customLeafSrc;                               // 自定义叶子 Src
        public Drawable customLeafDrawable;                     // 自定义叶子 Drawable
        public int customLeafWidth;                             // 自定义叶子宽度
        public int customLeafHeight;                            // 自定义叶子高度
        public int customNodeSrc;                               // 自定义枝节点 Src
        public Drawable customNodeDrawable;                     // 自定义枝节点 Drawable
        public int customNodeWidth;                             // 自定义枝节点宽度
        public int customNodeHeight;                            // 自定义枝节点高度
        public int indentLevel;                                 // 缩进级别
        public int branchTargetIndex;                           // 是否是横枝指向的目标 View
        public int offsetTargetTop;                             // 横枝距离指向目标 View 顶部的偏移值
        public int itemType;                                    // 条目类型

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TreeLayout_Layout, 0, R.style.TreeLayoutItemDefaultStyle);
            if (typedArray != null) {
                customBranchWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_Layout_customBranchWidth, CUSTOM_DEFAULT_INT);
                customBranchHeight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_Layout_customBranchHeight, CUSTOM_DEFAULT_INT);
                customBranchColor = typedArray.getColor(R.styleable.TreeLayout_Layout_customBranchColor, CUSTOM_DEFAULT_COLOR);
                customLeafSrc = typedArray.getResourceId(R.styleable.TreeLayout_Layout_customLeafSrc, CUSTOM_DEFAULT_RES_ID);
                if (customLeafSrc != 0) {
                    customLeafDrawable = ContextCompat.getDrawable(context, customLeafSrc);
                }
                customLeafWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_Layout_customLeafWidth, CUSTOM_DEFAULT_INT);
                customLeafHeight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_Layout_customLeafHeight, CUSTOM_DEFAULT_INT);
                customNodeSrc = typedArray.getResourceId(R.styleable.TreeLayout_Layout_customNodeSrc, CUSTOM_DEFAULT_RES_ID);
                if (customNodeSrc != 0) {
                    customNodeDrawable = ContextCompat.getDrawable(context, customNodeSrc);
                }
                customNodeWidth = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_Layout_customNodeWidth, CUSTOM_DEFAULT_INT);
                customNodeHeight = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_Layout_customNodeHeight, CUSTOM_DEFAULT_INT);
                indentLevel = typedArray.getInt(R.styleable.TreeLayout_Layout_indentLevel, DEFAULT_INDENT_LEVEL);
                branchTargetIndex = typedArray.getInt(R.styleable.TreeLayout_Layout_branchTargetIndex, BRANCH_TARGET_INDEX);
                offsetTargetTop = typedArray.getDimensionPixelOffset(R.styleable.TreeLayout_Layout_offsetTargetTop, CUSTOM_DEFAULT_INT);
                itemType = typedArray.getInt(R.styleable.TreeLayout_Layout_itemType, DEFAULT_ITEM_TYPE);
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
