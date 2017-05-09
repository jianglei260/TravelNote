package cn.edu.cuit.wsy.travelnote.ask;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityNewAskBinding;

public class NewAskActivity extends BaseActivity {
    ActivityNewAskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String objectId = getIntent().getStringExtra("objectId");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_ask);
        binding.setAskViewModel(new NewAskViewModel(this, objectId));
    }

    public void askEdit(){
        publishEvent(AskDetailActivity.ACTION_ASK_EDIT);
    }
}
