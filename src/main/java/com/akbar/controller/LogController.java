package com.akbar.controller;

import com.akbar.entity.Log;
import com.akbar.service.LogService;
import com.akbar.utils.Result;
import com.akbar.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/log")
@CrossOrigin
public class LogController {

    private final LogService logService;
    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * 获取日志列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping
    public Result<Page<Log>> getLog(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        // 构建分页对象, 传入当前页码和每页条数
        Page<Log> logPage = new Page<>(pageNum, pageSize);

        // 执行分页查询
        Page<Log> pageResult = logService.page(logPage);

        return Result.success(pageResult);
    }
}
