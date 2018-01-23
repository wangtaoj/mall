package com.waston.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



/**
 * @Author wangtao
 * @Date 2018/1/23
 **/
public class FTPUtilTest {

    private Logger logger = LoggerFactory.getLogger(FTPUtilTest.class);

    @Test
    public void uploadFile1() throws Exception {

        File file = new File("E:/ftpTest.jpg");
        List<File> list = new ArrayList<>();
        list.add(file);
        if(FTPUtil.uploadFile(list)) {
            logger.info("图片上传成功");
        } else {
            logger.info("图片上传失败");
        }

    }

    @Test
    public void uploadFile2() throws Exception {

        File file = new File("E:/ftpTest.jpg");
        List<File> list = new ArrayList<>();
        list.add(file);
        //日期做目录
        if(FTPUtil.uploadFile(NameUtil.getDateDir(), list)) {
            logger.info("图片上传成功");
        } else {
            logger.info("图片上传失败");
        }

    }

    @Test
    public void mkdirs() throws Exception{
        String path = "cc/dd/ff";
        System.out.println(FTPUtil.mkDirs(path));
    }

}