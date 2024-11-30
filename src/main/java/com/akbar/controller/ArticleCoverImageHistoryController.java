package com.akbar.controller;

import com.akbar.domain.entity.ArticleCoverImageHistory;
import com.akbar.domain.entity.Log;
import com.akbar.service.ArticleCoverImageHistoryService;
import com.akbar.service.LogService;
import com.akbar.utils.Result;
import com.akbar.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/articleCoverImageHistory")
public class ArticleCoverImageHistoryController {

    private final ArticleCoverImageHistoryService articleCoverImageHistoryService;
    private final LogService logService;
    @Autowired
    public ArticleCoverImageHistoryController(ArticleCoverImageHistoryService articleCoverImageHistoryService, LogService logService) {
        this.articleCoverImageHistoryService = articleCoverImageHistoryService;
        this.logService = logService;
    }


    /**
     * 获取文章封面图片历史列表（分页）
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping
    public Result<Page<ArticleCoverImageHistory>> getArticleCoverImageHistoryList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        Page<ArticleCoverImageHistory> coverImageHistoryPage = new Page<>(pageNum, pageSize);
        QueryWrapper<ArticleCoverImageHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("uploaded_time");

        Page<ArticleCoverImageHistory> result = articleCoverImageHistoryService.page(coverImageHistoryPage, queryWrapper);
        return Result.success(result);
    }


    /**
     * 删除文章封面图片历史记录
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteArticleCoverImageHistory(@PathVariable("id") Integer id) {
        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        String username = (String) claims.get("username");
        Integer adminId = (Integer) claims.get("id");
        String ipAddress = ThreadLocalUtil.getIP();

        Log log = new Log();
        log.setAdminId(adminId);
        log.setOperator(username);
        log.setOperationType("删除文章封面图片历史记录");
        ArticleCoverImageHistory articleCoverImageHistory = articleCoverImageHistoryService.getById(id);
        log.setDetails("删除云端地址为："+articleCoverImageHistory.getCoverUrl()+"的文章封面图片历史记录！");
        log.setLogLevel("danger");
        log.setIpAddress(ipAddress);
        logService.save(log);

        boolean result = articleCoverImageHistoryService.removeById(id);

        if (!result) {
            return Result.error("删除失败！");
        }

        return Result.success("删除成功！");

    }
}
