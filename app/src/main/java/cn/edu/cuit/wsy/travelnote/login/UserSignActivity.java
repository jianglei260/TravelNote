package cn.edu.cuit.wsy.travelnote.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityUserSignBinding;


/**
 * Created by jianglei on 2017/4/16.
 */

public class UserSignActivity extends BaseActivity {
    private ActivityUserSignBinding binding;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_sign);
        SignUpViewModel signUpViewModel = new SignUpViewModel(this);
        binding.setSignUpViewModel(signUpViewModel);
    }
}
