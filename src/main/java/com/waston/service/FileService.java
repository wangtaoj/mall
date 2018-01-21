package com.waston.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author wangtao
 * @Date 2018/1/20
 **/
public interface FileService {

    /**
     * 文件上传服务
     * @param multipartFile
     * @param path
     * @return 返回上传文件后的文件资源位置(URI), 不需要开头的斜杠
     */
    String uploadFile(MultipartFile multipartFile, String path);

}
