package com.bean;

import com.utils.PropertiesUtils;

public class Constants {
    public static String AUTHOR_COMMENT;
    public static Boolean IGNORE_TABLE_PERFIX;
    public static String SUFFIX_BEAN_PARAM;
    public static String SUFFIX_BEAN_PARAM_FUZZY;
    public static String SUFFIX_BEAN_PARAM_TIME;
    public static String SUFFIX_BEAN_PARAM_START;
    public static String SUFFIX_BEAN_PARAM_END;
    public static String SUFFIX_MAPPERS;
    //忽略
    public static String IGNORE_BEAN_TOJSON_FIELD;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;

    //日期序列化和反
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;
    public static String BEAN_DATE_UNFORMAT_EXPRESSION;
    public static String BEAN_DATE_UNFORMAT_CLASS;
    public static String PATH_JAVA = "java";
    public static String PATH_RESOURCES = "resources";
    public static String PATH_BASE;
    public static String PATH_PO;
    public static String PATH_UTILS;
    public static String PATH_QUERY;
    public static String PATH_ENUMS;
    public static String PATH_MAPPERS;
    public static String PATH_SERVICE;
    public static String PATH_SERVICE_IMPL;
    public static String PATH_MAPPERS_XMLS;
    public static String PATH_VO;
    public static String PATH_DTO;
    public static String PATH_CONTROLLER;
    public static String PATH_EXCEPTION;
    public static String PACKAGE_BASE;
    public static String PACKAGE_QUERY;
    public static String PACKAGE_PO;
    public static String PACKAGE_UTILS;
    public static String PACKAGE_ENUMS;
    public static String PACKAGE_MAPPERS;
    public static String PACKAGE_SERVICE;
    public static String PACKAGE_SERVICE_IMPL;
    public static String PACKAGE_VO;
    public static String PACKAGE_EXCEPTION;
    public static String PACKAGE_CONTROLLER;
    public static String PACKAGE_DTO;

    static {

        AUTHOR_COMMENT = PropertiesUtils.getString("auther.comment");

        IGNORE_BEAN_TOJSON_FIELD = PropertiesUtils.getString("ignore.bean.tojson.field");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("ignore.bean.tojson.class");

        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");
        BEAN_DATE_UNFORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.unformat.expression");
        BEAN_DATE_UNFORMAT_CLASS = PropertiesUtils.getString("bean.date.unformat.class");

        IGNORE_TABLE_PERFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_PARAM = PropertiesUtils.getString("suffix.bean.param");
        SUFFIX_BEAN_PARAM_FUZZY = PropertiesUtils.getString("suffix.bean.param.fuzzy");
        SUFFIX_BEAN_PARAM_TIME = PropertiesUtils.getString("suffix.bean.param.time");
        SUFFIX_BEAN_PARAM_START = PropertiesUtils.getString("suffix.bean.param.start");
        SUFFIX_BEAN_PARAM_END = PropertiesUtils.getString("suffix.bean.param.end");
        SUFFIX_MAPPERS = PropertiesUtils.getString("suffix.mapper");

        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.po");
        PACKAGE_QUERY = PACKAGE_BASE + "." + PropertiesUtils.getString("package.query");
        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.utils");
        PACKAGE_ENUMS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.enums");
        PACKAGE_MAPPERS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.mappers");
        PACKAGE_SERVICE = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service");
        PACKAGE_SERVICE_IMPL = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service.impl");
        PACKAGE_VO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.vo");
        PACKAGE_EXCEPTION = PACKAGE_BASE + "." + PropertiesUtils.getString("package.exception");
        PACKAGE_CONTROLLER = PACKAGE_BASE + "." + PropertiesUtils.getString("package.controller");
        PACKAGE_DTO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.dto");

        PATH_BASE = PropertiesUtils.getString("path.base");
        PATH_BASE = PATH_BASE + PATH_JAVA ;

        PATH_PO = PATH_BASE + "/" + PACKAGE_PO.replace(".", "/");
        PATH_QUERY = PATH_BASE + "/" + PACKAGE_QUERY.replace(".", "/");
        PATH_UTILS = PATH_BASE + "/" + PACKAGE_UTILS.replace(".", "/");
        PATH_ENUMS = PATH_BASE + "/" + PACKAGE_ENUMS.replace(".", "/");
        PATH_MAPPERS = PATH_BASE + "/" + PACKAGE_MAPPERS.replace(".", "/");
        PATH_SERVICE = PATH_BASE + "/" + PACKAGE_SERVICE.replace(".", "/");
        PATH_SERVICE_IMPL = PATH_BASE + "/" + PACKAGE_SERVICE_IMPL.replace(".", "/");
        PATH_VO =  PATH_BASE + "/" + PACKAGE_VO.replace(".", "/");
        PATH_EXCEPTION = PATH_BASE + "/" + PACKAGE_EXCEPTION.replace(".", "/");
        PATH_CONTROLLER = PATH_BASE + "/" + PACKAGE_CONTROLLER.replace(".", "/");
        PATH_DTO = PATH_BASE + "/" + PACKAGE_DTO.replace(".", "/");

        PATH_MAPPERS_XMLS = PropertiesUtils.getString("path.base");
        PATH_MAPPERS_XMLS = PATH_MAPPERS_XMLS + PATH_RESOURCES + "/" + PACKAGE_MAPPERS.replace(".", "/");

    }

    public static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
    public static String[] SQL_DATE_TYPES = new String[]{"date"};
    public static String[] SQL_DECIMAL_TYPES = new String[]{"decimal", "double", "float"};
    public static String[] SQL_STRING_TYPES = new String[]{"varchar", "char", "text", "mediumtext", "longtext"};
    public static String[] SQL_INTEGER_TYPES = new String[]{"int", "tinyint", "smallint", "bit"};
    public static String[] SQL_LONG_TYPES = new String[]{"bigint"};

}
