package com.builder;

import com.bean.Constants;
import com.bean.FieldInfo;
import com.bean.TableInfo;
import com.utils.PropertiesUtils;
import com.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.*;
import java.util.*;

public class BuildTable {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(BuildTable.class);
    private static Connection conn = null;
    private static String SQL_SHOW_TABLE_STATUS = "show table status";
    private static String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";
    private static String SQL_SHOW_INDEX = "show index from %s";

    static {
        String dirverName = PropertiesUtils.getString("spring.datasource.driver-class-name");
        String url = PropertiesUtils.getString("spring.datasource.url");
        String user = PropertiesUtils.getString("spring.datasource.username");
        String password = PropertiesUtils.getString("spring.datasource.password");
        try {
            Class.forName(dirverName);
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            logger.error("连接数据库失败", e);
        }
    }

    public static List<TableInfo> getTable() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            rs = ps.executeQuery();
            while (rs.next()) {
                String tableName = rs.getString("name");
                String comment = rs.getString("comment");

                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PERFIX) {
                    beanName = tableName.substring(beanName.indexOf("_")+1);
                }

                beanName = processField(beanName, true);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_PARAM);

                readFieldInfo(tableInfo);
                getKeyIndexInfo(tableInfo);

                tableInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            logger.error("读取表失败");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return tableInfoList;
        }
    }

    public static void readFieldInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<FieldInfo> list = new ArrayList<>();

        List<FieldInfo> fieldExtendList = new ArrayList<>();

        Boolean haveDate = false, haveBigDecimal = false, haveDateTime = false;
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS, tableInfo.getTableName()));
            rs = ps.executeQuery();
            while (rs.next()) {
                String field = rs.getString("field");
                String type = rs.getString("type");
                String extra = rs.getString("extra");
                String comment = rs.getString("comment");

                if (type.indexOf("(")>0) {
                    type = type.substring(0, type.indexOf("("));
                }
                String propertyName = processField(field, false);

                FieldInfo fieldInfo = new FieldInfo();
                list.add(fieldInfo);

                fieldInfo.setFieldName(field);
                fieldInfo.setComment(comment);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra)?true:false);
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(procressJavaType(type));
                fieldInfo.setSqlType(type);

                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)) {
                    haveDateTime = true;
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
                    haveDate = true;
                }
                if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
                    haveBigDecimal = true;
                }

                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)) {

                    FieldInfo fuzzyField = new FieldInfo();
                    fuzzyField.setJavaType(fieldInfo.getJavaType());
                    fuzzyField.setFieldName(fieldInfo.getFieldName());
                    fuzzyField.setSqlType(type);
                    fuzzyField.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_PARAM_FUZZY);
                    fieldExtendList.add(fuzzyField);
                }

                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type) ||
                        ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
                    FieldInfo timeStartField = new FieldInfo();
                    timeStartField.setJavaType("String");
                    timeStartField.setSqlType(type);
                    timeStartField.setFieldName(fieldInfo.getFieldName());
                    timeStartField.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_PARAM_START);
                    fieldExtendList.add(timeStartField);

                    FieldInfo timeEndField = new FieldInfo();
                    timeEndField.setJavaType("String");
                    timeEndField.setSqlType(type);
                    timeEndField.setFieldName(fieldInfo.getFieldName());
                    timeEndField.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_PARAM_END);
                    fieldExtendList.add(timeEndField);

                }
            }
            tableInfo.setHaveDate(haveDate);
            tableInfo.setHaveDateTime(haveDateTime);
            tableInfo.setHaveBigDecimal(haveBigDecimal);
            tableInfo.setFieldInfoList(list);
            tableInfo.setFieldExtendList(fieldExtendList);
        } catch (Exception e) {
            logger.error("读取表失败");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<FieldInfo> getKeyIndexInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<FieldInfo> list = new ArrayList<>();
        try {

            Map<String, FieldInfo> tempMap = new HashMap<>();
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                tempMap.put(fieldInfo.getFieldName(), fieldInfo);
            }

            ps = conn.prepareStatement(String.format(SQL_SHOW_INDEX, tableInfo.getTableName()));
            rs = ps.executeQuery();
            while (rs.next()) {
                String keyName = rs.getString("key_name");
                Integer nonUnique = rs.getInt("non_unique");
                String columnName = rs.getString("column_name");
                if (nonUnique == 1) {
                    continue;
                }
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);
                if (keyFieldList == null) {
                    keyFieldList = new ArrayList<>();
                    tableInfo.getKeyIndexMap().put(keyName, keyFieldList);
                }
//                for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
//                    if (fieldInfo.getFieldName().equals(columnName)) {
//                        keyFieldList.add(fieldInfo);
//                    }
//                }
                keyFieldList.add(tempMap.get(columnName));
            }
        } catch (Exception e) {
            logger.error("读取索引失败");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }
    }

    private static String processField(String field, Boolean upperCaseFirst) {
        StringBuilder sb = new StringBuilder();
        String[] fields = field.split("_");
        sb.append(upperCaseFirst? StringUtils.upperCaseFirstLetter(fields[0]) :fields[0]);
        for (int i = 1; i < fields.length; i++) {
            sb.append(StringUtils.upperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }

    private static String procressJavaType(String type) {
        if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPES, type)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPES, type)) {
            return "Long";
        } else if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)) {
            return "String";
        } else if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type) ||
                ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
            return "Date";
        } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
            return "BigDecimal";
        } else {
            throw new RuntimeException("无法识别的类型：" + type);
        }
    }

}
