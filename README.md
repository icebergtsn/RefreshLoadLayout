# Dependency

支持RecyclerView  下拉刷新，加载更多 ScrollerView WebView下拉刷新，app中有示例。

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
Don't forget to add this whether you use it or not
```
dependencies {
        implementation 'com.android.support:recyclerview-v7:28.0.0'
}
```

# Usage

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
```
java

```java
//设置开启下拉刷新和上拉加载
mRll.setFunctionState(true, true);
//头部布局
mRll.setHeader(getLayoutInflater().inflate(R.layout.layout_header, null));
//底部布局
mRll.setFooter(getLayoutInflater().inflate(R.layout.layout_footer, null));

//头部状态监听
mRll.addOnHeaderStateListener(new RefreshLoadLayout.OnHeaderStateListener() {
        @Override
        public void onScrollChange(View Header, int scrollOffset, int scrollRatio) {

        }

        @Override
        public void onRefresh(View Header) {
            //关闭刷新
            mRll.refreshFinish();
        }

        @Override
        public void onFinished(View Header) {

        }
});

//尾部状态监听
mRll.addOnFooterStateListener(new RefreshLoadLayout.OnFooterStateListener() {
        @Override
        public void onScrollChange(View Footer, int scrollOffset, int scrollRatio) {

        }

        @Override
        public void onLoadMore(View Footer) {
            //关闭加载
            mRll.loadMoreFinish();
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
# Document

方法名|约束
--|--
setFunctionState(boolean isRefresh, boolean isLoadMore)|设置下拉刷新与上拉加载开启关闭
setHeader(View Header)|设置下拉刷新布局
setFooter(View Footer)|设置上拉加载布局
setFooterNoData(View FooterNoData)|设置底部没有数据布局
setEmpty(View view)|webview空布局
setEmptyState(boolean isEmpty)|设置webview空布局显示关闭
refreshFinish()|下拉刷新完成
loadMoreFinish()|上拉加载完成
isMore(boolean isMore)|是否有更多数据
addOnHeaderStateListener(OnHeaderStateListener listener)|添加头部状态监听器
addOnFooterStateListener(OnFooterStateListener listener)|添加底部状态监听器
removeOnHeaderStateListener()|移除头部状态监听器
removeOnFooterStateListener()|移除尾部状态监听哦
setDamp(int damp)|设置滑动阻力




