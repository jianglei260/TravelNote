package cn.edu.cuit.wsy.travelnote.data.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.cuit.wsy.travelnote.data.entity.Ask;
import cn.edu.cuit.wsy.travelnote.data.entity.UserInfo;
import cn.edu.cuit.wsy.travelnote.utils.LeanEngine;

/**
 * Created by jianglei on 2017/4/27.
 */

public class AskRepository {
    private static AskRepository instance;
    private Map<String, Ask> cache = new HashMap<>();

    public synchronized static AskRepository getInstance() {
        if (instance == null)
            instance = new AskRepository();
        return instance;
    }

    public boolean sendAsk(Ask ask) {
        return LeanEngine.save(ask);
    }

    public boolean deleteAsk(Ask ask) {
        return LeanEngine.delete(ask);
    }

    public boolean updateAsk(Ask ask, String content) {
        return LeanEngine.updateField(ask, "content", content);
    }

    public List<Ask> saveToCache(List<Ask> asks) {
        for (Ask ask : asks) {
            cache.put(ask.getObjectId(), ask);
        }
        return asks;
    }

    public Ask find(String objectId) {
        Ask ask = LeanEngine.Query.get(Ask.class).whereEqualTo("objectId", objectId).findFrist();
        if (ask != null && ask.getObjectId() != null) {
            cache.put(ask.getObjectId(), ask);
        }
        return ask;
    }

    public Ask findFromCahe(String objectId) {
        return cache.get(objectId);
    }

    public List<Ask> findAskByUser(UserInfo userInfo){
        return saveToCache(LeanEngine.Query.get(Ask.class).whereEqualTo("sender",userInfo).find());
    }
    public List<Ask> findAll() {
        return saveToCache(LeanEngine.Query.get(Ask.class).find());
    }

}
