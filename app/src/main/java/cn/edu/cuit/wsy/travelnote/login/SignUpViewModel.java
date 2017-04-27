package cn.edu.cuit.wsy.travelnote.login;

import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import cn.edu.cuit.liyun.laboratory.base.DetailViewModel;
import cn.edu.cuit.liyun.laboratory.data.entity.Role;
import cn.edu.cuit.liyun.laboratory.data.entity.User;
import cn.edu.cuit.liyun.laboratory.data.repository.UserRepository;
import cn.edu.cuit.liyun.laboratory.utils.IOTask;
import cn.edu.cuit.liyun.laboratory.utils.RxUtil;
import cn.edu.cuit.liyun.laboratory.utils.UIAction;

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
        loadding.set(true);
        RxUtil.execute(new IOTask<User>() {
            @Override
            public User run() {
                return UserRepository.getInstance().signUp(account.get(), password.get(), Role.STUDENT);
            }
        }, new UIAction<User>() {
            @Override
            public void onComplete(User user) {
                loadding.set(false);
                Log.d(TAG, "onComplete: "+user.getObjectId());
            }
        });
    }
}
