package com.mouqu.zhailu.zhailu.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mouqu.zhailu.zhailu.R;
import com.mouqu.zhailu.zhailu.base.BaseFragment;
import com.mouqu.zhailu.zhailu.bean.AllOrderBean;
import com.mouqu.zhailu.zhailu.contract.fragment.OngingOrderContract;
import com.mouqu.zhailu.zhailu.modle.fragment.OngoingOrderModel;
import com.mouqu.zhailu.zhailu.presenter.fragment.OngoingOrderPresenter;
import com.mouqu.zhailu.zhailu.ui.adapter.OngoingOrderRecyclerviewAdapter;
import com.mouqu.zhailu.zhailu.util.GetSPData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;

public class OngoingOrderFragment extends BaseFragment<OngoingOrderPresenter, OngoingOrderModel> implements OngingOrderContract.View, View.OnClickListener {
    @BindView(R.id.order_recycler)
    RecyclerView orderRecycler;
    @BindView(R.id.ll_no_order)
    LinearLayout llNoOrder;
    Unbinder unbinder;
    @BindView(R.id.all_order_swiperefreshlayout)
    SwipeRefreshLayout allOrderSwiperefreshlayout;
    private int mCurrentCounter;
    private int page = 1;
    //刷新标志
    boolean isErr = true;
    private String spUserID;
    private List countBean=null;
    private OngoingOrderRecyclerviewAdapter ongoingOrderRecyclerviewAdapter;


    @Override
    protected void initViewAndEvents() {
        mCurrentCounter = 0;
        page = 0;
        countBean = new ArrayList<AllOrderBean.TasksBean>();
        spUserID = new GetSPData().getSPUserID(getActivity());
        mMvpPresenter.getProgressIndent(spUserID, "3", mMultipleStateView);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_order;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void setUpData() {

    }


    @Override
    public void onClick(View v) {

    }


    private void initSwipeRefresh() {
        //设置下拉刷新
        allOrderSwiperefreshlayout.setColorSchemeResources(R.color.blue);
            allOrderSwiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    orderRecycler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (countBean.size()!=0) {
                                ongoingOrderRecyclerviewAdapter.notifyDataSetChanged();
                                ongoingOrderRecyclerviewAdapter.setUpFetching(false);
                                ongoingOrderRecyclerviewAdapter.setUpFetchEnable(false);
                                allOrderSwiperefreshlayout.setRefreshing(false);
                            }else{
                                mMvpPresenter.getProgressIndent(spUserID,"3", mMultipleStateView);
                                allOrderSwiperefreshlayout.setRefreshing(false);
                            }
                        }
                    }, 2000);
                }
            });
    }

    @Override
    public void getProgressIndent(AllOrderBean bean) {
        for (int i = 0; i < bean.getTasks().size(); i++) {
            countBean.add(bean.getTasks().get(i));
        }
        //设置recyclerview
        setRecyclerview(bean);
        //设置上拉加载
        setUpLoad(bean);
        //设置下拉刷新
        initSwipeRefresh();
    }

    @Override
    public void getIndentNext(AllOrderBean bean) {
        for (int i = 0; i < bean.getTasks().size(); i++) {
            countBean.add(bean.getTasks().get(i));
        }
    }

    private void setUpLoad(AllOrderBean bean) {
        ongoingOrderRecyclerviewAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);//设置recyclerview 动画
        ongoingOrderRecyclerviewAdapter.isFirstOnly(false);//设置动画一直使用
        if (bean.getTasks().size() >=10) {
            ongoingOrderRecyclerviewAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    setLoadMore();
                }
            }, orderRecycler);
        }
    }

    private void setLoadMore() {
        orderRecycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (page < 2) {
                    mMvpPresenter.getIndentNext(spUserID, "3", page + "", mMultipleStateView);
                    page++;
                }
                if (mCurrentCounter >= countBean.size()&&page<2) {
                    //数据全部加载完毕
                    ongoingOrderRecyclerviewAdapter.loadMoreEnd();
                } else {
                    if (isErr) {
                        //成功获取更多数据
                        ongoingOrderRecyclerviewAdapter.addData(countBean);
                        mCurrentCounter = ongoingOrderRecyclerviewAdapter.getData().size();
                        ongoingOrderRecyclerviewAdapter.loadMoreComplete();
                    } else {
                        //获取更多数据失败
                        isErr = true;
                        Toast.makeText(getMContext(), "数据加载失败", Toast.LENGTH_LONG).show();
                        ongoingOrderRecyclerviewAdapter.loadMoreFail();
                    }
                }
            }
        }, 1500);
    }

    @Override
    public void getEmpty() {
        //暂无订单
        llNoOrder.setVisibility(View.VISIBLE);
        orderRecycler.setVisibility(View.GONE);
        initSwipeRefresh();
    }

    private void setRecyclerview(AllOrderBean bean) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getMContext());
        orderRecycler.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置 多布局type
        //设置Adapter

        ongoingOrderRecyclerviewAdapter = new OngoingOrderRecyclerviewAdapter(R.layout.adapter_ongoing_layout, bean.getTasks());
        orderRecycler.setAdapter(ongoingOrderRecyclerviewAdapter);
        ongoingOrderRecyclerviewAdapter.disableLoadMoreIfNotFullPage(orderRecycler);
     }

}
