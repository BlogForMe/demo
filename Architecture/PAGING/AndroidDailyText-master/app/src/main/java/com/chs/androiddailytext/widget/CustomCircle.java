package com.chs.androiddailytext.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.chs.androiddailytext.R;

/**
 * Created by yangjie on 2017/9/23.
 */

public class CustomCircle extends ViewGroup {
    private View mHeaderView = null;
    private View mFooterView = null;
    private Scroller mScroller = null;
    private TextView tv_refresh_header, tv_refresh_footer;
    private ProgressBar probar_load_header, probar_load_footer;
    private LinearLayoutManager mLayoutManager = null;
    private int mCount;
    private RecyclerView mRecyclerView;
    private int screenWith;
    private int screenHeight;

    public CustomCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mHeaderView = View.inflate(context, R.layout.header_layout, null);
        mFooterView = View.inflate(context, R.layout.footer_layout, null);
        mScroller = new Scroller(context);
        screenWith = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mHeaderView.setLayoutParams(params);
        mFooterView.setLayoutParams(params);
        addView(mFooterView);
        addView(mHeaderView);
        tv_refresh_footer = (TextView) mFooterView.findViewById(R.id.tv_refresh);
        tv_refresh_header = (TextView) mHeaderView.findViewById(R.id.tv_refresh);
        probar_load_footer = (ProgressBar) mFooterView.findViewById(R.id.proBar_refresh);
        probar_load_header = (ProgressBar) mHeaderView.findViewById(R.id.proBar_refresh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view == mHeaderView) {
                view.layout(0, -view.getMeasuredHeight(), view.getMeasuredWidth(), 0);
                break;
            } else if (view == mFooterView) {
                view.layout(0, view.getMeasuredHeight(), view.getMeasuredWidth(), view.getMeasuredHeight() * 2);
            } else {
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                mRecyclerView = (RecyclerView) view;
            }
        }
    }

    private float mStartY;

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mCount = mRecyclerView.getAdapter().getItemCount();
        mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int position = mLayoutManager.findFirstCompletelyVisibleItemPosition();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int y = Math.round(ev.getY() - mStartY);
                if (position == 0 && y > 0) {
                    return true;
                } else if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mCount - 1 && y < 0) {
                    return true;
                }
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScroller.computeScrollOffset()) {
                    int scollDis = Math.round(event.getY() - mStartY);
                    int scrollY = getScrollY();
                    if (scollDis != 0) {
                        /*if (y > 0) { //????????????
                            y = -y;
                        } else {//????????????
                            y = Math.abs(y);

                        }*/
                        scollDis = scollDis> 0 ? -scollDis: Math.abs(scollDis);
                        textUpdate(scrollY);
                        mScroller.startScroll(0, scrollY, 0, scollDis, 0);
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                textUpdate(getScrollY());
                //??????
                reBound();
                break;
        }
        mStartY = event.getY();
        return true;
    }

    //????????????
    private void reBound() {
        int scrollY = getScrollY();
        int scrollDis; //????????????
        int absY = Math.abs(scrollY);
        if (scrollY != 0) {
            //??????????????????200
            if (absY < 200) { //?????????:???????????????????????????
                scrollDis = scrollY > 0 ? 0 - scrollY : absY;
                mScroller.startScroll(0, scrollY, 0, scrollDis, 1000);
                invalidate();
                return;
            } else { //??????200??????????????????
                scrollDis = scrollY > 0 ? 0 - (scrollY - 100) : absY - 100;
                mScroller.startScroll(0, scrollY, 0, scrollDis, 1000);
                invalidate();
                // ??????/????????????
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int y = getScrollY();
                        if (y > 0) { //??????
                            mCallBack.upRefresh(mScroller, y);
                        } else { //??????
                            mCallBack.downLoad(mScroller, y);
                        }
                    }
                }, 2000);
            }

        }
    }

    //????????????
    private void textUpdate(int scrollY) {

        if (scrollY < 0) {//??????Header??????text
            scrollY = Math.abs(scrollY);
            if (scrollY > 200 && !tv_refresh_header.getText().toString().equals("????????????")) {
                tv_refresh_header.setText("????????????");
            } else if (scrollY < 200 && !tv_refresh_header.getText().toString().equals("????????????")) {
                tv_refresh_header.setText("????????????");
            }
        } else { //??????Footer??????text
            if (scrollY > 200 && !tv_refresh_header.getText().toString().equals("??????????????????")) {
                tv_refresh_footer.setText("??????????????????");
            } else if (scrollY < 200 && !tv_refresh_header.getText().toString().equals("??????????????????")) {
                tv_refresh_footer.setText("??????????????????");
            }
        }
    }

    public interface RefreshCallBack {
        void upRefresh(Scroller scroller, int y);

        void downLoad(Scroller scroller, int y);
    }

    private RefreshCallBack mCallBack = null;

    public void setRefreshCallBack(RefreshCallBack callBack) {
        if (callBack != null) {
            this.mCallBack = callBack;
        }
    }
}
