package cn.edu.cuit.wsy.travelnote.data.entity;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * Created by jianglei on 2017/4/25.
 */

public class User extends AVUser {
    private UserInfo info;

    public AVObject getInfo() {
        return getAVObject("info");
    }

    public void setInfo(AVObject info) {
        put("info", info);
    }
}
