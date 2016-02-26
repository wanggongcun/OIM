package com.time.oim.view;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.time.oim.R;
import com.time.oim.util.DatetimeUtil;

public class RefreshListView extends ListView implements OnScrollListener{
	
	private float mDownY;
    private float mMoveY;

    private int mHeaderHeight;

    private int mCurrentScrollState;

    private final static int NONE_PULL_REFRESH = 0;    //正常状态
    private final static int ENTER_PULL_REFRESH = 1;   //进入下拉刷新状态
    private final static int OVER_PULL_REFRESH = 2;    //进入松手刷新状态
    private final static int EXIT_PULL_REFRESH = 3;    //松手后反弹和加载状态
    private int mPullRefreshState = 0;                 //记录刷新状态

    private final static int REFRESH_BACKING = 0;      //反弹中
    private final static int REFRESH_BACED = 1;        //达到刷新界限，反弹结束后
    private final static int REFRESH_RETURN = 2;       //没有达到刷新界限，返回
    private final static int REFRESH_DONE = 3;         //加载数据结束

    private LinearLayout mHeaderLinearLayout = null;
    private LinearLayout mFooterLinearLayout = null;
    private TextView mHeaderTextView = null;
    private TextView mHeaderUpdateText = null;
    private ProgressBar mHeaderProgressBar = null;

    private Object mRefreshObject = null;
    private RefreshListener mRefreshListener = null;
    
    private boolean isrefreshable = false;
    
    public void setOnRefreshListener(RefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    void init(final Context context) {
    	
        mHeaderLinearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_list_header, null);
        measureView(mHeaderLinearLayout);
        mHeaderHeight = mHeaderLinearLayout.getMeasuredHeight();
        
        mHeaderTextView = (TextView) mHeaderLinearLayout.findViewById(R.id.refresh_list_header_text);
        mHeaderUpdateText = (TextView) mHeaderLinearLayout.findViewById(R.id.refresh_list_header_last_update);
        mHeaderProgressBar = (ProgressBar) mHeaderLinearLayout.findViewById(R.id.refresh_list_header_progressbar);
        // 设置内边距，正好距离顶部为一个负的整个布局的高度，正好把头部隐藏  
        mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getLeft(), -mHeaderHeight, mHeaderLinearLayout.getRight(), 0);    
        addHeaderView(mHeaderLinearLayout);
        mFooterLinearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_list_footer, null);
        addFooterView(mFooterLinearLayout);
        
        setSelection(1);
        setOnScrollListener(this);
                mHeaderUpdateText.setText(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
        
    }

    @Override
	public void setEmptyView(View emptyView) {
		// TODO Auto-generated method stub
		super.setEmptyView(emptyView);
		this.findViewById(R.layout.refresh_list_empty);
	}

	@Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveY = ev.getY();
                if(isrefreshable){
                	 if (mHeaderLinearLayout.getBottom() >= 0 && mHeaderLinearLayout.getBottom() < mHeaderHeight) {
                         //进入且仅进入下拉刷新状态
                         if (mPullRefreshState == NONE_PULL_REFRESH) {
                             mPullRefreshState = ENTER_PULL_REFRESH;
                             mDownY = mMoveY; 
                             mHeaderTextView.setText("下拉刷新");
                         }
                     } else if (mHeaderLinearLayout.getBottom() >= mHeaderHeight) {
                         //下拉达到界限，进入松手刷新状态
                         if (mPullRefreshState == ENTER_PULL_REFRESH ) {
                             mPullRefreshState = OVER_PULL_REFRESH;
                             mDownY = mMoveY; //为下拉1/3折扣效果记录开始位置
                             mHeaderTextView.setText("松手刷新");//显示松手刷新
                         }
                     } else{
                         //不刷新了
                         if (mPullRefreshState == ENTER_PULL_REFRESH) {
                             mPullRefreshState = NONE_PULL_REFRESH;
                         }else if (mPullRefreshState == NONE_PULL_REFRESH) {
                             setSelection(1);
                         }
                     }
                     
                     if (mPullRefreshState == ENTER_PULL_REFRESH) {
                     	mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
                                 (int)((mMoveY - mDownY)) - mHeaderHeight,
                                 mHeaderLinearLayout.getPaddingRight(),
                                 mHeaderLinearLayout.getPaddingBottom());
                     	
                     }else if (mPullRefreshState == OVER_PULL_REFRESH ) {
                     	mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
                                 (int)((mMoveY - mDownY)/3),
                                 mHeaderLinearLayout.getPaddingRight(),
                                 mHeaderLinearLayout.getPaddingBottom());
                     
                     }
                }

               
                break;
            case MotionEvent.ACTION_UP:
                //when you action up, it will do these:
                //1. roll back util header topPadding is 0
                //2. hide the header by setSelection(0)
                if (mPullRefreshState == OVER_PULL_REFRESH || mPullRefreshState == ENTER_PULL_REFRESH) {
                    new Thread() {
                        public void run() {
                            Message msg;
                            while(mHeaderLinearLayout.getPaddingTop() > 1) {
                                msg = mHandler.obtainMessage();
                                msg.what = REFRESH_BACKING;
                                mHandler.sendMessage(msg);
                                try {
                                    sleep(5);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            msg = mHandler.obtainMessage();
                            if (mPullRefreshState == OVER_PULL_REFRESH) {
                                msg.what = REFRESH_BACED;
                            } else {
                                msg.what = REFRESH_RETURN;
                            }
                            mHandler.sendMessage(msg);
                        };
                    }.start();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    	if(firstVisibleItem == 0){
    		isrefreshable = true;
    	}else{
    		isrefreshable = false;
    		mPullRefreshState = NONE_PULL_REFRESH;
    	}
    	
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        setSelection(1);
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case REFRESH_BACKING:
                mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
                        (int) (mHeaderLinearLayout.getPaddingTop()*0.75f),
                        mHeaderLinearLayout.getPaddingRight(),
                        mHeaderLinearLayout.getPaddingBottom());
                break;
            case REFRESH_BACED:
                mHeaderTextView.setText("正在加载...");
                mHeaderProgressBar.setVisibility(View.VISIBLE);
                mPullRefreshState = EXIT_PULL_REFRESH;
                new Thread() {
                    public void run() {
                    	
                    	try {
							sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        if (mRefreshListener != null) {
                            mRefreshObject = mRefreshListener.refreshing();
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = REFRESH_DONE;
                        mHandler.sendMessage(msg);
                    };
                }.start();
                break;
            case REFRESH_RETURN:
                mHeaderTextView.setText("下拉刷新");
                mHeaderProgressBar.setVisibility(View.INVISIBLE);
                mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
                        -mHeaderHeight,
                        mHeaderLinearLayout.getPaddingRight(),
                        0);
                mPullRefreshState = NONE_PULL_REFRESH;
                setSelection(1);
                break;
            case REFRESH_DONE:
                mHeaderTextView.setText("下拉刷新");
                mHeaderProgressBar.setVisibility(View.INVISIBLE);
                mHeaderUpdateText.setText(DatetimeUtil.now_yyyy_MM_dd_HH_mm_ss());
                mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
                        -mHeaderHeight,
                        mHeaderLinearLayout.getPaddingRight(),
                        0);
                mPullRefreshState = NONE_PULL_REFRESH;
                setSelection(1);
                if (mRefreshListener != null) {
                    mRefreshListener.refreshed(mRefreshObject);
                }
                break;
            default:
                break;
            }
        }
    };
    public interface RefreshListener {
        Object refreshing();
        void refreshed(Object obj);
        void more();
    }
    
    public void finishFootView() {
    	
    }

    public void addFootView() {
        if (getFooterViewsCount() == 0) {
            addFooterView(mFooterLinearLayout);
        }
    }

    public void removeFootView() {
        removeFooterView(mFooterLinearLayout);
    }
	
}
