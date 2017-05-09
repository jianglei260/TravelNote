package cn.edu.cuit.wsy.travelnote.user;

import android.databinding.ObservableField;
import android.text.TextUtils;
import android.widget.Toast;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.UserInfo;
import cn.edu.cuit.wsy.travelnote.data.repository.FileRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/4.
 */

public class UserInfoViewModel extends DetailViewModel {
    private UserInfoActivity activity;
    private UserInfo userInfo;
    public ObservableField<String> userHead = new ObservableField<>();
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> userNick = new ObservableField<>();

    public ReplyCommand headClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            activity.selectPicture();
        }
    });

    public ReplyCommand saveClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            saveInfo();
        }
    });

    public void saveInfo() {;
        userInfo.setNick(userNick.get());
        loadding.set(true);
        RxUtil.execute(new IOTask<Boolean>() {
            @Override
            public Boolean run() {
                return UserRepository.getInstance().save(userInfo);
            }
        }, new UIAction<Boolean>() {
            @Override
            public void onComplete(Boolean s) {
                loadding.set(false);
                if (!s) {
                    Toast.makeText(activity, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onImageSelected(final byte[] data) {
        loadding.set(true);
        RxUtil.execute(new IOTask<Boolean>() {
            @Override
            public Boolean run() {
                String url = FileRepository.getInstance().save(data);
                if (!TextUtils.isEmpty(url)) {
                    userInfo.setUrl(url);
                    return UserRepository.getInstance().editInfo(userInfo, "url", url);
                } else {
                    return false;
                }
            }
        }, new UIAction<Boolean>() {
            @Override
            public void onComplete(Boolean s) {
                loadding.set(false);
                if (!s) {
                    Toast.makeText(activity, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public UserInfoViewModel(UserInfoActivity activity) {
        this.activity = activity;
        initUserInfo();
    }

    public void initUserInfo() {
        RxUtil.execute(new IOTask<UserInfo>() {
            @Override
            public UserInfo run() {
                return UserRepository.getInstance().getMyInfo();
            }
        }, new UIAction<UserInfo>() {
            @Override
            public void onComplete(UserInfo userInfo) {
                UserInfoViewModel.this.userInfo = userInfo;
                bindInfo();
            }
        });
    }

    public void bindInfo() {
        userHead.set(userInfo.getUrl());
        userName.set(userInfo.getName());
        userNick.set(userInfo.getNick());
    }

    @Override
    public void onBack() {
        activity.finish();
    }
}
