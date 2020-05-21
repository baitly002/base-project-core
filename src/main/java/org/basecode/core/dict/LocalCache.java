package org.basecode.core.dict;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LocalCache {

    //字典的详细信息
    public static Cache<String, List<DictInfo>> dictCache = Caffeine.newBuilder()
            .expireAfterWrite(Long.MAX_VALUE, TimeUnit.DAYS)
            .maximumSize(10_000)
            .build();

    //可以根据value获取到name  value为字典值, name为字典含义
    public static Cache<String, Map<Object, Object>> nameCache = Caffeine.newBuilder()
            .expireAfterWrite(Long.MAX_VALUE, TimeUnit.DAYS)
            .maximumSize(10_000)
            .build();

    //可以根据name获取到value  name为字典含义, value为字典值
    public static Cache<String, Map<Object, Object>> valueCache = Caffeine.newBuilder()
            .expireAfterWrite(Long.MAX_VALUE, TimeUnit.DAYS)
            .maximumSize(10_000)
            .build();

    public static String versionId = "";

    public static DictGroupInfo dictGroups = null;

}
