package com.akbar.controller;

import com.akbar.utils.Result;
import com.akbar.utils.AliOSSUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        //文件内容存储到本地磁盘中
        //如果两次文件的原始名字一样，进行覆盖
        String originalFilename = file.getOriginalFilename();
        //保证文件的名字是唯一的
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        //把文件上传到阿里云上
        String url = AliOSSUtil.uploadFile(fileName,file.getInputStream());

        return Result.success(url);
    }
}
