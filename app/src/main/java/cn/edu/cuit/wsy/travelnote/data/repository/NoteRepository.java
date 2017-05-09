package cn.edu.cuit.wsy.travelnote.data.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.cuit.wsy.travelnote.data.entity.Note;
import cn.edu.cuit.wsy.travelnote.data.entity.UserInfo;
import cn.edu.cuit.wsy.travelnote.utils.LeanEngine;

/**
 * Created by jianglei on 2017/4/27.
 */

public class NoteRepository {
    private static NoteRepository instance;
    private Map<String, Note> cache = new HashMap<>();

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

    public List<Note> saveToCache(List<Note> notes) {
        for (Note note : notes) {
            cache.put(note.getObjectId(), note);
        }
        return notes;
    }

    public Note findFromCache(String objectId) {
        return cache.get(objectId);
    }

    public Note find(String objectId) {
        return LeanEngine.Query.get(Note.class).whereEqualTo("objectId", objectId).findFrist();
    }

    public boolean deleteNote(Note note) {
        return LeanEngine.delete(note);
    }

    public List<Note> findAllNote() {
        return saveToCache(LeanEngine.Query.get(Note.class).find());
    }
    public List<Note> findNoteByUser(UserInfo userInfo) {
        return saveToCache(LeanEngine.Query.get(Note.class).whereEqualTo("sender",userInfo).find());
    }

    public List<Note> findNoteByCity(String city) {
        return saveToCache(LeanEngine.Query.get(Note.class).whereEqualTo("city", city).find());
    }
}
