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
import android.widget.TextView;

import java.util.ArrayList;

import bytetrade.io.zyhang.viewlibrary.RefreshLoadLayout;

/**
 * Created by zyhang on 2019/5/15
 * <p>
 * Description:
 */
public class RecyclerViewActivity extends AppCompatActivity {
    RefreshLoadLayout mRll;
    RecyclerView mRecycleView;
    ArrayList<String> mArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        mRll = findViewById(R.id.rll_recycler);

        mRll.setFunctionState(true, true);
        mRll.setDamp(4);
        mRll.setHeader(getLayoutInflater().inflate(R.layout.layout_header, null));
        mRll.setFooter(getLayoutInflater().inflate(R.layout.layout_footer, null));

        mRll.addOnHeaderStateListener(new RefreshLoadLayout.OnHeaderStateListener() {
            @Override
            public void onScrollChange(View Header, int scrollOffset, int scrollRatio) {

            }

            @Override
            public void onRefresh(View Header) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRll.refreshFinish();
                    }
                }, 3000);
            }

            @Override
            public void onFinished(View Header) {

            }
        });

        mRll.addOnFooterStateListener(new RefreshLoadLayout.OnFooterStateListener() {
            @Override
            public void onScrollChange(View Footer, int scrollOffset, int scrollRatio) {

            }

            @Override
            public void onLoadMore(View Footer) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRll.loadMoreFinish();
                        //mRll.isMore(false);
                    }
                }, 3000);
            }

            @Override
            public void onFinished(View Footer) {

            }

            @Override
            public void onNotMore(View Footer) {

            }

            @Override
            public void onHasMore(View Footer) {

            }
        });
        //adapter数据
        mArrayList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mArrayList.add("???");
        }
        //recycler设置
        mRecycleView = findViewById(R.id.recycler);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(new myAdapter());

    }


    class myAdapter extends RecyclerView.Adapter<myAdapter.viewHolder> {

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
