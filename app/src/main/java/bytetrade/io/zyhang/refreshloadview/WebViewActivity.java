package bytetrade.io.zyhang.refreshloadview;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import bytetrade.io.zyhang.viewlibrary.RefreshLoadLayout;

/**
 * Created by zyhang on 2019/5/15
 * <p>
 * Description:
 */
public class WebViewActivity extends AppCompatActivity {
    RefreshLoadLayout mRll;
    WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mRll = findViewById(R.id.rll_web);
        mRll.setHeader(getLayoutInflater().inflate(R.layout.layout_header, null));
        View view = getLayoutInflater().inflate(R.layout.layout_empty, null);
        Button button = view.findViewById(R.id.btn_empty);
        mRll.setEmpty(view, button);

        mRll.addOnHeaderStateListener(new RefreshLoadLayout.OnHeaderStateListener() {
            @Override
            public void onScrollChange(View Header, int scrollOffset, int scrollRatio) {

            }

            @Override
            public void onRefresh(View Header) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //正确地址
                        mWebView.loadUrl("https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=2&tn=baiduhome_pg&wd=webview%E9%87%8D%E6%96%B0%E5%8A%A0%E8%BD%BD&rsv_spt=1&oq=RelativeLayout.LayoutParams&rsv_pq=b808325f000048de&rsv_t=e51cfsZDdW65ruvjHyJgz3cKWvRZsOxOs5wyQ0tKTHrOxTM4WwjUR4o3W2vX64JOY676&rqlang=cn&rsv_enter=1&inputT=9062&rsv_sug3=103&rsv_sug1=6&rsv_sug7=100&rsv_sug2=0&rsv_sug4=10024");
                        mRll.refreshFinish();
                    }
                }, 3000);
            }

            @Override
            public void onFinished(View Header) {

            }
        });


        mWebView = findViewById(R.id.web);
        //错误地址
        mWebView.loadUrl("https://blowprofilcoding/article/details/77928614");

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //设置不显示
                mRll.setEmptyState(false);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                //设置显示
                mRll.setEmptyState(true);
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

        });
    }
}
