package cn.edu.cuit.wsy.travelnote.data.entity;

import com.avos.avoscloud.AVGeoPoint;

import java.util.ArrayList;
import java.util.List;

import cn.edu.cuit.wsy.travelnote.utils.LeanEngine;

/**
 * Created by jianglei on 2017/4/25.
 */
@LeanEngine.Entity
public class Note {
    /**
     * 游记内容
     */
    private String content;//游记内容
    private String title;//游记标题
    private String city;//城市
    private UserInfo sender;//游记发布者
    private List<Comment> comments=new ArrayList<>();//游记评论
    private String objectId;//对象id
    private String createdAt;//对象创建时间
    private String updatedAt;//对象更新时间
    private AVGeoPoint location;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public AVGeoPoint getLocation() {
        return location;
    }

    public void setLocation(AVGeoPoint location) {
        this.location = location;
    }
}
