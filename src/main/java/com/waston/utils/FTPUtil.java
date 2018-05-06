package com.waston.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * FTPClient API
 * 登录FTP服务器当前目录为FTP服务根目录
 * boolean makeDirectory(String path); 在FTP 服务当前目录创建文件夹, 不能创建多级目录
 * boolean changeWorkingDirectory(String path);改变当前工作目录
 * 注 path = "images", 不用写"/images"
 */

/**
 * @Author wangtao
 * @Date 2018/1/23
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
    private static final String FTP_IP = PropertiesUtil.getProperty("ftp.server.ip");
    private static final int FTP_PORT = 21;
    private static final String FTP_USER = PropertiesUtil.getProperty("ftp.user");
    private static final String FTP_PASSWORD = PropertiesUtil.getProperty("ftp.password");

    //上传的缓冲区大小 1M
    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * FTPClient客户端, Apache的
     */
    private static final FTPClient ftpClient = new FTPClient();

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
        try {
            logger.info("开始连接ftp服务器");
            ftpClient.connect(ip, port);
            int reply = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.error("FTP服务拒接连接");
            }
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }


    /**
     * 对外开放的上传方法, 上传到根目录
     * @param fileList
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) throws IOException{
        logger.info("开始上传文件");
        boolean result = upload(null, fileList);
        logger.info("结束上传,上传结果:{}", result);
        return result;
    }

    /**
     * 上传多个文件
     * @param remoteDir 远程目录
     * @param fileList 本地文件list
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(String remoteDir, List<File> fileList) throws IOException{
        logger.info("开始上传文件");
        boolean result = upload(remoteDir, fileList);
        logger.info("结束上传,上传结果:{}", result);
        return result;
    }

    /**
     * 上传单个文件
     * @param remoteDir 远程目录
     * @param file 本地文件
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(String remoteDir, File file) throws IOException{
        logger.info("开始上传文件");
        List<File> fileList = new ArrayList<>();
        fileList.add(file);
        boolean result = upload(remoteDir, fileList);
        logger.info("结束上传,上传结果:{}", result);
        return result;
    }

    /**
     * 上传文件到文件服务器的指定目录
     * @param remoteDir 远程服务端目录(可以是多级目录), 处于根目录下 如myImages/ftp
     * @param fileList 本地文件的file集合
     * @return
     * @throws IOException
     */
    private static boolean upload(String remoteDir,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器
        if(ftpClient.isConnected() || connectServer(FTP_IP, FTP_PORT, FTP_USER, FTP_PASSWORD)){
            try {
                //设置上传目录
                if(remoteDir != null && !ftpClient.changeWorkingDirectory(remoteDir)) {
                    if(!makeDirsTemp(remoteDir, false))
                        logger.info("改变上传目录失败, 使用根目录");
                }
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
                if(ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            }
        } else {
            uploaded = false;
        }
        return uploaded;
    }

    /**
     * 创建多级目录, 关闭连接
     * @param dirs
     * @return
     * @throws IOException
     */
    public static boolean mkDirs(String dirs) throws IOException{
        return makeDirsTemp(dirs, true);
    }
    /**
     * 创建目录(可以多级)
     * @param dirs 目录: 如aa/bb/cc
     * @param isClose 是否关闭连接, 如果调用该方法后要继续操作, 则传false, 否则true
     * @return
     * @throws IOException
     */
    private static boolean makeDirsTemp(String dirs, boolean isClose) throws IOException {
        //如果ftpClient已连接, 否则重新连接服务器成功的话
        if ((ftpClient.isConnected()) || connectServer(FTP_IP, FTP_PORT, FTP_USER, FTP_PASSWORD)) {
            if (dirs.startsWith("/"))
                dirs = dirs.substring(1);
            if (dirs.endsWith("/"))
                dirs = dirs.substring(0, dirs.length() - 1);
            String[] dirArr = dirs.split("/");
            try {
                for (String dir : dirArr) {
                    //改变目录失败, 说明目录不存在
                    if (!ftpClient.changeWorkingDirectory(dir)) {
                        if (!ftpClient.makeDirectory(dir))
                            return false;
                        ftpClient.changeWorkingDirectory(dir);
                    }
                }
            } catch (IOException e) {
                logger.error("创建目录失败", e);
                return false;
            } finally {
                if (isClose && ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            }
        } else {
            return false; //连接失败, 返回false
        }
        return true;
    }

}
