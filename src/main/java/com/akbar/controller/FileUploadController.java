package com.akbar.controller;

import com.akbar.entity.Log;
import com.akbar.service.LogService;
import com.akbar.utils.Result;
import com.akbar.utils.AliOSSUtil;
import com.akbar.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
public class FileUploadController {

    private final LogService logService;
    @Autowired
    public FileUploadController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        //文件内容存储到本地磁盘中
        //如果两次文件的原始名字一样，进行覆盖
        String originalFilename = file.getOriginalFilename();
        //保证文件的名字是唯一的
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        //把文件上传到阿里云上
        String url = AliOSSUtil.uploadFile(fileName,file.getInputStream());

        // 存储日志记录
        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        Integer adminId = (Integer)claims.get("id");
        String username = (String)claims.get("username");
        String ipAddress = ThreadLocalUtil.getIP();

        Log log = new Log();
        log.setOperationType("上传文件");
        log.setOperator(username);
        log.setDetails("本地文件名：" + originalFilename + "，云端文件名：" + fileName + "，云端地址：" + url);
        log.setAdminId(adminId);
        log.setLogLevel("success");
        log.setIpAddress(ipAddress);
        logService.save(log);

        return Result.success(url);
    }
}
