package com.hotbitmapgg.ohmybilibili.module.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hotbitmapgg.ohmybilibili.R;
import com.hotbitmapgg.ohmybilibili.adapter.UserCoinsVideoAdapter;
import com.hotbitmapgg.ohmybilibili.base.RxLazyFragment;
import com.hotbitmapgg.ohmybilibili.entity.user.UserCoinsInfo;
import com.hotbitmapgg.ohmybilibili.network.RetrofitHelper;
import com.hotbitmapgg.ohmybilibili.rx.RxBus;
import com.hotbitmapgg.ohmybilibili.utils.ConstantUtils;
import com.hotbitmapgg.ohmybilibili.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 2016/10/12 18:18
 * 100332338@qq.com
 * <p>
 * 用户详情界面的投币
 */

public class UserCoinsVideoFragment extends RxLazyFragment
{

    @Bind(R.id.recycle)
    RecyclerView mRecyclerView;

    @Bind(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;

    private int mid;

    private static final String EXTRA_MID = "extra_mid";

    private List<UserCoinsInfo.DataBean.ListBean> userCoins = new ArrayList<>();

    private UserCoinsVideoAdapter mAdapter;

    private int count;

    public static UserCoinsVideoFragment newInstance(int mid)
    {

        UserCoinsVideoFragment mFragment = new UserCoinsVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_MID, mid);
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Override
    public int getLayoutResId()
    {

        return R.layout.fragment_user_coins;
    }

    @Override
    public void finishCreateView(Bundle state)
    {

        mid = getArguments().getInt(EXTRA_MID);

        initRecyclerView();
        getUserCoins();
    }

    private void initRecyclerView()
    {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new UserCoinsVideoAdapter(mRecyclerView, userCoins);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getUserCoins()
    {

        RetrofitHelper.getUserCoinsVideoApi()
                .getUserCoinVideos(mid)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userCoinsInfo -> {

                    count = userCoinsInfo.getData().getCount();
                    userCoins.addAll(userCoinsInfo.getData().getList());
                    finishTask();
                }, throwable -> {
                    initEmptyLayout();
                });
    }

    private void finishTask()
    {

        postCount();
        mAdapter.notifyDataSetChanged();
        if (userCoins.isEmpty())
            initEmptyLayout();
    }

    private void postCount()
    {

        Bundle bundle = new Bundle();
        bundle.putString(ConstantUtils.EXTRA_TYPE, ConstantUtils.USER_COINS);
        bundle.putInt(ConstantUtils.EXTRA_COUNT, count);
        RxBus.getInstance().post(bundle);
    }

    private void initEmptyLayout()
    {

        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_space_no_data);
        mCustomEmptyView.setEmptyText("ㄟ( ▔, ▔ )ㄏ 再怎么找也没有啦");
        mCustomEmptyView.hideReloadButton();
    }
}
