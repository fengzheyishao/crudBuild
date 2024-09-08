package com.builder;

import com.bean.Constants;
import com.utils.DateUtils;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class BuildComment {
    public static void createClassComment(BufferedWriter bw, String classComment) throws Exception {
        bw.write("/**");
        bw.newLine();
        bw.write(" * @Description: " + classComment);
        bw.newLine();
        bw.write(" * @auther: " + Constants.AUTHOR_COMMENT);
        bw.newLine();
        bw.write(" * @Date: " + DateUtils.format(new Date(), DateUtils._YYYYMMDD));
        bw.newLine();
        bw.write(" */");
        bw.newLine();
    }

    public static void createFieldComment(BufferedWriter bw, String fieldComment) throws Exception{
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * " + (fieldComment==null?"" : fieldComment));
        bw.newLine();
        bw.write("\t */");
        bw.newLine();
    }

    public static void createMethodComment() {

    }
}
