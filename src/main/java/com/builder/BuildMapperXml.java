package com.builder;

import com.bean.Constants;
import com.bean.FieldInfo;
import com.bean.TableInfo;
import com.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildMapperXml {
    public static final String BASE_COLUMN_LIST = "base_column_list";
    public static final String BASE_QUERY_CONDITION = "base_query_condition";
    public static final String QUERY_CONDITION = "query_condition";
    public static final String BASE_QUERY_CONDITION_EXTEND = "base_query_condition_extend";
    public static final String BASE_RESULT_MAP = "base_result_map";
    public static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPERS_XMLS);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String xmlName = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
        File file = new File(folder, xmlName + ".xml");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            outw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outw);

            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bw.newLine();
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"");
            bw.newLine();
            bw.write("\t\t\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            bw.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPERS + "." + xmlName + "\">");
            bw.newLine();
            bw.newLine();

            bw.write("\t<!--实体映射-->");
            bw.newLine();
            String poClass = Constants.PACKAGE_PO + "." + tableInfo.getBeanName();
            bw.write("\t<resultMap id=\"base_result_map\" type=\"" + poClass + "\">");
            bw.newLine();

            FieldInfo idField = null;
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> entry: keyIndexMap.entrySet()) {
                if ("PRIMARY".equals(entry.getKey())) {
                    List<FieldInfo> fieldInfoList = entry.getValue();
                    if (fieldInfoList.size() == 1) {
                        idField = fieldInfoList.get(0);
                        break;
                    }
                }
            }
            //resultMap
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                bw.write("\t\t<!--" + fieldInfo.getComment() + "-->");
                bw.newLine();
                String key = "";
                if (idField != null && fieldInfo.getPropertyName().equals(idField.getPropertyName())) {
                    key = "id";
                } else {
                    key = "result";
                }
                bw.write("\t\t<" + key + " column=\"" + fieldInfo.getFieldName() + "\" property=\""
                    + fieldInfo.getPropertyName() + "\"/>");
                bw.newLine();
            }

            bw.write("\t</resultMap>");
            bw.newLine();

            //查询列
            bw.newLine();
            bw.write("\t<!--通用查询结果列 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_COLUMN_LIST + "\">");
            bw.newLine();
            StringBuilder columnBuilder = new StringBuilder();
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                columnBuilder.append(fieldInfo.getFieldName()).append(",");
            }
            String colBuilderStr = columnBuilder.substring(0, columnBuilder.lastIndexOf(","));
            bw.write("\t\t" + colBuilderStr);
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            bw.newLine();
            bw.write("\t<!--基础查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION + "\">");
            bw.newLine();

            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                String stringQuery = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    stringQuery = "and query." + fieldInfo.getPropertyName() + "!=''";
                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + "!= null " + stringQuery + "\">");
                bw.newLine();
                bw.write("\t\t\tand " + fieldInfo.getPropertyName() + "= #{query." + fieldInfo.getPropertyName() + "}");
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }

            bw.write("\t</sql>");
            bw.newLine();

            bw.newLine();
            bw.write("\t<!--扩展的查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION_EXTEND + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo: tableInfo.getFieldExtendList()) {
                String andWhere = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    andWhere = "and " + fieldInfo.getPropertyName() + " like concat('%', #{query."
                            + fieldInfo.getPropertyName() + "}, '%')";
                } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())
                        || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_PARAM_END)) {
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " < date_sub(#{" + fieldInfo.getPropertyName()
                                + "}, 'Y-%m-%d') ]]>";
                    } else if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_PARAM_START)){
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " >= str_to_date(#{" + fieldInfo.getPropertyName()
                                + "}, 'Y-%m-%d'), interval -1 day ]]>";
                    }
                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null and query." + fieldInfo.getPropertyName()
                    + " !=''\">");
                bw.newLine();
                bw.write("\t\t\t" + andWhere);
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();

            bw.newLine();
            bw.write("\t<!--通用的查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\"" + QUERY_CONDITION + "\">");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION_EXTEND + "\"/>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            bw.newLine();
            bw.write("\t<!--查询列表-->");
            bw.newLine();

            bw.write("\t<select id=\"selectList\" resultMap=\"" + BASE_RESULT_MAP +"\">");
            bw.newLine();
            bw.write("\t\tSELECT");
            bw.newLine();
            bw.write("\t\t<include refid=\"" + BASE_COLUMN_LIST + "\"/>");
            bw.newLine();
            bw.write("\t\tFROM " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();

            bw.write("\t\t<if test=\"query.orderBy != null\">");
            bw.newLine();
            bw.write("\t\t\torder by ${query.orderBy}");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();

            bw.write("\t\t<if test=\"query.simplePage != null\">");
            bw.newLine();
            bw.write("\t\t\tlimit #{query.simplePage.start}, #{query.simplePage.end}");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();

            bw.write("\t</select>");
            bw.newLine();

            bw.newLine();
            bw.write("\t<!--查询数量-->");
            bw.newLine();

            bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Integer\">");
            bw.newLine();
            bw.write("\t\tSELECT count(1) FROM " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();

            bw.newLine();
            bw.write("\t<!--插入 (匹配有值字段)-->");
            bw.newLine();
            bw.write("\t<insert id=\"insert\"" + " parameterType=\"" + Constants.PACKAGE_PO
                    + "." + tableInfo.getBeanName() + "\">");
            bw.newLine();

            FieldInfo autoIncrementField = null;
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                if (fieldInfo.getAutoIncrement() != null && fieldInfo.getAutoIncrement()) {
                    autoIncrementField = fieldInfo;
                    break;
                }
            }

            if (autoIncrementField != null) {
                bw.write("\t\t<selectKey keyProperty=\"bean." + autoIncrementField.getPropertyName() + "\" resultType=\""
                        + autoIncrementField.getJavaType() + "\" order=\"AFTER\">");
                bw.newLine();
                bw.write("\t\t\tSELECT LAST_INSERT_ID()");
                bw.newLine();
                bw.write("\t\t</selectKey>");
                bw.newLine();
            }

            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();

            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();

            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }

            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            bw.newLine();
            bw.write("\t<!-- 插入或者更新 (匹配有值的字段) -->");
            bw.newLine();

            bw.write("\t<insert id=\"insertOrUpdate\"" + " parameterType=\"" + Constants.PACKAGE_PO
                    + "." + tableInfo.getBeanName() + "\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();

            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();

            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();

            bw.write("\t\ton DUPLICATE key update");
            bw.newLine();

            Set<String> keyIndexSet = new HashSet<>();
            for (Map.Entry<String, List<FieldInfo>> entry: tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                for (FieldInfo fieldInfo: fieldInfoList) {
                    keyIndexSet.add(fieldInfo.getFieldName());
                }
            }

            bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                if (keyIndexSet.contains(fieldInfo.getFieldName())) {
                    continue;
                }
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "),");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();

            bw.write("\t</insert>");
            bw.newLine();

            bw.newLine();
            bw.write("\t<!-- 添加(批量插入) -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertBatch\"" + " parameterType=\"" + Constants.PACKAGE_PO
                    + "." + tableInfo.getBeanName() + "\" >");
            bw.newLine();
            StringBuilder insertSb = new StringBuilder();
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                if (fieldInfo.getAutoIncrement()) {
                    continue;
                }
                insertSb.append(fieldInfo.getFieldName()).append(",");
            }
            String insertStr = insertSb.substring(0, insertSb.lastIndexOf(","));
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(" + insertStr + ") values");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item = \"item\" separator=\",\" >");
            bw.newLine();
            StringBuilder insertPropertySb = new StringBuilder();
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                if (fieldInfo.getAutoIncrement()) {
                    continue;
                }
                insertPropertySb.append("#{item.").append(fieldInfo.getFieldName()).append("},");
            }
            String insertPropertyStr = insertPropertySb.substring(0, insertPropertySb.lastIndexOf(","));
            bw.write("\t\t\t(" + insertPropertyStr + ")");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();

            bw.write("\t</insert>");
            bw.newLine();

            bw.newLine();
            bw.write("\t<!-- 批量新增修改(批量插入) -->");
            bw.newLine();

            bw.write("\t<insert id=\"insertOrUpdateBatch\"" + " parameterType=\"" + Constants.PACKAGE_PO
                    + "." + tableInfo.getBeanName() + "\" >");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(" + insertStr + ") values");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\" >");
            bw.newLine();
            bw.write("\t\t\t(" + insertPropertyStr + ")");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();

            bw.write("\t\ton DUPLICATE key update");
            bw.newLine();
            int index = 0;
            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                bw.write("\t\t" + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + ")");
                if (index++ < tableInfo.getFieldInfoList().size()-1) bw.write(", ");
                bw.newLine();

            }

            bw.write("\t</insert>");
            bw.newLine();

            for (Map.Entry<String, List<FieldInfo>> entry: keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldList = entry.getValue();

                StringBuffer methodName = new StringBuffer();
                StringBuffer methodParmas = new StringBuffer();

                for (int i = 0; i < keyFieldList.size(); i++) {
                    String propertyName = keyFieldList.get(i).getPropertyName();

                    methodName.append(StringUtils.upperCaseFirstLetter(propertyName));
                    methodParmas.append(keyFieldList.get(i).getFieldName() + "=#{" + keyFieldList.get(i).getPropertyName() + "}");

                    if (i < keyFieldList.size()-1) {
                        methodName.append("And");
                        methodParmas.append(" and ");
                    }
                }

                bw.newLine();
                bw.write("\t<!-- 根据" + methodName +"查询 -->");
                bw.newLine();
                bw.write("\t<select id=\"selectBy" + methodName + "\" resultMap=\""+ BASE_RESULT_MAP +"\">");
                bw.newLine();
                bw.write("\t\tselect <include refid=\"" + BASE_COLUMN_LIST +"\"/>" + " from " + tableInfo.getTableName()
                        + " where " + methodParmas);
                bw.newLine();
                bw.write("\t</select>");
                bw.newLine();

                bw.newLine();
                bw.write("\t<!-- 根据Id修改 -->");
                bw.newLine();
                bw.write("\t<update id=\"updateBy" + methodName +"\" parameterType=\"" + Constants.PACKAGE_PO
                        + "." + tableInfo.getBeanName() + "\">");
                bw.newLine();
                bw.write("\t\tUPDATE " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\t<set>");
                bw.newLine();
                for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + "!=null\">");
                    bw.newLine();
                    bw.write("\t\t\t\t"+fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getPropertyName() + "},");
                    bw.newLine();
                    bw.write("\t\t\t</if>");
                    bw.newLine();
                }
                bw.write("\t\t</set>");
                bw.newLine();
                bw.write("\t\twhere " + methodParmas);
                bw.newLine();
                bw.write("\t</update>");
                bw.newLine();

                bw.newLine();
                bw.write("\t<!-- 根据" + methodName +"删除 -->");
                bw.newLine();
                bw.write("\t<delete id=\"deleteBy" + methodName + "\">");
                bw.newLine();
                bw.write("\t\tDELETE FROM " + tableInfo.getTableName() + " where " + methodParmas);
                bw.newLine();
                bw.write("\t</delete>");
                bw.newLine();

            }

            bw.newLine();
            bw.write("</mapper>");
            bw.flush();
        } catch (Exception e) {
                logger.error("创建mapperXml失败", e);
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outw != null) {
                    try {
                        outw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
}
