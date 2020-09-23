package com.jdk.readmap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReadHashMap {
    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("","");
        try {
            ReadHashMap.readMap();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    private static void readMap() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        HashMap<String, String> map = new HashMap<String, String>(3);
        map.put("hollis", "hollischuang");
        map.putIfAbsent("","");
        map.putAll(new HashMap<>());
        map.remove(null);
        Class<?> mapType = map.getClass();
        Method capacity = mapType.getDeclaredMethod("capacity");
        capacity.setAccessible(true);
        System.out.println("capacity : " + capacity.invoke(map));

        Field size = mapType.getDeclaredField("size");
        size.setAccessible(true);
        System.out.println("size : " + size.get(map));
    }
}
