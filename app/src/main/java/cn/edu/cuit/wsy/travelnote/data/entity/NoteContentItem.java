package cn.edu.cuit.wsy.travelnote.data.entity;

/**
 * Created by jianglei on 2017/5/7.
 */

public class NoteContentItem {
    public static final String TYPE_TEXT="text";
    public static final String TYPE_IMAGE="image";
    private String type;
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
