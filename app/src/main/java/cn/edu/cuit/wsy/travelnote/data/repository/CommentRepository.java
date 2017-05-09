package cn.edu.cuit.wsy.travelnote.data.repository;

import java.util.List;

import cn.edu.cuit.wsy.travelnote.data.entity.Ask;
import cn.edu.cuit.wsy.travelnote.data.entity.Comment;
import cn.edu.cuit.wsy.travelnote.data.entity.Note;
import cn.edu.cuit.wsy.travelnote.data.entity.UserInfo;
import cn.edu.cuit.wsy.travelnote.utils.LeanEngine;

/**
 * Created by jianglei on 2017/4/27.
 */

public class CommentRepository {
    private static CommentRepository instance;


    public static CommentRepository getInstance() {
        if (instance == null)
            instance = new CommentRepository();
        return instance;
    }

    public boolean commentAsk(Ask ask, Comment comment) {
        ask.getComments().add(comment);
        return LeanEngine.updateField(ask, "comments", ask.getComments());
    }

    public boolean commentComment(Comment beComment, Comment comment) {
        beComment.getComments().add(comment);
        return LeanEngine.insertToList(beComment, "comments", comment);
    }

    public boolean commentNote(Note note, Comment comment) {
        note.getComments().add(comment);
        return LeanEngine.insertToList(note, "comments", comment);
    }

    public boolean deleteComment(Comment comment, UserInfo userInfo) {
        if (LeanEngine.equeal(comment.getSender(), userInfo)) {
            return LeanEngine.delete(comment);
        }
        return false;
    }

    public Comment find(String objectId) {
        return LeanEngine.Query.get(Comment.class).whereEqualTo("objectId", objectId).findFrist();
    }

    public List<Comment> findCommentByUser(UserInfo info) {
        return LeanEngine.Query.get(Comment.class).whereEqualTo("sender", info).find();
    }

    public boolean editComment(Comment comment, String content, UserInfo userInfo) {
        if (LeanEngine.equeal(comment.getSender(), userInfo)) {
            return LeanEngine.updateField(comment, "content", content);
        }
        return false;
    }

    public boolean edit(Comment comment, String content) {
        return LeanEngine.updateField(comment, "content", content);
    }
}
