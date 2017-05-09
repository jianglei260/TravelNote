package cn.edu.cuit.wsy.travelnote.ask;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityAskListBinding;

public class AskListActivity extends BaseActivity {
    public static final String ACTION_ASK_UPDATE="ACTION_ASK_UPDATE";
    ActivityAskListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_ask_list);
        binding.setAskViewModel(new AskListViewModel(this));
        registeEventAction(ACTION_ASK_UPDATE);
        registeEventAction(AskDetailActivity.ACTION_ASK_EDIT);
    }

    @Override
    protected void onEvent(String action) {
        super.onEvent(action);
        if (action.equals(ACTION_ASK_UPDATE)||action.equals(AskDetailActivity.ACTION_ASK_EDIT)){
            binding.getAskViewModel().initAsks();
        }
    }
}
