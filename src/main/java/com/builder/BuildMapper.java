package com.builder;

import com.bean.Constants;
import com.bean.FieldInfo;
import com.bean.TableInfo;
import com.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuildMapper {
    public static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPERS);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
        File file = new File(folder, className + ".java");
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

            bw.write("package "+ Constants.PACKAGE_MAPPERS + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Mapper");
            bw.write("public interface " + className + "<T, P> extends BaseMapper {");
            bw.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> entry: keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldList = entry.getValue();

                StringBuffer methodName = new StringBuffer();
                StringBuffer methodParmas = new StringBuffer();

                for (int i = 0; i < keyFieldList.size(); i++) {
                    String propertyName = keyFieldList.get(i).getPropertyName();

                    methodName.append(StringUtils.upperCaseFirstLetter(propertyName));
                    methodParmas.append("@Param(\"" + propertyName + "\") " + keyFieldList.get(i).getJavaType()
                        + " "+ propertyName);

                    if (i < keyFieldList.size()-1) {
                        methodName.append("And");
                        methodParmas.append(", ");
                    }
                }
                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\tT selectBy" + methodName + "(" + methodParmas +");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger updateBy" + methodName + "(@Param(\"bean\") T t, " + methodParmas +");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger deleteBy" + methodName + "(" + methodParmas +");");
                bw.newLine();
                bw.newLine();

            }

            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建Mapper失败", e);
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
