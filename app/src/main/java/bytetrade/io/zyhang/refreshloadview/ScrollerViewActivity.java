package bytetrade.io.zyhang.refreshloadview;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ScrollView;

import bytetrade.io.zyhang.viewlibrary.RefreshLoadLayout;

/**
 * Created by zyhang on 2019/5/15
 * <p>
 * Description:
 */
public class ScrollerViewActivity extends AppCompatActivity {
    RefreshLoadLayout mRll;
    ScrollView mScroll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollerview);
        mRll = findViewById(R.id.rll_scroll);
        mScroll = findViewById(R.id.scroll);

        mRll.setHeader(getLayoutInflater().inflate(R.layout.layout_header, null));
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
                }, 1000);
            }

            @Override
            public void onFinished(View Header) {

            }
        });
    }
}
