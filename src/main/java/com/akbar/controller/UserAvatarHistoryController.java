package com.akbar.controller;

import com.akbar.entity.UserAvatarHistory;
import com.akbar.service.UserAvatarHistoryService;
import com.akbar.utils.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userAvatarHistory")
public class UserAvatarHistoryController {

    private final UserAvatarHistoryService userAvatarHistoryService;
    @Autowired
    public UserAvatarHistoryController(UserAvatarHistoryService userAvatarHistoryService) {
        this.userAvatarHistoryService = userAvatarHistoryService;
    }

    /**
     * 获取用户头像历史列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping
    public Result<Page<UserAvatarHistory>> userAvatarHistoryList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        Page<UserAvatarHistory> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UserAvatarHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("uploaded_time");
        Page<UserAvatarHistory> pageResult = userAvatarHistoryService.page(page, queryWrapper);

        return Result.success(pageResult);
    }
}