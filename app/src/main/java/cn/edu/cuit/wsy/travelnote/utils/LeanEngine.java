package cn.edu.cuit.wsy.travelnote.utils;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.edu.cuit.wsy.travelnote.data.entity.User;
import cn.edu.cuit.wsy.travelnote.data.entity.UserInfo;


/**
 * Created by jianglei on 2017/4/25.
 */

public class LeanEngine {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Entity {
    }

    private static final String TAG = "LeanEngine";

    public static <T> AVObject toAVObject(T t) {
        Class clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        AVObject avObject = new AVObject(clazz.getSimpleName());
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldObject = field.get(t);
                if (fieldObject instanceof Collection) {
                    if (((Collection) fieldObject).size() <= 0) {
                        continue;
                    }
                    AVRelation relation = avObject.getRelation(field.getName());
                    for (Object o : ((Collection) fieldObject)) {
                        if (o == null)
                            continue;
                        AVObject object = toAVObject(o);
                        object.save();
                        updateObjectId(object,o);
                        relation.add(object);
                    }
                } else if (putAsBuiltIn(field, t, avObject)) {
                    continue;
                } else if (field.getType().isEnum()) {
                    putEnum(field, t, avObject);
                } else if (putAsPrimiteType(field, t, avObject)) {
                    continue;
                } else if (field.getType().getAnnotation(Entity.class) != null) {
                    Object o = field.get(t);
                    if (o == null)
                        continue;
                    avObject.put(field.getName(), toAVObject(o));
                } else {
                    Object o = field.get(t);
                    if (o == null)
                        continue;
                    avObject.put(field.getName(), o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avObject;
    }

    public static <T> void updateObjectId(AVObject avObject, T t) {
        try {
            Method method = t.getClass().getMethod("setObjectId",String.class);
            method.invoke(t, avObject.getObjectId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> boolean insertToList(T t, String name, Object value) {
        try {
            AVObject avObject = toAVObjectWithoutData(t);
            AVObject object = toAVObject(value);
            object.save();
            updateObjectId(object,value);
            avObject.getRelation(name).add(object);
            avObject.save();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T> boolean removeFromList(T t, String name, List values) {
        try {
            AVObject avObject = toAVObjectWithoutData(t);
            for (Object value : values) {
                avObject.getRelation(name).remove(toAVObjectWithoutData(value));
            }
            avObject.save();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T> AVObject toAVObjectWithoutData(T t) {
        try {
            Method method = t.getClass().getMethod("getObjectId");
            String objectId = (String) method.invoke(t);
            AVObject avObject = AVObject.createWithoutData(t.getClass().getSimpleName(), objectId);
            return avObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AVObject.create(t.getClass().getSimpleName());
    }

    public static <T> boolean updateField(T t, String name, Object value) {
        try {
            AVObject avObject = toAVObjectWithoutData(t);
            Field field = t.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(t,value);
            if (value instanceof AVObject) {
                avObject.put(name, value);
            } else if (value instanceof List) {
                List list = (List) value;
                for (Object o : list) {
                    AVObject object = toAVObject(o);
                    object.save();
                    updateObjectId(object,o);
                    avObject.getRelation(name).add(object);
                }
            } else if (putAsBuiltIn(field, t, avObject)) {
            } else if (field.getType().isEnum()) {
                putEnum(field, t, avObject);
            } else if (putAsPrimiteType(field, t, avObject)) {
            } else {
                avObject.put(name, toAVObject(value));
            }
            avObject.save();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static <T> boolean putAsPrimiteType(Field field, T object, AVObject avObject) {
        String type = field.getType().getName();
        try {
            if (type.equals(String.class.getName())) {
                String value = "";
                if (field.get(object) != null) {
                    value = (String) field.get(object);
                }
                avObject.put(field.getName(), value);
                return true;
            }
            if (type.equals("int") || type.equals(Integer.class.getName())) {
                avObject.put(field.getName(), field.get(object));
                return true;
            }
            if (type.equals("float") || type.equals(Float.class.getName())) {
                avObject.put(field.getName(), field.get(object));
                return true;
            }
            if (type.equals("double") || type.equals(Double.class.getName())) {
                avObject.put(field.getName(), field.get(object));
                return true;
            }
            if (type.equals("long") || type.equals(Long.class.getName())) {
                avObject.put(field.getName(), field.get(object));
                return true;
            }
            if (type.equals("boolean") || type.equals(Boolean.class.getName())) {
                avObject.put(field.getName(), field.get(object));
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static <T> boolean putAsBuiltIn(Field field, T t, AVObject avObject) {
        try {
            if (field.getName().equals("objectId")) {
                avObject.setObjectId((String) field.get(t));
                return true;
            }
            if (field.getName().equals("createdAt")) {
                return true;
            }
            if (field.getName().equals("updatedAt")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T> T toObject(AVObject avObject, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    if (List.class.isAssignableFrom(field.getType())) {
                        Log.d(TAG, "toObject: " + field.getName());
                        Type genericType = field.getGenericType();
                        List<AVObject> results = avObject.getRelation(field.getName()).getQuery().find();
                        List list = new ArrayList();
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) genericType;
                            Class genericClass = (Class) parameterizedType.getActualTypeArguments()[0];
                            for (AVObject result : results) {
                                list.add(toObject(result, genericClass));
                            }
                        }
                        field.set(t, list);
                    } else if (handleBuildInField(field, t, avObject)) {
                        continue;
                    } else if (field.getType().isEnum()) {
                        handleEnum(field, t, avObject);
                    } else if (!handleAsPrimiteType(field, t, avObject)) {
                        AVObject fieldAVObject = avObject.getAVObject(field.getName());
                        fieldAVObject.fetchIfNeeded();
                        field.set(t, toObject(fieldAVObject, field.getType()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Query<T> {
        private AVQuery<AVObject> avQuery;
        private Class<T> clazz;

        public static <T> Query<T> get(Class<T> clazz) {
            Query<T> query = new Query<>();
            query.avQuery = AVQuery.getQuery(clazz.getSimpleName());
            query.clazz = clazz;
            return query;
        }

        public Query<T> whereLessThan(String key, Object value) {
            avQuery.whereLessThan(key, value);
            return this;
        }

        public Query<T> whereGreaterThan(String key, Object value) {
            avQuery.whereGreaterThan(key, value);
            return this;
        }

        public Query<T> whereEqualTo(String key, Object value) {
            try {
                if (value.getClass().getAnnotation(Entity.class) != null) {
                    avQuery.whereEqualTo(key, toAVObjectWithoutData(value));
                } else {
                    avQuery.whereEqualTo(key, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public static <T> Query<T> and(Query<T>... querys) {
            Query<T> query = new Query<>();
            List<AVQuery<AVObject>> avQueries = new ArrayList<>(querys.length);
            for (Query<T> childQuery : querys) {
                avQueries.add(childQuery.avQuery);
            }
            query.avQuery = AVQuery.and(avQueries);
            return query;
        }

        public T findFrist() {
            try {
                AVObject objects = avQuery.getFirst();
                if (objects != null) {
                    return toObject(objects, clazz);
                }
            } catch (AVException e) {
                e.printStackTrace();
            }
            return null;
        }

        public List<T> find() {
            try {
                List<AVObject> objects = avQuery.find();
                if (objects != null) {
                    List<T> results = new ArrayList<>();
                    for (AVObject avObject : objects) {
                        results.add(toObject(avObject, clazz));
                    }
                    return results;
                }
            } catch (AVException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

    public static UserInfo getUserInfo(User user) {
        try {
            user.fetch();
            AVObject object = user.getInfo();
            object.fetchIfNeeded();
            return toObject(object, UserInfo.class);
        } catch (AVException e) {
            e.printStackTrace();
        }
        return new UserInfo();
    }

    public static <T> boolean save(T t) {
        try {
            AVObject avObject=toAVObject(t);
            avObject.save();
            updateObjectId(avObject,t);
            return true;
        } catch (AVException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void putEnum(Field field, Object object, AVObject avObject) {
        try {
            avObject.put(field.getName(), ((Enum) field.get(object)).ordinal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleEnum(Field field, Object object, AVObject avObject) {
        try {
            Method method = Enum.class.getDeclaredMethod("getSharedConstants", Class.class);
            method.setAccessible(true);
            Object[] objects = (Object[]) method.invoke(null, field.getType());
            field.set(object, objects[avObject.getInt(field.getName())]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> boolean delete(T t) {
        try {
            toAVObject(t).delete();
            return true;
        } catch (AVException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T> boolean equeal(T t1, T t2) {
        if (t1.equals(t2)) {
            return true;
        }
        try {
            Method method = t1.getClass().getMethod("getObjectId");
            if (method != null) {
                String objectId = (String) method.invoke(t1);
                String objectId1 = (String) method.invoke(t2);
                if (objectId.equals(objectId1)) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T> boolean remove(List<T> list, T t) {
        for (T t1 : list) {
            if (t1.equals(t)) {
                list.remove(t1);
                return true;
            }
        }
        return false;
    }


    private static <T> boolean handleBuildInField(Field field, T object, AVObject avObject) {
        try {
            String name = field.getName();
            if (name.equals("objectId")) {
                field.set(object, avObject.getObjectId());
                return true;
            }
            if (name.equals("createdAt")) {
                field.set(object, String.valueOf(avObject.getCreatedAt().getTime()));
                return true;
            }
            if (name.equals("updatedAt")) {
                field.set(object, String.valueOf(avObject.getCreatedAt().getTime()));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean handleAsPrimiteType(Field field, Object object, AVObject avObject) {
        String type = field.getType().getName();
        try {
            if (type.equals(String.class.getName())) {
                field.set(object, avObject.getString(field.getName()));
                return true;
            }
            if (type.equals("int") || type.equals(Integer.class.getName())) {
                field.set(object, avObject.getInt(field.getName()));
                return true;
            }
            if (type.equals("float") || type.equals(Float.class.getName())) {
                field.set(object, avObject.getDouble(field.getName()));
                return true;
            }
            if (type.equals("double") || type.equals(Double.class.getName())) {
                field.set(object, avObject.getDouble(field.getName()));
                return true;
            }
            if (type.equals("long") || type.equals(Long.class.getName())) {
                field.set(object, avObject.getLong(field.getName()));
                return true;
            }
            if (type.equals("boolean") || type.equals(Boolean.class.getName())) {
                field.set(object, avObject.getBoolean(field.getName()));
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
