package cn.edu.cuit.wsy.travelnote.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableField;
import android.widget.Toast;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.ask.AskListActivity;
import cn.edu.cuit.wsy.travelnote.base.BaseViewModel;
import cn.edu.cuit.wsy.travelnote.comment.CommentListActivity;
import cn.edu.cuit.wsy.travelnote.comment.MyCommentActivity;
import cn.edu.cuit.wsy.travelnote.data.entity.User;
import cn.edu.cuit.wsy.travelnote.data.entity.UserInfo;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.login.UserLoginActivity;
import cn.edu.cuit.wsy.travelnote.note.NoteListActivity;
import cn.edu.cuit.wsy.travelnote.note.NoteListViewModel;
import cn.edu.cuit.wsy.travelnote.user.UserInfoActivity;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class MeViewModel extends BaseViewModel {
    private MeFragment fragment;
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> userHead = new ObservableField<>();
    private UserInfo myInfo;
    public ReplyCommand noteClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            if (!UserRepository.getInstance().isLogin()){
                Toast.makeText(fragment.getActivity(),"登录后才能操作",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(fragment.getActivity(), NoteListActivity.class);
            fragment.startActivity(intent);
        }
    });

    public ReplyCommand settingClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
//            Intent intent = new Intent(fragment.getActivity(), SettingActivity.class);
//            fragment.startActivity(intent);
            if (UserRepository.getInstance().isLogin())
                logout();
        }
    });
    public ReplyCommand askClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            if (!UserRepository.getInstance().isLogin()){
                Toast.makeText(fragment.getActivity(),"登录后才能操作",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(fragment.getActivity(), AskListActivity.class);
            fragment.startActivity(intent);
        }
    });
    public ReplyCommand commentClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            if (!UserRepository.getInstance().isLogin()){
                Toast.makeText(fragment.getActivity(),"登录后才能操作",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(fragment.getActivity(), MyCommentActivity.class);
            fragment.startActivity(intent);
        }
    });
    public ReplyCommand userHeadClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            if (UserRepository.getInstance().isLogin()) {
                Intent intent = new Intent(fragment.getActivity(), UserInfoActivity.class);
                fragment.startActivity(intent);
            } else {
                Intent intent = new Intent(fragment.getActivity(), UserLoginActivity.class);
                fragment.startActivity(intent);
            }
        }
    });

    public MeViewModel(MeFragment fragment) {
        this.fragment = fragment;
        initUserInfo();
    }

    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
        builder.setMessage("是否退出登录").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doLogout();
                dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void doLogout() {
        RxUtil.execute(new IOTask<Boolean>() {
            @Override
            public Boolean run() {
                UserRepository.getInstance().logout();
                return true;
            }
        }, new UIAction<Boolean>() {
            @Override
            public void onComplete(Boolean aBoolean) {
                Intent intent = new Intent(fragment.getActivity(), UserLoginActivity.class);
                fragment.startActivity(intent);
                ((MainActivity)fragment.getActivity()).publishEvent(MainActivity.ACTION_USER_LOGOUT);
                initUserInfo();
//                fragment.getActivity().finish();
            }
        });
    }

    public void initUserInfo() {
        if (!UserRepository.getInstance().isLogin()) {
            userName.set("未登录");
            userHead.set("res:///"+ R.drawable.ic_head_default);
            return;
        }else {
            RxUtil.execute(new IOTask<UserInfo>() {
                @Override
                public UserInfo run() {
                    return UserRepository.getInstance().getMyInfo();
                }
            }, new UIAction<UserInfo>() {
                @Override
                public void onComplete(UserInfo userInfo) {
                    MeViewModel.this.myInfo = userInfo;
                    bindInfo();
                }
            });
        }
    }

    public void bindInfo() {
        userHead.set(myInfo.getUrl());
        userName.set(myInfo.getName());
    }
}