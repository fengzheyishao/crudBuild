package com.utils;


import org.apache.ibatis.annotations.Param;

import java.util.List;

interface testUtils<T, P> {
    /**
    插入
     */
    Integer insert(@Param("bean") T t);
    /**
     插入
     */
    Integer insertOrUpdate(@Param("bean") T t);
    /**
     批量插入
     */
    Integer insertBatch(@Param("List") List<T> t);
    /**
     批量插入或更新
     */
    Integer insertOrUpdateBatch(@Param("List") List<T> t);
    /**
     查询
     */
    List<T> selectList(@Param("query") P p);
    /**
     查询数量
     */
    Integer selectCount(@Param("query") P p);
}
