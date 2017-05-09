package cn.edu.cuit.wsy.travelnote.ask;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityAskDetailBinding;
import cn.edu.cuit.wsy.travelnote.note.TextEditActivity;

public class AskDetailActivity extends BaseActivity {
    public static final String ACTION_ASK_EDIT="ACTION_ASK_EDIT";
    ActivityAskDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String objectId = getIntent().getStringExtra("objectId");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ask_detail);
        binding.setAskViewModel(new AskDetailViewModel(this, objectId));
        registeEventAction(TextEditActivity.ACTION_EDIT_FINISH);
        registeEventAction(ACTION_ASK_EDIT);
    }

    public void askUpdate(){
        publishEvent(AskListActivity.ACTION_ASK_UPDATE);
    }

    @Override
    protected void onEvent(String action) {
        super.onEvent(action);
        if (action.equals(ACTION_ASK_EDIT)){
            binding.getAskViewModel().onRefresh(null);
        }
    }

    @Override
    protected void onEvent(Intent intent) {
        super.onEvent(intent);
        if (intent.getAction().equals(TextEditActivity.ACTION_EDIT_FINISH)) {
            if (intent.getIntExtra(TextEditActivity.INDEX, -1) >= 0) {
                int index = intent.getIntExtra(TextEditActivity.INDEX, -1);
                binding.getAskViewModel().commentItemEdit(TextEditActivity.getEditContent(intent), index);
            } else {
                binding.getAskViewModel().commentEdit(TextEditActivity.getEditContent(intent));
            }
        }
    }
}
