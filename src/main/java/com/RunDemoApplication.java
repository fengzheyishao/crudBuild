package com;

import com.bean.TableInfo;
import com.builder.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class RunDemoApplication {
    public static void main(String[] args) {
        List<TableInfo> tableInfoList = BuildTable.getTable();
        BuildBase.execute();
//        SpringApplication.run(RunDemoApplication.class, args);
        for (TableInfo tableInfo: tableInfoList) {
            BuildPo.execute(tableInfo);
            BuildQuery.execute(tableInfo);
            BuildMapper.execute(tableInfo);
            BuildMapperXml.execute(tableInfo);
            BuildService.execute(tableInfo);
            BuildServiceImpl.execute(tableInfo);
            BuildController.execute(tableInfo);
        }
    }
}
