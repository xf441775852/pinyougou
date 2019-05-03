package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;

//上传文件
@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload.do")
    public Result upload(MultipartFile file) throws Exception {

        try {
            //获取文件名后缀
            int index = file.getOriginalFilename().lastIndexOf(".");
            String suffix = file.getOriginalFilename().substring(index + 1);
            //上传文件工具类
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //上传
            String name = fastDFSClient.uploadFile(file.getBytes(), suffix);
            //返回fileId
            String url = FILE_SERVER_URL+name;
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }
}
