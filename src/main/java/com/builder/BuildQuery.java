package com.builder;

import com.bean.Constants;
import com.bean.FieldInfo;
import com.bean.TableInfo;
import com.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class BuildQuery {
    public static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_QUERY);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, tableInfo.getBeanName() + Constants.SUFFIX_BEAN_PARAM +".java");
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            outw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outw);

            bw.write("package "+ Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();

            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
            }

            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                if (ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FIELD.split(","), fieldInfo.getPropertyName())) {
                    bw.write(Constants.IGNORE_BEAN_TOJSON_CLASS);
                    bw.newLine();
                    break;
                }
            }

            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
            }

            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment()+"Query");
            bw.write("public class " + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_PARAM + " extends BaseParam{");
            bw.newLine();

            for (FieldInfo fieldInfo: tableInfo.getFieldInfoList()) {
                BuildComment.createFieldComment(bw, fieldInfo.getComment());
                bw.write("\tprivate "+ fieldInfo.getJavaType() +" " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();

                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\tprivate "+ fieldInfo.getJavaType() +" " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_PARAM_FUZZY + ";");
                    bw.newLine();
                    bw.newLine();
                }

                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType()) ||
                        ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_PARAM_START + ";");
                    bw.newLine();
                    bw.newLine();

                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_PARAM_END + ";");
                    bw.newLine();
                    bw.newLine();

                }
            }

            buildGetAndSet(bw, tableInfo.getFieldInfoList());
            buildGetAndSet(bw, tableInfo.getFieldExtendList());

            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建Query失败", e);
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

    public static void buildGetAndSet(BufferedWriter bw, List<FieldInfo> list) throws Exception {
        for (FieldInfo fieldInfo: list) {
            String tempField = StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName());
            bw.write("\tpublic void set" +  tempField + "(" + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() +") {");
            bw.newLine();
            bw.write("\t\tthis." + fieldInfo.getPropertyName() + " = " + fieldInfo.getPropertyName() + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            bw.write("\tpublic " + fieldInfo.getJavaType() + " get"+ tempField +" () {");
            bw.newLine();
            bw.write("\t\treturn this." + fieldInfo.getPropertyName() + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
        }
    }
}
