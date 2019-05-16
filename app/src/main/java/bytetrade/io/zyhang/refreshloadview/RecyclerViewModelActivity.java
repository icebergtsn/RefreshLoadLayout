package bytetrade.io.zyhang.refreshloadview;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import java.util.ArrayList;

import bytetrade.io.zyhang.viewlibrary.RefreshLoadLayout;

/**
 * Created by zyhang on 2019/5/13
 * <p>
 * Description:
 */
public class RecyclerViewModelActivity extends AppCompatActivity {
    RecyclerView mRecycleView;
    ArrayList<String> mArrayList;
    RefreshLoadLayout mRefreshLayout;
    //请求数据次数
    int mLoadTime = 0;
    SimpleAdapter mSimpleAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_model);
        mRefreshLayout = findViewById(R.id.rll_recycler_model);

        //打开上拉刷新，下拉加载
        mRefreshLayout.setFunctionState(true, true);
        //设置滑动阻力
        mRefreshLayout.setDamp(2);
        //可选设置底部无数据时布局，也有默认布局
        //mRefreshLayout.setFooterNoData(getLayoutInflater().inflate(R.layout.layout_footer_no_data_test, null));
        mRefreshLayout.setHeader(getLayoutInflater().inflate(R.layout.layout_header, null));
        mRefreshLayout.setFooter(getLayoutInflater().inflate(R.layout.layout_footer, null));

        mRefreshLayout.addOnHeaderStateListener(new RefreshLoadLayout.OnHeaderStateListener() {
            @Override
            public void onScrollChange(View Header, int scrollOffset, int scrollRatio) {

            }

            @Override
            public void onRefresh(View Header) {
                //通过接口获得Footer开启动画
                RotateAnimation anim = new RotateAnimation(0f, 720f, Header.getWidth() / 2, Header.getHeight() / 2);
                anim.setDuration(3000);
                Header.startAnimation(anim);

                //模拟网络请求延时
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mArrayList.clear();
                        for (int i = 0; i < 20; i++) {
                            mArrayList.add("这是初始数据的第" + i + "条数据");
                        }

                        mSimpleAdapter.notifyDataSetChanged();
                        mRefreshLayout.refreshFinish();
                        //重制次数
                        mLoadTime = 0;
                    }
                }, 3000);
            }

            @Override
            public void onFinished(View Header) {

            }


        });

        mRefreshLayout.addOnFooterStateListener(new RefreshLoadLayout.OnFooterStateListener() {
            @Override
            public void onScrollChange(View Footer, int scrollOffset, int scrollRatio) {

            }

            @Override
            public void onLoadMore(final View Footer) {
                if (mLoadTime < 2) {
                    //通过接口获得Footer开启动画
                    RotateAnimation anim = new RotateAnimation(0f, 720f, Footer.getWidth() / 2, Footer.getHeight() / 2);
                    anim.setDuration(2000);
                    Footer.startAnimation(anim);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //数据添加
                            for (int i = 0; i < 20; i++) {
                                mArrayList.add("这是第" + (mLoadTime + 1) + "次加载的第" + i + "条数据");
                            }
                            //次数加1
                            mLoadTime++;
                            mSimpleAdapter.notifyDataSetChanged();
                            //关闭
                            mRefreshLayout.loadMoreFinish();
                        }
                    }, 2000);
                } else {
                    mRefreshLayout.loadMoreFinish();
                    //没有数据了
                    mRefreshLayout.isMore(false);
                }

            }

            @Override
            public void onFinished(View Footer) {
            }

            @Override
            public void onNotMore(View Footer) {
                mRefreshLayout.loadMoreFinish();
            }

            @Override
            public void onHasMore(View Footer) {

            }
        });

        //初始数据
        mArrayList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mArrayList.add("这是初始数据的第" + i + "条数据");
        }
        //recycler配置
        mRecycleView = findViewById(R.id.recycler_model);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mSimpleAdapter = new SimpleAdapter();
        mRecycleView.setAdapter(mSimpleAdapter);

    }

    //移除监听
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRefreshLayout.removeOnFooterStateListener();
        mRefreshLayout.removeOnHeaderStateListener();
    }

    class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.viewHolder> {

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
            return new viewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {
            holder.text.setText(mArrayList.get(position));
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

        class viewHolder extends RecyclerView.ViewHolder {

            TextView text;

            public viewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.item_text);
            }
        }
    }

}
