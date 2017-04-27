package cn.edu.cuit.wsy.travelnote.base;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;

import cn.edu.cuit.wsy.travelnote.data.entity.User;

/**
 * Created by jianglei on 2017/4/27.
 */

public class NoteApplication extends Application {
    private static NoteApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AVUser.alwaysUseSubUserClass(User.class);
        AVOSCloud.initialize(this, "6CNxGgkPeMeOw1l7DGwtYqwC-gzGzoHsz", "EKYUYoGrjhAwYphvCqvSuRcE");
    }

    public static NoteApplication getInstance() {
        return instance;
    }
}
