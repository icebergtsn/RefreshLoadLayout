# Dependency

支持RecyclerView ScrollerView 下拉刷新，加载更多 WebView下拉刷新，app中有示例。

Add this in your root build.gradle file:

```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Then, add the library to your module build.gradle:
```java
dependencies {
         implementation 'com.github.icebergtsn:RefreshLoadLayout:v1.0'
}
```
### RecyclerView

xml
```xml
  <bytetrade.io.zyhang.viewlibrary.RefreshLoadLayout
        android:id="@+id/rll_recycler"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <android.support.v7.widget.RecyclerView
            android:id="@+id/recycler"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    </bytetrade.io.zyhang.viewlibrary.RefreshLoadLayout>
    
</bytetrade.io.zyhang.viewlibrary.RefreshLoadLayout>
```

java

```java
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
```

