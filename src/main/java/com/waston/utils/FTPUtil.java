package com.waston.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by geely
 */
public class FTPUtil {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    /**
     * 连接FTP服务器需要的IP, USER, PASSWORD, 从配置文件中读取
     * 以及上传的目录
     */
    private static final String FTP_ID = PropertiesUtil.getProperty("ftp.server.ip");
    private static final int FTP_PORT = 21;
    private static final String FTP_USER = PropertiesUtil.getProperty("ftp.user");
    private static final String FTP_PASSWORD = PropertiesUtil.getProperty("ftp.password");
    private static final String REMOTE_DIR = PropertiesUtil.getProperty("tp.remoteDir");

    //上传的缓冲区大小 1M
    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * FTPClient客户端, Apache的
     */
    private static FTPClient ftpClient;

    /**
     * 对外开放的上传方法
     * @param fileList
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) throws IOException{
        logger.info("开始连接ftp服务器");
        boolean result = uploadFile(REMOTE_DIR, fileList);
        logger.info("结束上传,上传结果:{}", result);
        return result;
    }

    /**
     * 连接FTP服务器
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @return
     */
    private static boolean connectServer(String ip,int port,String user,String pwd){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip, port);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

    /**
     * 使用FTPClient上传文件
     * @param remoteDir
     * @param fileList
     * @return
     * @throws IOException
     */
    private static boolean uploadFile(String remoteDir,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectServer(FTP_ID, FTP_PORT, FTP_USER, FTP_PASSWORD)){
            try {
                //设置上传目录
                ftpClient.changeWorkingDirectory(remoteDir);
                ftpClient.setBufferSize(BUFFER_SIZE);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
            } finally {
                if(fis != null)
                    fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }
}
