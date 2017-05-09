package cn.edu.cuit.wsy.travelnote.data.repository;

import cn.edu.cuit.wsy.travelnote.data.entity.City;
import cn.edu.cuit.wsy.travelnote.utils.LeanEngine;

/**
 * Created by jianglei on 2017/5/9.
 */

public class CityRepository {
    private static CityRepository instance;

    public static CityRepository getInstance() {
        if (instance == null)
            instance = new CityRepository();
        return instance;
    }

    public City find(String name) {
        return LeanEngine.Query.get(City.class).whereEqualTo("name", name).findFrist();
    }
}
