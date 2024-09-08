package com.builder;

import com.bean.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BuildBase {
    private static Logger logger = LoggerFactory.getLogger(BuildBase.class);
    public static void execute() {
        List<String> headerInfoList = new ArrayList<>();

        headerInfoList.add("package " + Constants.PACKAGE_ENUMS);
        build(headerInfoList, "DateTimePatternEnum", Constants.PATH_ENUMS);
        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_ENUMS);
        build(headerInfoList, "PageSize", Constants.PATH_ENUMS);
        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_UTILS);
        build(headerInfoList, "DateUtils", Constants.PATH_UTILS);
        headerInfoList.clear();

//        headerInfoList.add("package " + Constants.PACKAGE_UTILS);
//        build(headerInfoList, "CopyTools", Constants.PATH_UTILS);
//        headerInfoList.clear();

//        headerInfoList.add("package " + Constants.PACKAGE_UTILS);
//        build(headerInfoList, "StringTools", Constants.PATH_UTILS);
//        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_MAPPERS);
        build(headerInfoList, "BaseMapper", Constants.PATH_MAPPERS);
        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_QUERY);
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".PageSize");
        build(headerInfoList, "SimplePage", Constants.PATH_QUERY);
        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_QUERY);
        build(headerInfoList, "BaseParam", Constants.PATH_QUERY);
        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_VO);
        build(headerInfoList, "PaginationResultVO", Constants.PATH_VO);
        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_VO);
        build(headerInfoList, "ResponseVO", Constants.PATH_VO);
        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_EXCEPTION);
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum");
        build(headerInfoList, "BusinessException", Constants.PATH_EXCEPTION);
        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_ENUMS);
        build(headerInfoList, "ResponseCodeEnum", Constants.PATH_ENUMS);
        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum");
//        headerInfoList.add("import " + Constants.PACKAGE_VO + ".PaginationResultVO");
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVO");
//        headerInfoList.add("import " + Constants.PACKAGE_UTILS + ".CopyTools");
        headerInfoList.add("import " + Constants.PACKAGE_EXCEPTION + ".BusinessException");
        build(headerInfoList, "ABaseController", Constants.PATH_CONTROLLER);
        headerInfoList.clear();

//        headerInfoList.add("package " + Constants.PACKAGE_DTO);
//        build(headerInfoList, "SessionWebUserDto", Constants.PATH_DTO);
//        headerInfoList.clear();

        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVO");
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum");
        headerInfoList.add("import " + Constants.PACKAGE_EXCEPTION + ".BusinessException");
        build(headerInfoList, "AGlobalExceptionHandlerController", Constants.PATH_CONTROLLER);
        headerInfoList.clear();
    }

    private static void build(List<String> headerInfoList,String fileName, String outPutPath) {
        File folder = new File(outPutPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File javaFile = new File(outPutPath, fileName + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader bf = null;
        try {
            out = new FileOutputStream(javaFile);
            outw = new OutputStreamWriter(out, "utf-8");
            bw = new BufferedWriter(outw);

            String templatePath = BuildBase.class.getClassLoader().getResource((
                    "template/"+ fileName + ".txt"
                    )).getPath();

            in = new FileInputStream(templatePath);
            inr = new InputStreamReader(in, "utf-8");
            bf = new BufferedReader(inr);

            for (String head: headerInfoList) {
                bw.write(head + ";");
                bw.newLine();
                bw.newLine();
            }

            String lineInfo = null;
            while ((lineInfo = bf.readLine()) != null) {
                bw.write(lineInfo);
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {
            logger.error("生成基础类：{}，失败", e);
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inr != null) {
                try {
                    inr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
