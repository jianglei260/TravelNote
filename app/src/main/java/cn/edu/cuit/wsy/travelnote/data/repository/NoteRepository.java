package cn.edu.cuit.wsy.travelnote.data.repository;

import java.util.List;

import cn.edu.cuit.wsy.travelnote.data.entity.Note;
import cn.edu.cuit.wsy.travelnote.utils.LeanEngine;

/**
 * Created by jianglei on 2017/4/27.
 */

public class NoteRepository {
    private static NoteRepository instance;

    public static NoteRepository getInstance() {
        if (instance == null)
            instance = new NoteRepository();
        return instance;
    }

    public boolean sendNote(Note note) {
        return LeanEngine.save(note);
    }

    public boolean updateNote(Note note, String content) {
        return LeanEngine.updateField(note, "content", content);
    }

    public boolean deleteNote(Note note) {
        return LeanEngine.delete(note);
    }

    public List<Note> findAllNote() {
        return LeanEngine.Query.get(Note.class).find();
    }
}
