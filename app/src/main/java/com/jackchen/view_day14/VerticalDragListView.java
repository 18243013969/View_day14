package com.jackchen.view_day14;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * Email 2185134304@qq.com
 * Created by JackChen on 2018/2/10.
 * Version 1.0
 * Description:
 */
public class VerticalDragListView extends FrameLayout {

    //可以认为这个是系统给我们写好的工具类
    private ViewDragHelper mDragHelper ;


    //这个View的目的就是为了兼容 ListView和RecyclerView
    //VerticalDragListView中的第一个子View -> "前面"
    private View mDragListView ;

    //"后面" 菜单的高度
    private int mMenuHeight ;

    //判断菜单是否打开
    private boolean mMenuIsOpen = false ;

    //记录手指按下的位置
    private float mDownY;


    public VerticalDragListView(Context context) {
        this(context, null);
    }

    public VerticalDragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalDragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragHelper = ViewDragHelper.create(this , mDragHelperCallback) ;
    }


    //只要是在onMeasure()方法之后，super.onMeasure()之后去获取高度都是可以的，我们也可以在onLayout()方法中获取高度
    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View menuView = getChildAt(0);
        mMenuHeight = menuView.getMeasuredHeight() ;
    }*/


    /*onMeasure()在什么情况下会调用多次？
    当在调用requestLayout()、addView()、setVisibility()方法时候会测量多次
    因为调用这几个方法都会去重新指定宽高*/

    /*@Override
    public void requestLayout() {
        super.requestLayout();
    }*/

    /*@Override
    public void addView(View child) {
        super.addView(child);
    }*/

    /*@Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }*/

    /*onSizeChanged() 在什么时候调用*/
    /*@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }*/

    /**
     * 在调用onMeasure()测量、onLayout()摆放之后 来获取  "后面" 子View的高度
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 表示：如果布局改变了，都会去重新测量，然后摆放，就会获取菜单高度
        if (changed){
            View menuView = getChildAt(0);  //获取后面菜单的View，即就是 "第0个" 子view
            mMenuHeight = menuView.getMeasuredHeight() ;
        }

    }

    /**
     * 在setContentView()之后调用，即就是在解析XML文件之后调用，
     * 在解析XML文件之后，然后获取VerticalDragListView中的子View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //获取VerticalDragListView中的子View
        int childCount = getChildCount();
        if (childCount !=2){ //抛运行时异常
            throw new RuntimeException("VerticalDragListView只能包含2个子View !") ;
        }
        mDragListView = getChildAt(1) ;  //即就是 "前面" ，第一个子View
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把onTouchEvent触摸事件交给mDragHelper来处理
        mDragHelper.processTouchEvent(event);
        return true;
    }




    //1. 拖动我们的子View，也就是VerticalDragListView里边包裹的子布局
    private ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //指定该子View是否可以拖动,就是child。 返回true的目的就是我让VerticalDragListView里边包裹的所有子View都返回true
            //1.1  只能前面的控件拖动，后面的控件不能拖动
            //只能是让  "前面"  控件拖动 ，而不让 "后面" 控件拖动
            return mDragListView == child;
        }


        //使用场景：卡片布局
        //1.3  垂直拖动的范围只能是后面的View的高度
        //每次垂直拖动，移动的位置
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top <= 0){
                top = 0 ;
            }

            if (top >= mMenuHeight){  //"后面" 菜单的高度
                top = mMenuHeight ;
            }
            return top;

        }


//        //1.2  列表只能垂直拖动，只要把水平的拖动方法注释即可
//        //左右拖动，移动的位置
//        @Override
//        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            return left;
//        }


        //1.4  手指松开的时候两者选其一，要么打开要么关闭
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            if (releasedChild == mDragListView){
            //刚进来初始化时候是关闭的：
                //手指拖动高度如果大于mMenuHeight/2，则让打开；
                //手指拖动高度如果小于mMenuHeight/2，则让关闭；

            //xvel、yvel 代表手指松开的位置
            //水平距离xvel不需要判断，只需要判断垂直距离yvel即可
            if (mDragListView.getTop() > mMenuHeight/2){
                // 滚动到菜单的高度 (打开)
                mDragHelper.settleCapturedViewAt(0 , mMenuHeight) ;
                mMenuIsOpen = true ;
            }else{
                // 滚动到0的位置 (关闭)
                mDragHelper.settleCapturedViewAt(0 , 0) ;
                mMenuIsOpen = false ;
            }

            //刷新
            invalidate();

             //因为在源码中的super什么都没写，所以可以注释
//            super.onViewReleased(releasedChild, xvel, yvel);

            }
        }
    } ;



    // because ACTION_DOWN was not received for this pointer before ACTION_MOVE
    //调用方法
    // VDLV.onInterceptTouchEvent()中的DOWN -> LV.onTouchEvent() ->
    // VDLV.onInterceptTouchEvent()中的MOVE -> VDLV.onTouchEvent()中的MOVE
    //此时返回true，表示拦截，拦截意思就是不要去处理子view的onTouchEvent(),只去处理父类自己的onTouchEvent()，
    // 也就是说listview的LV.onTouch()不会处理



    //现象是：添加ListView列表之后，listview列表可以滑动，但是后面固定的菜单没有效果了
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        //只要当菜单打开时候，就全部拦截，不要让listview响应任何触摸事件
        if (mMenuIsOpen){
            return true ;
        }


        //返回super -> 默认情况是所有的都没有拦截
        //向下滑动listview时候，要拦截，不要给listview做处理
        //谁拦截谁 -> 父View拦截子View，但是子View可以调用 requestDisallowInterceptTouchEvent()方法
        //表示请求父类不要拦截子View，在源码中其实就是 改变mGroupFlags 的值

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();

                //让 DragHelper拿一个完整的事件
                mDragHelper.processTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                 float moveY = ev.getY() ;
                 if ((moveY - mDownY)>0 && !canChildScrollUp()){
                     //向下移动的距离大于手指按下的距离
                     //向下滑动 && 滚动到顶部 -> 不让ListView做处理
                     return true ; //return true表示拦截事件
                 }
                 break;
        }

        return super.onInterceptTouchEvent(ev);
    }


    /**
     * @return Whether it is possible for the child view of this layout to
     *         scroll up. Override this if the child view is a custom view.
     *
     *  可以判断ListView、RecyclerView、ScrollView
     *         这个方法是SwipeRefreshLayout中的源码：用于判断该View是否滚动到了最顶部，还能不能向上滚动
     */
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mDragListView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mDragListView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mDragListView, -1) || mDragListView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mDragListView, -1);
        }
    }

    /**
     * 响应滚动
     * 这个是直接复制网上的，目的是你拖动下边的View的时候 可以按照你自己拖动的高度自动滚动
     *      手指拖动高度如果大于mMenuHeight/2，则让打开；
     *      手指拖动高度如果小于mMenuHeight/2，则让关闭；
     */
    @Override
    public void computeScroll() {
//        super.computeScroll();
        if (mDragHelper.continueSettling(true)){
            invalidate();
        }
    }
}
