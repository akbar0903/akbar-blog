package com.akbar.controller;

import com.akbar.entity.Log;
import com.akbar.entity.UserAvatarHistory;
import com.akbar.service.LogService;
import com.akbar.service.UserAvatarHistoryService;
import com.akbar.utils.Result;
import com.akbar.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/userAvatarHistory")
public class UserAvatarHistoryController {

    private final UserAvatarHistoryService userAvatarHistoryService;

    private final LogService logService;

    @Autowired
    public UserAvatarHistoryController(UserAvatarHistoryService userAvatarHistoryService, LogService logService) {
        this.userAvatarHistoryService = userAvatarHistoryService;
        this.logService = logService;
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

    /**
     * 删除用户头像历史记录
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteUserAvatarHistory(@PathVariable Integer id) {
        boolean result = userAvatarHistoryService.removeById(id);

        if (!result) {
            return Result.success("删除失败！");
        }

        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        String username = (String) claims.get("username");
        Integer adminId = (Integer) claims.get("id");
        String ipAddress = ThreadLocalUtil.getIP();

        Log log = new Log();
        log.setAdminId(adminId);
        log.setOperator(username);
        log.setLogLevel("danger");
        log.setOperationType("删除");
        log.setDetails("删除用户头像历史记录成功！");
        log.setIpAddress(ipAddress);
        logService.save(log);

        return Result.success("删除成功！");
    }
}
