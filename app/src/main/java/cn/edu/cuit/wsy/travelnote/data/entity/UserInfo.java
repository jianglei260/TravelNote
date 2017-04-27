package cn.edu.cuit.wsy.travelnote.data.entity;

import java.util.List;

/**
 * Created by jianglei on 2017/4/25.
 */

public class UserInfo {
    protected String name;//用户名
    private String head;//编号
    private String objectId;//对象id
    private String createdAt;//对象创建时间
    private String updatedAt;//对象更新时间

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}
