package cn.edu.cuit.wsy.travelnote.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import cn.edu.cuit.wsy.travelnote.data.entity.NoteContentItem;

/**
 * Created by jianglei on 2017/5/7.
 */

public class NoteContentParser {
    public static Gson gson = new Gson();

    public static List<NoteContentItem> items(String content) {
        return gson.fromJson(content, new TypeToken<List<NoteContentItem>>() {
        }.getType());
    }

    public static String toString(List<NoteContentItem> items) {
        return gson.toJson(items);
    }

    public static String firstImage(String content) {
        for (NoteContentItem noteContentItem : items(content)) {
            if (noteContentItem.getType().equals(NoteContentItem.TYPE_IMAGE))
                if (!TextUtils.isEmpty(noteContentItem.getContent()))
                    return noteContentItem.getContent();
        }
        return "";
    }
}
