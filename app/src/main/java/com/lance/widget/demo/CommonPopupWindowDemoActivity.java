package com.lance.widget.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lance.common.util.DensityUtil;
import com.lance.common.widget.CommonPopupWindow;

import java.util.ArrayList;
import java.util.List;

import static com.lance.widget.demo.R.id.recycle_view;

public class CommonPopupWindowDemoActivity extends AppCompatActivity implements View.OnClickListener, CommonPopupWindow.ViewInterface {
    private CommonPopupWindow popupWindow;
    private boolean isShowRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_popup_window_demo);
        initViews();
    }

    public void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar(toolbar, "PopupWindow", true);
    }

    //向下弹出
    public void showDownPop(View view) {
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popup_down)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.AnimDown)
                .setViewOnclickListener(this)
                .setOutsideTouchable(true)
                .create();
        popupWindow.showAsDropDown(view);
    }

    //向右弹出
    public void showRightPop(View view) {
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popup_left_or_right)
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.AnimHorizontal)
                .setViewOnclickListener(this)
                .create();
        popupWindow.showAsDropDown(view, view.getWidth(), -view.getHeight());
    }

    //向左弹出
    public void showLeftPop(View view) {
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popup_left_or_right)
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.AnimRight)
                .setViewOnclickListener(this)
                .create();
        popupWindow.showAsDropDown(view, -popupWindow.getWidth(), -view.getHeight());
    }


    //全屏弹出
    public void showAll(View view) {
        if (popupWindow != null && popupWindow.isShowing()) return;
        View upView = LayoutInflater.from(this).inflate(R.layout.popup_up, null);
        //测量View的宽高
        measureWidthAndHeight(upView);
        popupWindow = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popup_up)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, upView.getMeasuredHeight())
                .setBackGroundLevel(0.5f)//取值范围0.0f-1.0f 值越小越暗
                .setAnimationStyle(R.style.AnimUp)
                .setViewOnclickListener(this)
                .create();
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
    }

    //向上弹出
    public void showUpPop(View view) {
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popup_left_or_right)
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setViewOnclickListener(this)
                .create();
        popupWindow.showAsDropDown(view, 0, -(popupWindow.getHeight() + view.getMeasuredHeight()));
    }


    public void showReminder(View view) {
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow = new CommonPopupWindow.Builder(this)
                .setView(R.layout.query_info)
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .create();
        popupWindow.showAsDropDown(view, (-popupWindow.getWidth() + DensityUtil.dp2px(this, 20)), -(popupWindow.getHeight() + view.getMeasuredHeight()));
    }

    @Override
    public void getChildView(View view, int layoutResId) {
        //获得PopupWindow布局里的View
        switch (layoutResId) {
            case R.layout.popup_down:
                final RecyclerView recyclerView = (RecyclerView) view.findViewById(recycle_view);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                final PopupAdapter adapter = new PopupAdapter(this, getData());
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new MyOnclickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        toast("position is " + position + ", text is " + adapter.getItem(position));
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                    }
                });
                break;
            case R.layout.popup_up:
                Button btn_take_photo = (Button) view.findViewById(R.id.btn_take_photo);
                Button btn_select_photo = (Button) view.findViewById(R.id.btn_select_photo);
                Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
                btn_take_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast("拍照");
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                    }
                });
                btn_select_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast("相册选取");
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                    }
                });
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                        return true;
                    }
                });
                break;
            case R.layout.popup_left_or_right:
                TextView tv_like = (TextView) view.findViewById(R.id.tv_like);
                TextView tv_hate = (TextView) view.findViewById(R.id.tv_hate);
                tv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast("赞一个");
                        popupWindow.dismiss();
                    }
                });
                tv_hate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast("踩一下");
                        popupWindow.dismiss();
                    }
                });
                break;
        }
    }

    public void initToolBar(Toolbar toolbar, String name, boolean showHomeAsUp) {
        initToolBar(toolbar, name, showHomeAsUp, false);
    }

    public void initToolBar(Toolbar toolbar, String name, boolean showHomeAsUp, boolean isShowRight) {
        this.isShowRight = isShowRight;
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isShowRight) {
            getMenuInflater().inflate(R.menu.toolbar_right, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_icon:
                add();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void add() {
    }

    /**
     * @param str 弹出的文字
     */
    public void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 测量View的宽高
     *
     * @param view View
     */
    private void measureWidthAndHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
    }

    private List<String> getData() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            data.add("王者荣耀" + (i + 1));
        }
        return data;
    }
}


class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.MyViewHolder> {
    private Context context;
    private MyOnclickListener myItemClickListener;
    private List<String> data;

    PopupAdapter(Context mContext, List<String> data) {
        this.context = mContext;
        this.data = data;
    }

    void setOnItemClickListener(MyOnclickListener listener) {
        this.myItemClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_item, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        holder.choice_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myItemClickListener != null) {
                    myItemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.choice_text.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public String getItem(int position) {
        return data != null ? data.get(position) : null;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView choice_text;

        MyViewHolder(final View itemView) {
            super(itemView);
            choice_text = (TextView) itemView.findViewById(R.id.choice_text);
        }
    }
}

interface MyOnclickListener {
    void onItemClick(View view, int position);
}