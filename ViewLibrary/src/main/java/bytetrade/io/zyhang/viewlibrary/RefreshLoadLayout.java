package bytetrade.io.zyhang.viewlibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * Created by zyhang on 2019/5/13
 * <p>
 * Description: 支持RecyclerView 下拉刷新，加载更多 ScrollerView WebView下拉刷新
 */
public class RefreshLoadLayout extends ViewGroup {

    protected Context mContext;

    //最小高度
    private static final int HEADER_DEFAULT_HEIGHT = 100;
    private static final int FOOTER_DEFAULT_HEIGHT = 100;
    private static final int FOOTER_NO_DATA_DEFAULT_HEIGHT = 60;
    //刷新高度
    private static final int REFRESH_HEIGHT = 20;
    private static final int LOAD_MORE_HEIGHT = 20;
    //容器
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private RelativeLayout mEmptyLayout;
    //View
    private View mHeader;
    private View mFooter;
    private View mFooterNoData;
    //高度
    private int mHeaderHeight = HEADER_DEFAULT_HEIGHT;
    private int mFooterHeight = FOOTER_DEFAULT_HEIGHT;

    //滑动的偏移量
    private int mScrollOffset = 0;

    //无状态
    private final int STATE_NOT = -1;
    //上拉状态
    private final int STATE_UP = 1;
    //下拉状态
    private final int STATE_DOWN = 2;
    //当前状态
    private int mCurrentState = STATE_NOT;

    //正在更新状态
    private boolean mIsRefresh = false;
    //正在加载状态
    private boolean mIsLoadMore = false;

    //是否启用下拉刷新功能（默认开启）上拉加载功能（默认不开启）
    private boolean mIsRefreshOpen = true;
    private boolean mIsLoadMoreOpen = false;

    //加载状态
    private boolean mIsLoading = false;

    //默认阻力
    private int mDamp = 2;

    //是否还有更多数据
    private boolean isMore = true;

    private Scroller mScroller;
    //刷新恢复时间 到刷新状态
    private static final int SCROLLER_RESTORE_TIME = 1000;
    private static final int SCROLLER_REFRESH_TIME = 500;
    //到最开始
    private static final int SCROLLER_LOADING_MORE_TIME = 500;


    /***************************构造方法*************************/

    public RefreshLoadLayout(Context context) {
        this(context, null);
    }

    public RefreshLoadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //调整边缘效果，属性的默认值是true
        setClipToPadding(false);
        initHeaderLayout();
        initFooterLayout();
        initEmptyLayout();
        mScroller = new Scroller(mContext);
    }

    /***************************初始化*************************/

    private void initHeaderLayout() {
        mHeaderLayout = new LinearLayout(mContext);
        addView(mHeaderLayout);
    }

    private void initFooterLayout() {
        mFooterLayout = new LinearLayout(mContext);
        addView(mFooterLayout);
    }

    private void initEmptyLayout() {
        mEmptyLayout = new RelativeLayout(mContext);
        addView(mEmptyLayout);
    }


    /***************************RefreshLoadLayout可用方法*************************/

    public void setHeader(View Header) {
        mHeader = Header;
        mHeaderLayout.removeAllViews();
        mHeaderLayout.addView(mHeader);

        //获取头部高度
        mHeaderLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mHeader.getHeight() > FOOTER_DEFAULT_HEIGHT && mHeader.getHeight() != 0) {
                    mHeaderHeight = mHeader.getHeight();
                } else {
                    mHeaderHeight = HEADER_DEFAULT_HEIGHT;
                }

                setLayoutParams(mHeaderLayout, mHeaderHeight);

                //当获取到头部高度的时候，如果正处于下拉刷新状态，应该把头部打开。
                if (mIsRefresh) {
                    scroll(-mHeaderHeight);
                }
                invalidate();
            }
        });
    }

    public void setFooter(View Footer) {
        mFooter = Footer;
        mFooterLayout.removeAllViews();
        mFooterLayout.addView(mFooter);

        //获取尾部高度
        mFooterLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mFooter.getHeight() > FOOTER_DEFAULT_HEIGHT) {
                    mFooterHeight = mFooter.getHeight();
                } else {
                    mFooterHeight = FOOTER_DEFAULT_HEIGHT;
                }

                setLayoutParams(mFooterLayout, mFooterHeight);

                //当获取到尾部高度的时候，如果正处于上拉刷新状态，应该把尾部打开。
                if (mIsLoadMore) {
                    scroll(mFooterHeight);
                }
                invalidate();
            }
        });
    }

    public void setFooterNoData(View FooterNoData) {
        mFooterNoData = FooterNoData;
    }

    public void setEmpty(View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startRefresh();
            }
        });

        mEmptyLayout.removeAllViews();
        mEmptyLayout.addView(view);

        mEmptyLayout.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                mEmptyLayout.setGravity(Gravity.CENTER);
                mEmptyLayout.setLayoutParams(layoutParams);
                invalidate();
            }
        });

    }

    public void setEmptyState(boolean isEmpty) {
        if (getChildCount() > 3) {
            View view = getChildAt(3);
            if (isEmpty) {
                view.setVisibility(GONE);
                mEmptyLayout.setVisibility(VISIBLE);
            } else {
                view.setVisibility(VISIBLE);
                mEmptyLayout.setVisibility(GONE);
            }
        }
    }

    /**
     * 设置是否启用上下拉功能
     *
     * @param isRefresh  是否开启下拉功能 默认开启
     * @param isLoadMore 是否开启上拉功能 默认不开启
     */
    public void setFunctionState(boolean isRefresh, boolean isLoadMore) {
        mIsRefreshOpen = isRefresh;
        mIsLoadMoreOpen = isLoadMore;
    }

    //通知加载完成
    public void refreshFinish() {
        restore();
        if (mIsLoading) {
            mIsLoading = false;
            if (mIsRefresh) {
                mIsRefresh = false;
                if (mHeaderStateListener != null) {
                    mHeaderStateListener.onFinished(mHeader);
                    isMore(true);
                }
            }
        }
    }

    //通知刷新完成
    public void loadMoreFinish() {
        restore();
        if (mIsLoading) {
            mIsLoading = false;
            if (mIsLoadMore) {
                mIsLoadMore = false;
                if (mFooterStateListener != null && isMore) {
                    mFooterStateListener.onFinished(mFooter);
                }
            }
        }
    }

    //设置加载更多有无
    public void isMore(boolean isMore) {
        this.isMore = isMore;
        if (mFooterStateListener != null) {
            if (isMore) {
                setFooter(mFooter);
                mFooterStateListener.onHasMore(mFooter);
            } else {
                setFooterNoData();
                mFooterStateListener.onNotMore(mFooter);
            }
        }
    }


    //设置头部监听器
    public void addOnHeaderStateListener(OnHeaderStateListener listener) {
        mHeaderStateListener = listener;
    }

    //设置尾部监听器
    public void addOnFooterStateListener(OnFooterStateListener listener) {
        mFooterStateListener = listener;
    }

    public void removeOnHeaderStateListener() {
        mHeaderStateListener = null;
    }

    public void removeOnFooterStateListener() {
        mFooterStateListener = null;
    }

    /**
     * 设置拉动阻力 （1到10）
     *
     * @param damp 阻力值
     */
    public void setDamp(int damp) {
        if (damp < 1) {
            mDamp = 1;
        } else if (damp > 10) {
            mDamp = 10;
        } else {
            mDamp = damp;
        }
    }

    /***************************测量与布局*************************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //测量头部高度
        View Header = getChildAt(0);
        measureChild(Header, widthMeasureSpec, heightMeasureSpec);

        //测量尾部高度
        View Footer = getChildAt(1);
        measureChild(Footer, widthMeasureSpec, heightMeasureSpec);

        //测量空高度
        View Empty = getChildAt(2);
        measureChild(Empty, widthMeasureSpec, heightMeasureSpec);

        //测量内容容器宽高
        int count = getChildCount();
        int contentHeight = 0;
        int contentWidth = 0;
        if (count > 3) {
            View content = getChildAt(3);
            measureChild(content, widthMeasureSpec, heightMeasureSpec);
            contentHeight = content.getMeasuredHeight();
            contentWidth = content.getMeasuredWidth();
        }

        //设置宽高
        setMeasuredDimension(measureWidth(widthMeasureSpec, contentWidth), measureHeight(heightMeasureSpec, contentHeight));
    }

    private int measureWidth(int measureSpec, int contentWidth) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = contentWidth + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec, int contentHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = contentHeight + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //布局头部
        View Header = getChildAt(0);
        Header.layout(getPaddingLeft(), -mHeaderHeight, getPaddingLeft() + Header.getMeasuredWidth(), 0);

        //布局尾部
        View Footer = getChildAt(1);
        Footer.layout(getPaddingLeft(), getMeasuredHeight(), getPaddingLeft() + Footer.getMeasuredWidth(), getMeasuredHeight() + mFooterHeight);

        //布局中间
        View Empty = getChildAt(2);
        Empty.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + Empty.getMeasuredWidth(), getPaddingTop() + Empty.getMeasuredHeight());


        //布局内容容器
        int count = getChildCount();
        if (count > 3) {
            View content = getChildAt(3);
            content.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + content.getMeasuredWidth(), getPaddingTop() + content.getMeasuredHeight());
        }
    }

    /***************************私有方法*************************/


    private void setLayoutParams(LinearLayout layout, int height) {
        LayoutParams layoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setGravity(Gravity.CENTER);
        layoutParams.height = height;
        layout.setLayoutParams(layoutParams);
    }

    private void setFooterNoData() {
        if (mFooterNoData == null) {
            mFooterNoData = LayoutInflater.from(mContext).inflate(R.layout.layout_no_data, mFooterLayout);
        } else {
            mFooterLayout.removeAllViews();
            mFooterLayout.addView(mFooterNoData);
        }
        //获取尾部高度
        mFooterLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mFooter.getHeight() > FOOTER_NO_DATA_DEFAULT_HEIGHT) {
                    mFooterHeight = mFooter.getHeight();
                } else {
                    mFooterHeight = FOOTER_NO_DATA_DEFAULT_HEIGHT;
                }

                setLayoutParams(mFooterLayout, mFooterHeight);

                //当获取到尾部高度的时候，如果正处于上拉刷新状态，应该把尾部打开。
                if (mIsLoadMore) {
                    scroll(mFooterHeight);
                }
                invalidate();
            }
        });

    }

    /**
     * 触发下拉刷新
     */
    private void startRefresh() {

        if (!mIsRefreshOpen) {
            return;
        }

        if (!mIsLoading) {
            mIsLoading = true;
            mIsRefresh = true;
            mCurrentState = STATE_NOT;
            this.smoothScrollTo(-mHeaderHeight, SCROLLER_REFRESH_TIME);
            if (mHeaderStateListener != null) {
                mHeaderStateListener.onRefresh(mHeader);
            }
        }
    }

    /**
     * 触发上拉加载
     */
    private void startLoadMore() {

        if (!mIsLoadMoreOpen) {
            return;
        }

        if (!isMore) {
            fastRestore();
            return;
        }

        if (!mIsLoading) {
            mIsLoading = true;
            mIsLoadMore = true;
            mCurrentState = STATE_NOT;
            this.smoothScrollTo(mFooterHeight, SCROLLER_LOADING_MORE_TIME);
            if (mFooterStateListener != null && isMore) {
                mFooterStateListener.onLoadMore(mFooter);
            }
        } else {
            refreshFinish();
        }
    }

    /***************************滑动处理*************************/

    //直接移动
    private void scroll(int offset) {

        if (offset < 0 && !mIsRefreshOpen) {
            return;
        }

        if (offset > 0 && !mIsLoadMoreOpen) {
            return;
        }

        scrollTo(0, offset);
        mScrollOffset = Math.abs(offset);

        if (mCurrentState == STATE_DOWN && mHeaderStateListener != null) {
            mHeaderStateListener.onScrollChange(mHeader, mScrollOffset, mScrollOffset >= mHeaderHeight ? 100 : mScrollOffset * 100 / mHeaderHeight);
        }

        if (mCurrentState == STATE_UP && mFooterStateListener != null && isMore) {
            mFooterStateListener.onScrollChange(mFooter, mScrollOffset, mScrollOffset >= mFooterHeight ? 100 : mScrollOffset * 100 / mFooterHeight);
        }
    }

    //缓慢移动还原
    private void restore() {
        mCurrentState = STATE_NOT;
        this.smoothScrollTo(0, SCROLLER_RESTORE_TIME);
    }

    private void fastRestore() {
        mCurrentState = STATE_NOT;
        this.smoothScrollTo(0, 500);
    }

    public void smoothScrollTo(int destY, int duration) {
        int scrollY = getScrollY();
        int delta = destY - scrollY;
        mScroller.startScroll(0, scrollY, 0, delta, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /***************************监听事件处理*************************/

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mY > y) {
                    if (mCurrentState == STATE_UP) {
                        scroll((mY - y) / mDamp);
                    }
                } else if (mCurrentState == STATE_DOWN) {
                    scroll((mY - y) / mDamp);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsRefresh && !mIsLoadMore) {
                    if (mCurrentState == STATE_DOWN) {
                        if (mScrollOffset < mHeaderHeight) {
                            fastRestore();
                        } else {
                            startRefresh();
                        }
                    } else if (mCurrentState == STATE_UP) {
                        if (mScrollOffset < mFooterHeight) {
                            fastRestore();
                        } else {
                            startLoadMore();
                        }
                    } else {
                        fastRestore();
                    }
                }
                mY = 0;

                break;
            default:

                break;
        }
        return super.onTouchEvent(event);
    }

    int mY = 0;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mY = (int) ev.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                if (mIsLoading) {
                    return false;
                }

                if (Refresh() && y - mY > REFRESH_HEIGHT) {
                    mCurrentState = STATE_DOWN;
                    return true;
                }

                if (LoadMore() && mY - y > LOAD_MORE_HEIGHT) {
                    mCurrentState = STATE_UP;
                    return true;
                }

                return false;
            case MotionEvent.ACTION_UP:

                return false;
        }

        return false;
    }

    protected boolean Refresh() {
        return mCurrentState != STATE_UP && mIsRefreshOpen && isTop();
    }

    protected boolean LoadMore() {
        return mCurrentState != STATE_DOWN && mIsLoadMoreOpen && isBottom();
    }

    protected boolean isTop() {

        if (getChildCount() < 3) {
            return true;
        }

        View view = getChildAt(3);

        if (view instanceof ViewGroup) {

            if (view instanceof ScrollView) {
                ScrollView scrollView = (ScrollView) view;
                return scrollView.getScrollY() <= 0;
            } else if (view instanceof WebView) {
                WebView webView = (WebView) view;
                return webView.getScrollY() <= 0;
            } else {
                return isChildTop((ViewGroup) view);
            }
        } else {
            return true;
        }
    }


    protected boolean isChildTop(ViewGroup viewGroup) {
        int minY = 0;
        int realCount;
        int firstVisibleItem;

        if (viewGroup instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) viewGroup;
            if (recyclerView.getAdapter() != null) {
                realCount = recyclerView.getAdapter().getItemCount();
                if (realCount == 0) {
                    return false;
                }
                if (recyclerView.getLayoutManager() != null) {
                    if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        firstVisibleItem = manager.findFirstCompletelyVisibleItemPosition();
                    } else {
                        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                        firstVisibleItem = manager.findFirstCompletelyVisibleItemPosition();
                    }
                    return firstVisibleItem == 0;
                }
                return false;

            }
            return false;
        } else {
            realCount = viewGroup.getChildCount();

            if (realCount == 0) {
                return false;
            }

            for (int i = 0; i < realCount; i++) {
                View view = viewGroup.getChildAt(i);
                int topMargin = 0;
                LayoutParams lp = view.getLayoutParams();
                if (lp instanceof MarginLayoutParams) {
                    topMargin = ((MarginLayoutParams) lp).topMargin;
                }
                int top = view.getTop() - topMargin;
                minY = Math.min(minY, top);
            }
            return minY >= 0;
        }
    }

    protected boolean isBottom() {

        if (getChildCount() < 3) {
            return false;
        }

        View view = getChildAt(3);

        if (view instanceof ViewGroup) {
            if (view instanceof ScrollView) {
                ScrollView scrollView = (ScrollView) view;
                if (scrollView.getChildCount() > 0) {
                    return scrollView.getScrollY() >= scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                } else {
                    return true;
                }
            } else {
                return isChildBottom((ViewGroup) view);
            }
        } else {
            return true;
        }
    }


    protected boolean isChildBottom(ViewGroup viewGroup) {
        int maxY = 0;
        int realCount;
        int lastVisibleItem;
        if (viewGroup instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) viewGroup;
            if (recyclerView.getAdapter() != null) {
                realCount = recyclerView.getAdapter().getItemCount();
                if (realCount == 0) {
                    return false;
                }
                if (recyclerView.getLayoutManager() != null) {
                    if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        lastVisibleItem = manager.findLastCompletelyVisibleItemPosition() + 1;
                    } else {
                        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                        lastVisibleItem = manager.findLastCompletelyVisibleItemPosition() + 1;
                    }
                    return lastVisibleItem == realCount;
                }
                return false;

            }
            return false;
        } else {
            realCount = viewGroup.getChildCount();

            if (realCount == 0) {
                return false;
            }

            for (int i = 0; i < realCount; i++) {
                View view = viewGroup.getChildAt(i);
                int bottomMargin = 0;
                LayoutParams lp = view.getLayoutParams();
                if (lp instanceof MarginLayoutParams) {
                    bottomMargin = ((MarginLayoutParams) lp).bottomMargin;
                }
                int bottom = view.getBottom() + bottomMargin;
                maxY = Math.max(maxY, bottom);
            }

            int h = viewGroup.getMeasuredHeight() - viewGroup.getPaddingBottom();

            return maxY <= h;
        }
    }

    /****************************接口*************************/


    /**
     * 头部状态监听器
     */
    private OnHeaderStateListener mHeaderStateListener;

    /**
     * 尾部状态监听器
     */
    private OnFooterStateListener mFooterStateListener;

    /**
     * 头部状态监听器
     */
    public interface OnHeaderStateListener {

        /**
         * 头部滑动变化
         *
         * @param Header       头部View
         * @param scrollOffset 滑动距离
         * @param scrollRatio  从开始到触发阀值的滑动比率（0到100）如果滑动到达了阀值，就算在滑动，这个值也是100
         */
        void onScrollChange(View Header, int scrollOffset, int scrollRatio);

        //刷新状态
        void onRefresh(View Header);

        //刷新完成
        void onFinished(View Header);

    }

    /**
     * 头部状态监听器
     */
    public interface OnFooterStateListener {

        /**
         * 尾部滑动变化
         *
         * @param Footer       尾部View
         * @param scrollOffset 滑动距离
         * @param scrollRatio  从开始到触发阀值的滑动比率（0到100）如果滑动到达了阀值，就算在滑动，这个值也是100
         */
        void onScrollChange(View Footer, int scrollOffset, int scrollRatio);

        //加载状态
        void onLoadMore(View Footer);

        //加载完成
        void onFinished(View Footer);

        //没有更多
        void onNotMore(View Footer);

        //有更多
        void onHasMore(View Footer);
    }

}
