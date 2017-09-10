package com.example.anadministrator.mytest2;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.anadministrator.mytest2.Adapter.MyRecyclerAdapter;
import com.example.anadministrator.mytest2.JavaBean.Bean;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String path = "http://c.3g.163.com/recommend/getChanListNews?channel=T1456112189138&size=20&passport=&devId=1uuFYbybIU2oqSRGyFrjCw%3D%3D&lat=%2F%2FOm%2B%2F8ScD%2B9fX1D8bxYWg%3D%3D&lon=LY2l8sFCNzaGzqWEPPgmUw%3D%3D&version=9.0&net=wifi&ts=1464769308&sign=bOVsnQQ6gJamli6%2BfINh6fC%2Fi9ydsM5XXPKOGRto5G948ErR02zJ6%2FKXOnxX046I&encryption=1&canal=meizu_store2014_news&mac=sSduRYcChdp%2BBL1a9Xa%2F9TC0ruPUyXM4Jwce4E9oM30%3D";
    private AlertDialog dialog;
    private Bean bean;
    private RecyclerView mRecyclerview;
    private MyRecyclerAdapter myRecyclerAdapter;
    private SwipeRefreshLayout mSwipRefreshLayout;
    private StaggeredGridLayoutManager layoutManager;
    private Button mButAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //1) 访问数据前先判断网络连接状态，无网络时Toast提示用户（5分）
        NetToShow();
        //2) 加载数据过程中给用户展示加载等待对话框(5分)
        waitDialog();
        //请求数据
        RequestData();
        //隐藏对话框
        dialog.dismiss();

        mSwipRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //我在List最前面加入一条数据
                bean.美女.add(0, bean.美女.get(1));

                //数据重新加载完成后，提示数据发生改变，并且设置现在不在刷新
                myRecyclerAdapter.notifyDataSetChanged();
                mSwipRefreshLayout.setRefreshing(false);
            }
        });



    }

    private void loadMoreData() {
        for (int i = 0; i < 10; i++) {
            bean.美女.add(0, bean.美女.get(1));
            myRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        myRecyclerAdapter = new MyRecyclerAdapter(bean, this);
        mRecyclerview.setAdapter(myRecyclerAdapter);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(layoutManager);

    }

    private void RequestData() {
        new Thread() {
            @Override
            public void run() {
                //创建Client
                OkHttpClient client = new OkHttpClient.Builder().build();

                //创建请求对象
                Request request = new Request.Builder().url(path).build();
                try {
                    //同步
                    Response response = client.newCall(request).execute();
                    String string = response.body().string();
                    bean = new Gson().fromJson(string, Bean.class);
                    //初始化RecyclerView
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initRecyclerView();
                            myRecyclerAdapter.setmOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Toast.makeText(MainActivity.this, "第" + position + "个条目", Toast.LENGTH_SHORT).show();
                                }
                            });
                            load();
                            mRecyclerview.setItemAnimator(new SimpleItemAnimator() {
                                @Override
                                public boolean animateRemove(RecyclerView.ViewHolder holder) {
                                    return false;
                                }

                                @Override
                                public boolean animateAdd(RecyclerView.ViewHolder holder) {
                                    ObjectAnimator
                                            .ofFloat(holder.itemView, "alpha", 0.0f, 1.0f)
                                            .setDuration(1000)
                                            .start();
                                    return false;
                                }

                                @Override
                                public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
                                    return false;
                                }

                                @Override
                                public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
                                    return false;
                                }

                                @Override
                                public void runPendingAnimations() {

                                }

                                @Override
                                public void endAnimation(RecyclerView.ViewHolder item) {

                                }

                                @Override
                                public void endAnimations() {

                                }

                                @Override
                                public boolean isRunning() {
                                    return false;
                                }
                            });
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    private void NetToShow() {
        boolean netState = getNetState();
        if (netState == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("是否设置网络");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
                }
            });
            builder.create().show();
        } else {
            Toast.makeText(MainActivity.this, "有网！！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 得到网络状态的方法
     *
     * @return
     */
    public boolean getNetState() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            return true;
        }
        return false;
    }



    public void waitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示");
        builder.setMessage("正在加载数据,稍等~");
        dialog = builder.create();
        dialog.show();
    }

    private void initView() {
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        mSwipRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipRefreshLayout);
        mButAdd = (Button) findViewById(R.id.butAdd);
        mButAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butAdd:
                //TODO
                bean.美女.add(1, bean.美女.get(1));
                myRecyclerAdapter.notifyItemInserted(1);
                break;
            default:
                break;
        }
    }

    public void load(){
        mRecyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) mRecyclerview.getLayoutManager();
            boolean isSlidingToLast = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    //获取最后一个完全显示的ItemPosition
                    int[] lastVisiblePositions = manager.findLastVisibleItemPositions(new int[manager.getSpanCount()]);
                    int lastVisiblePos = getMaxElem(lastVisiblePositions);
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部
                    if (lastVisiblePos == (totalItemCount -1) && isSlidingToLast) {
                        //加载更多功能的代码
                        loadMoreData();
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //用来判断横向滑动方向，dy用来判断纵向滑动方向
                if(dy > 0){
                    //大于0表示，正在向下滚动
                    isSlidingToLast = true;
                }else{
                    //小于等于0 表示停止或向上滚动
                    isSlidingToLast = false;
                }
            }
        });

    }
    private int getMaxElem(int[] arr) {
        int size = arr.length;
        int maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            if (arr[i]>maxVal)
                maxVal = arr[i];
        }
        return maxVal;
    }
}