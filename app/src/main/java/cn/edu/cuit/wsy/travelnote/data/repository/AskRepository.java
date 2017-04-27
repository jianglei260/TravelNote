package cn.edu.cuit.wsy.travelnote.data.repository;

import java.util.List;

import cn.edu.cuit.wsy.travelnote.data.entity.Ask;
import cn.edu.cuit.wsy.travelnote.utils.LeanEngine;

/**
 * Created by jianglei on 2017/4/27.
 */

public class AskRepository {
    private static AskRepository instance;

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

    public List<Ask> findAll() {
        return LeanEngine.Query.get(Ask.class).find();
    }

}
