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
import com.mouqu.zhailu.zhailu.contract.fragment.AllOrderContract;
import com.mouqu.zhailu.zhailu.modle.fragment.AllOrderModel;
import com.mouqu.zhailu.zhailu.presenter.fragment.AllOrderPresenter;
import com.mouqu.zhailu.zhailu.ui.adapter.AllorderRecyclerviewAdapter;
import com.mouqu.zhailu.zhailu.util.GetSPData;
import com.mouqu.zhailu.zhailu.util.MultipleItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;

public class AllOrderFragment extends BaseFragment<AllOrderPresenter, AllOrderModel> implements AllOrderContract.View, View.OnClickListener {
    @BindView(R.id.order_recycler)
    RecyclerView orderRecycler;
    @BindView(R.id.ll_no_order)
    LinearLayout llNoOrder;
    Unbinder unbinder;
    @BindView(R.id.all_order_swiperefreshlayout)
    SwipeRefreshLayout allOrderSwiperefreshlayout;
    private int mCurrentCounter = 0;
    private int page = 0;
    //刷新标志
    boolean isErr = true;
    List<AllOrderBean.TasksBean> countBean;
    private AllorderRecyclerviewAdapter allorderRecyclerviewAdapter;
    private String spUserID;


    @Override
    protected void initViewAndEvents() {
        mCurrentCounter = 0;
        page = 0;
        countBean = new ArrayList<AllOrderBean.TasksBean>();
        spUserID = new GetSPData().getSPUserID(getActivity());
        mMvpPresenter.getIndent(spUserID, mMultipleStateView);

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

    @Override
    public void getIndent(AllOrderBean bean) {
        for (int i = 0; i < bean.getTasks().size(); i++) {
            countBean.add(bean.getTasks().get(i));
        }
        //设置recyclerview
        setRecyclerview(countBean);
        //设置上拉加载
        setUpLoad(bean);
        //设置下拉刷新
        initSwipeRefresh();
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
                                allorderRecyclerviewAdapter.notifyDataSetChanged();
                                allorderRecyclerviewAdapter.setUpFetching(false);
                                allorderRecyclerviewAdapter.setUpFetchEnable(false);
                                allOrderSwiperefreshlayout.setRefreshing(false);
                            }else{
                                mMvpPresenter.getIndent(spUserID, mMultipleStateView);
                                allOrderSwiperefreshlayout.setRefreshing(false);
                            }
                        }
                    }, 2000);
                }
            });
    }


    @Override
    public void getIndentNext(AllOrderBean bean) {
        for (int i = 0; i < bean.getTasks().size(); i++) {
            countBean.add(bean.getTasks().get(i));
        }
    }

    private void setUpLoad(AllOrderBean bean) {
        allorderRecyclerviewAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);//设置recyclerview 动画
        allorderRecyclerviewAdapter.isFirstOnly(false);//设置动画一直使用
        if (bean.getTasks().size() >= 10) {
            onLoadMore();
        }
    }

    private void onLoadMore() {
        allorderRecyclerviewAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                orderRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (page < 3) {
                            page++;
                            mMvpPresenter.getIndentNext(spUserID, "0", page + "", mMultipleStateView);
                        }
                        if (mCurrentCounter >= countBean.size() && page < 3) {
                            //数据全部加载完毕
                            allorderRecyclerviewAdapter.loadMoreEnd();
                        } else {
                            if (isErr) {
                                List list = new ArrayList<>();
                                for (int i = 0; i < countBean.size(); i++) {
                                    MultipleItem multipleItem = new MultipleItem(Integer.parseInt(countBean.get(i).getProgress()), countBean.get(i));
                                    list.add(multipleItem);
                                }
                                //成功获取更多数据
                                allorderRecyclerviewAdapter.addData(list);
                                mCurrentCounter = allorderRecyclerviewAdapter.getData().size();
                                allorderRecyclerviewAdapter.loadMoreComplete();

                            } else {
                                //获取更多数据失败
                                isErr = true;
                                Toast.makeText(getMContext(), "数据加载失败", Toast.LENGTH_LONG).show();
                                allorderRecyclerviewAdapter.loadMoreFail();
                            }
                        }
                    }

                }, 1500);
            }
        }, orderRecycler);
    }


    @Override
    public void getEmpty() {
        //暂无订单
        llNoOrder.setVisibility(View.VISIBLE);
        orderRecycler.setVisibility(View.GONE);
        initSwipeRefresh();
    }

    //设置recyclerview
    private void setRecyclerview(List<AllOrderBean.TasksBean> countBean) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getMContext());
        orderRecycler.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置 多布局type
        List list = new ArrayList<>();
        for (int i = 0; i < countBean.size(); i++) {
            MultipleItem multipleItem = new MultipleItem(Integer.parseInt(countBean.get(i).getProgress()), countBean.get(i));
            list.add(multipleItem);
        }
        //设置Adapter
        allorderRecyclerviewAdapter = new AllorderRecyclerviewAdapter(getMContext(), list);
        orderRecycler.setAdapter(allorderRecyclerviewAdapter);
        allorderRecyclerviewAdapter.disableLoadMoreIfNotFullPage(orderRecycler);
    }
}
