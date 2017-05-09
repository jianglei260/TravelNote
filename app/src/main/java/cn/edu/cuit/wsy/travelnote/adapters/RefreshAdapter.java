package cn.edu.cuit.wsy.travelnote.adapters;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.Refreshable;


/**
 * Created by jianglei on 2017/5/3.
 */

public class RefreshAdapter {
    @BindingAdapter({"refresh"})
    public static void setRefreshListener(final SwipeRefreshLayout layout, final Refreshable refreshable){
        layout.setColorSchemeColors(layout.getResources().getColor(R.color.colorPrimary));
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshable.onRefresh(new Refreshable.OnComplete() {
                    @Override
                    public void onComplete() {
                        layout.setRefreshing(false);
                    }
                });
            }
        });
    }
}
