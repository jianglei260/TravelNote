package cn.edu.cuit.wsy.travelnote.login;

import android.content.Intent;
import android.databinding.ObservableField;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.User;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.main.MainActivity;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;


/**
 * Created by jianglei on 2017/4/16.
 */


public class LoginViewModel extends DetailViewModel {
    private static final String TAG = "LoginViewModel";
    public OnClickListener signClick = new OnClickListener() {
        public void onClick(View var1) {
            onSignClick();
        }
    };
    public ObservableField<String> account = new ObservableField<String>();
    public OnClickListener loginClick = new OnClickListener() {
        public void onClick(View var1) {
            onLoginClick();
        }
    };
    public OnClickListener testClick = new OnClickListener() {
        public void onClick(View var1) {
//            onTestClick();
        }
    };
    public ObservableField<String> password = new ObservableField<String>();
    private UserLoginActivity userLoginActivity;

    public LoginViewModel(UserLoginActivity userLoginActivity) {
        this.userLoginActivity = userLoginActivity;
        title.set("用户登录");
    }

    public void onLoginClick() {
        if (TextUtils.isEmpty(account.get())) {
            Toast.makeText(userLoginActivity, R.string.account_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password.get())) {
            Toast.makeText(userLoginActivity, R.string.password_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        loadding.set(true);
        RxUtil.execute(new IOTask<User>() {
            @Override
            public User run() {
                return UserRepository.getInstance().login(account.get(), password.get());
            }
        }, new UIAction<User>() {
            @Override
            public void onComplete(User user) {
                loadding.set(false);
                if (user!=null){
                    Intent intent=new Intent(userLoginActivity, MainActivity.class);
                    userLoginActivity.startActivity(intent);
                    userLoginActivity.publishEvent(MainActivity.ACTION_USER_LOGIN);
                    userLoginActivity.finish();
                }else {
                    Toast.makeText(userLoginActivity, R.string.login_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void gotoUserSignActivity() {
        Intent intent = new Intent(userLoginActivity, UserSignActivity.class);
        userLoginActivity.startActivity(intent);
    }

    public void onSignClick() {
        gotoUserSignActivity();
    }
}
