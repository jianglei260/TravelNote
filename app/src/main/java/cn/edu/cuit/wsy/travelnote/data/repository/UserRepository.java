package cn.edu.cuit.wsy.travelnote.data.repository;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.cuit.wsy.travelnote.data.entity.User;
import cn.edu.cuit.wsy.travelnote.data.entity.UserInfo;
import cn.edu.cuit.wsy.travelnote.utils.LeanEngine;


/**
 * Created by jianglei on 2017/4/15.
 */

public class UserRepository {
    private static UserRepository instance;
    private UserInfo myInfo;
    private Map<String, UserInfo> cache = new HashMap<>();

    public synchronized static UserRepository getInstance() {
        if (instance == null)
            instance = new UserRepository();
        return instance;
    }

    public User login(String userName, String password) {
        try {
            return AVUser.logIn(userName, password, User.class);
        } catch (AVException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User signUp(String userName, String password) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        UserInfo userInfo = new UserInfo();
        userInfo.setName(userName);
        userInfo.setNick(userName);
        user.setInfo(LeanEngine.toAVObject(userInfo));
        try {
            user.signUp();
            return user;
        } catch (AVException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserInfo getUserInfo() {
        return LeanEngine.getUserInfo(AVUser.getCurrentUser(User.class));
    }

    public boolean save(UserInfo userInfo) {
        return LeanEngine.save(userInfo);
    }

    public UserInfo getUserInfo(User user) {
        return saveToCache(Arrays.asList(LeanEngine.getUserInfo(user))).get(0);
    }

    public List<UserInfo> findAll() {
        return saveToCache(LeanEngine.Query.get(UserInfo.class).find());
    }

    public UserInfo getMyInfo() {
        if (myInfo != null)
            return myInfo;
        myInfo = getUserInfo(getCurrentUser());
        return myInfo;
    }

    public UserInfo findFromCache(String objectId) {
        return cache.get(objectId);
    }

    public List<UserInfo> saveToCache(List<UserInfo> infos) {
        for (UserInfo info : infos) {
            cache.put(info.getObjectId(), info);
        }
        return infos;
    }

    public boolean editInfo(UserInfo info, String field, Object value) {
        if (LeanEngine.equeal(info, getMyInfo())) {
            return LeanEngine.updateField(info, field, value);
        } else {
            return false;
        }
    }

    public void logout() {
        AVUser.logOut();
        myInfo = null;
    }


    public User getCurrentUser() {
        return AVUser.getCurrentUser(User.class);
    }

    public boolean isLogin() {
        return getCurrentUser() != null ? true : false;
    }
}
