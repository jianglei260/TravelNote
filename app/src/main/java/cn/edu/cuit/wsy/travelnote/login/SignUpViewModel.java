package cn.edu.cuit.wsy.travelnote.login;

import android.content.Intent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.User;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.main.MainActivity;
import cn.edu.cuit.wsy.travelnote.user.UserInfoActivity;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;

/**
 * Created by jianglei on 2017/4/16.
 */

public class SignUpViewModel extends DetailViewModel {
    private static final String TAG = "SignUpViewModel";
    public OnClickListener signClick = new OnClickListener() {
        public void onClick(View var1) {
            onSignClick();
        }
    };
    public ObservableField<String> account = new ObservableField<String>();
    public ObservableField<String> password = new ObservableField<String>();
    private UserSignActivity userSignActivity;


    public SignUpViewModel(UserSignActivity userSignActivity) {
        this.userSignActivity = userSignActivity;
        title.set("用户注册");
    }

    public void onSignClick() {
        if (TextUtils.isEmpty(account.get())) {
            Toast.makeText(userSignActivity, R.string.account_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password.get())) {
            Toast.makeText(userSignActivity, R.string.password_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        loadding.set(true);
        RxUtil.execute(new IOTask<User>() {
            @Override
            public User run() {
                return UserRepository.getInstance().signUp(account.get(), password.get());
            }
        }, new UIAction<User>() {
            @Override
            public void onComplete(User user) {
                loadding.set(false);
                if (user!=null){
                    Intent intent=new Intent(userSignActivity, MainActivity.class);
                    userSignActivity.startActivity(intent);
                    Intent infoIntent=new Intent(userSignActivity, UserInfoActivity.class);
                    userSignActivity.startActivity(infoIntent);
                    userSignActivity.publishEvent(MainActivity.ACTION_USER_LOGIN);
                    userSignActivity.finish();
                }else {
                    Toast.makeText(userSignActivity, R.string.sign_up_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBack() {
        userSignActivity.finish();
    }
}
