package com.ninetowns.ui.fragment;

import com.ninetowns.library.helper.ConstantsHelper;
import com.ninetowns.library.parser.AbsParser;
import com.ninetowns.ui.R;
import com.ninetowns.ui.widget.dialog.ProgressiveDialog;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase.Mode;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase.OnRefreshListener2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 
 * @Title: PageListFragment.java
 * @Description: 上拉刷新下拉刷新的组件
 * @author wuyulong
 * @date 2015-1-8 上午10:40:04
 * @version V1.0<Result extends Object,Par extends AbsParser>
 */
public abstract class PageListFragment<T extends View, Result extends Object, Par extends AbsParser>
        extends BaseFragment<Object, AbsParser> implements
        OnRefreshListener2<T> {
    private ProgressiveDialog progressDialog;
    private FragmentManager mFragmentManager;
    private PullToRefreshBase<T> mPullToRefreshWidget;
    protected static final int INVALID_FRAGMENT_ID = -1;
    private static final String SAVE_INSTANCE_STATE_CHILD_FRAGMENT_TAG = "child_fragment_tag";
    public int currentpage = 1;// 默认是1z
    private String mCurrentChildFragmentTag;
    private Fragment mCurrentChildFragment;

    private int mCurrentChildFragmentId = INVALID_FRAGMENT_ID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = onCreateFragmentView(inflater, container,
                savedInstanceState);
        mPullToRefreshWidget = initRefreshIdView();
        return view;
    }

    @Override
    public void getParserResult(Object parserResult) {
        @SuppressWarnings("unchecked")
        Result result = (Result) parserResult;
        getPageListParserResult(result);
    }
	
/**
 * 
* @Title: PageListFragment.java  
* @Description: 获取网络返回的数据 
* @author wuyulong
* @date 2015-1-21 下午2:59:57  
* @param 
* @return void
 */
    public abstract void getPageListParserResult(Result parserResult);

    protected abstract PullToRefreshBase<T> initRefreshIdView();

    protected abstract View onCreateFragmentView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mFragmentManager = getChildFragmentManager();
        // 注册刷新
        mPullToRefreshWidget.setOnRefreshListener(this);
        if (savedInstanceState != null) {
            String childFragmentTag = savedInstanceState
                    .getString(SAVE_INSTANCE_STATE_CHILD_FRAGMENT_TAG);
            if (!TextUtils.isEmpty(childFragmentTag)) {
                mCurrentChildFragmentTag = childFragmentTag;
                mCurrentChildFragment = mFragmentManager
                        .findFragmentByTag(mCurrentChildFragmentTag);
            }
        }
        if (mCurrentChildFragment == null) {
            initChildFragment();
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVE_INSTANCE_STATE_CHILD_FRAGMENT_TAG,
                mCurrentChildFragmentTag);
        super.onSaveInstanceState(outState);
    }

    @Override
    // 下拉的时候
    public void onPullDownToRefresh(PullToRefreshBase<T> refreshView) {

        mPullToRefreshWidget.setLastUpdatedLabel(DateUtils.formatDateTime(
                getActivity(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL));
        // initChildFragment();//如果要刷新的话
        currentpage = 1;
        super.loadData(getApiParmars());

    }

    @Override
    // 上拉的时候
    public void onPullUpToRefresh(PullToRefreshBase<T> refreshView) {
        mPullToRefreshWidget.setLastUpdatedLabel(DateUtils.formatDateTime(
                getActivity().getApplicationContext(),
                System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL));

        int totalpage = getTotalPage();
        if (totalpage > 1) {// 如果默认是一条
            // 大于一有值
            if (this.currentpage < totalpage) {
                this.currentpage++;
                super.loadData(getApiParmars());
            } else {
                loadingRefreshEnd();
                Toast.makeText(getActivity(), "没有更多数据", 0).show();
            }
        } else if (totalpage == 0) {
            loadingRefreshEnd();
            // 没有没有数据，
            Toast.makeText(getActivity(), "没有更多数据", 0).show();
        } else if (totalpage == 1) {
            ConstantsHelper.CURRENTPAGE = 1;
            loadingRefreshEnd();
            // 只有一页
            Toast.makeText(getActivity(), "没有更多数据", 0).show();
        }

    }

    /**
     * 
     * @Title: BaseListFragment.java
     * @Description: 调用onActivityCreate()方法，创建第一个碎片
     * @author wuyulong
     * @date 2014-7-30 上午10:28:33
     * @param
     * @return void
     */
    protected void initChildFragment() {

    }

    /**
     * 
     * @Title: ComponentUtil.java
     * @Description: 显示dialog
     * @author wuyulong
     * @date 2014-7-14 下午4:23:26
     * @param
     * @return void
     */
    @Override
    public void showProgressDialog() {
        if ((!getActivity().isFinishing()) && (progressDialog == null)) {
            progressDialog = new ProgressiveDialog(getActivity());
        }
        if (progressDialog != null) {
            progressDialog.setMessage(R.string.loading);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }

    }

    /**
     * 
     * @Title: ComponentUtil.java
     * @Description: 取消dialog
     * @author wuyulong
     * @date 2014-7-14 下午4:23:48
     * @param
     * @return void
     */
    @Override
    public void closeProgressDialogFragment() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    @Override
    protected void loadingRefreshStart() {
        mPullToRefreshWidget.setMode(Mode.PULL_DOWN_TO_REFRESH);
        mPullToRefreshWidget.setRefreshing();
    }

    @Override
    protected void lodaingRefreshBothStart() {
        mPullToRefreshWidget.setMode(Mode.BOTH);
        mPullToRefreshWidget.setRefreshing();
    }

    @Override
    protected void loadingRefreshEnd() {
        mPullToRefreshWidget.onRefreshComplete();

    }

    protected FragmentTransaction beginChildFragmentTransaction(
            int enterFragmentId, int exitFragmentId) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        return ft;
    }

    public void switchChildFragment(int fragmentId) {
        Class<? extends Fragment> clz = getChildFragmentClass(fragmentId);
        if (clz == null) {
            return;
        }
        mCurrentChildFragmentTag = clz.getName();
        Fragment fragment = mFragmentManager
                .findFragmentByTag(mCurrentChildFragmentTag);
        FragmentTransaction ft = beginChildFragmentTransaction(fragmentId,
                mCurrentChildFragmentId);
        mCurrentChildFragmentId = fragmentId;
        if (mCurrentChildFragment != null) {
            ft.detach(mCurrentChildFragment);
        }
        if (fragment == null) {
            fragment = Fragment.instantiate(getActivity(), clz.getName());
            Bundle args = getChildFragmentArguments(fragmentId);
            fragment.setArguments(args);
            ft.replace(getChildFragmentStubId(), fragment,
                    mCurrentChildFragmentTag);
        } else {
            ft.attach(fragment);
        }
        mCurrentChildFragment = fragment;
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        if (mCurrentChildFragment != null) {
            mFragmentManager.beginTransaction().remove(mCurrentChildFragment)
                    .commitAllowingStateLoss();
            mCurrentChildFragment = null;
        }
        super.onDestroyView();
        currentpage = 1;
    }

    protected Class<? extends Fragment> getChildFragmentClass(int fragmentId) {
        return null;
    }

    protected Bundle getChildFragmentArguments(int fragmentId) {
        return null;
    }

    protected int getChildFragmentStubId() {
        return 0;
    }

    public abstract int getTotalPage();
}
