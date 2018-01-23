package com.waston.service.impl;

import com.waston.service.FileService;
import com.waston.utils.FTPUtil;
import com.waston.utils.NameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Author wangtao
 * @Date 2018/1/20
 **/
@Service
public class FileServiceImpl implements FileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadFile(MultipartFile multipartFile, String path) {
        String oldName = multipartFile.getOriginalFilename();
        if(StringUtils.isEmpty(oldName))
            return null;
        String suffix = NameUtil.getSuffix(oldName);
        String newName = NameUtil.getName() + suffix;
        String dir =  NameUtil.getDateDir();
        path = path + "/" + dir;
        logger.info("开始上传文件, 文件名={}, 路径={}, 新文件名={}", oldName, path, newName);
        File fileDir = new File(path);
        if(!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File file = new File(path, newName);
        //上传到FTP服务器上
        try {
            //先上传到服务器上, 再从服务器把文件上传到FTP服务器中
            multipartFile.transferTo(file);
            if(FTPUtil.uploadFile(dir, Arrays.asList(file))) {
                logger.info("文件上传成功");
                file.delete();
                return dir + "/" + newName;
            } else {
                return "null";
            }
        } catch (IOException e) {
            logger.error("文件上传失败:", e);
            return "null"; //上传失败标志
        }
    }
}
