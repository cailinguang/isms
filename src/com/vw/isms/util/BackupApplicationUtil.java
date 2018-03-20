package com.vw.isms.util;


import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Created by clg on 2018/3/19.
 */
public class BackupApplicationUtil {

    public static final String SP_restore_key = "restoreFile";

    public static void rebootAndRestore(String restoreFile){
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    System.out.println("等待10s");
                    Thread.sleep(1000);

                    String javaHome = System.getProperty("java.home");
                    String rootDir = System.getProperty("root.dir");
                    StringBuffer commond = new StringBuffer(javaHome + File.separator +"bin" + File.separator + "java ");
                    if(!StringUtils.isEmpty(rootDir)){
                        commond.append("-Droot.dir=").append(rootDir);
                    }
                    commond.append(" -D"+SP_restore_key+"=").append(restoreFile);
                    commond.append(" -jar vw-standard-0.2.0.jar");
                    Process process = Runtime.getRuntime().exec(commond.toString());
                    System.out.println("程序重启完成！");
                } catch (Exception e) {
                    System.out.println("重启失败，原因：");
                    e.printStackTrace();
                }
            }
        });
        System.out.println("程序准备重启！");
        System.exit(0);
    }



    public static String exec(String command) {
        System.out.println("run command:"+command);
        StringBuilder sb = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            process.getOutputStream().close();
            reader.close();
            process.destroy();
        } catch (Exception e) {
            System.out.println("执行外部命令错误，命令行:" + command);
            e.printStackTrace();
        }
        return sb.toString();
    }
}
