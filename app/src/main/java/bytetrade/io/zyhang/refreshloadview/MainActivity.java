package bytetrade.io.zyhang.refreshloadview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button mBtnScroll, mBtnRecycler, mBtnWeb, mBtnRecyclerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnScroll = findViewById(R.id.btn_scroll);
        mBtnRecycler = findViewById(R.id.btn_recycler);
        mBtnWeb = findViewById(R.id.btn_web);
        mBtnRecyclerModel = findViewById(R.id.btn_recycler_model);

        mBtnScroll.setOnClickListener(this);
        mBtnRecycler.setOnClickListener(this);
        mBtnWeb.setOnClickListener(this);
        mBtnRecyclerModel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scroll:
                startActivity(new Intent(MainActivity.this, ScrollerViewActivity.class));
                break;
            case R.id.btn_recycler:
                startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class));
                break;
            case R.id.btn_web:
                startActivity(new Intent(MainActivity.this, WebViewActivity.class));
                break;
            case R.id.btn_recycler_model:
                startActivity(new Intent(MainActivity.this, RecyclerViewModelActivity.class));
                break;
            default:
                break;
        }
    }
}
