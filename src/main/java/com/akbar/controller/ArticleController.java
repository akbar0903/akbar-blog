package com.akbar.controller;

import com.akbar.domain.entity.Article;
import com.akbar.domain.entity.ArticleCoverImageHistory;
import com.akbar.domain.entity.Log;
import com.akbar.domain.vo.ArticleTagRequestVo;
import com.akbar.domain.vo.ArticleVO;
import com.akbar.service.ArticleCoverImageHistoryService;
import com.akbar.service.ArticleService;
import com.akbar.service.LogService;
import com.akbar.utils.Result;
import com.akbar.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleCoverImageHistoryService articleCoverImageHistoryService;
    private final LogService logService;
    private final ArticleService articleService;
    @Autowired
    public ArticleController(ArticleService articleService,ArticleCoverImageHistoryService articleCoverImageHistoryService,LogService logService) {
        this.articleService = articleService;
        this.articleCoverImageHistoryService = articleCoverImageHistoryService;
        this.logService = logService;
    }

    /**
     *  获取文章列表
     * @param categoryId
     * @param tagId
     * @param state
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    public Result<Page<ArticleVO>> getArticleList(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "tagId", required = false) Integer tagId,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        Page<ArticleVO> page = articleService.getArticleList(categoryId, tagId, state, pageNum, pageSize);
        return Result.success(page);
    }


    /**
     *  获取文章详情
     * @param articleId
     * @return
     */
    @GetMapping("/{id}")
    public Result<ArticleVO> getArticleById(@PathVariable(value = "id") Integer articleId) {
        ArticleVO articleVO = articleService.getArticleById(articleId);
        return Result.success(articleVO);
    }

    /**
     *  添加文章
     * @param articleTagRequestVo
     * @return
     */
    @PostMapping
    public Result addArticle(@RequestBody ArticleTagRequestVo articleTagRequestVo) {
        boolean result = articleService.addArticle(articleTagRequestVo);

        if (!result) {
            return Result.error("添加文章失败，文章已存在或参数错误！");
        }

        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        Integer adminId = (Integer) claims.get("id");
        String username = (String) claims.get("username");
        String ipAddress = ThreadLocalUtil.getIP();

        Log log = new Log();
        log.setOperator(username);
        log.setAdminId(adminId);
        log.setOperationType("添加文章");
        log.setLogLevel("success");
        log.setDetails("添加文章：" + articleTagRequestVo.getTitle());
        log.setIpAddress(ipAddress);
        logService.save(log);

        if (articleTagRequestVo.getCoverImage() != null) {
            ArticleCoverImageHistory articleCoverImageHistory = new ArticleCoverImageHistory();
            QueryWrapper<ArticleCoverImageHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("cover_url", articleTagRequestVo.getCoverImage());
            ArticleCoverImageHistory history = articleCoverImageHistoryService.getOne(queryWrapper);
            if (history == null) {
                articleCoverImageHistory.setCoverUrl(articleTagRequestVo.getCoverImage());
                articleCoverImageHistoryService.save(articleCoverImageHistory);
            }
        }

        return Result.success();
    }


    /**
     *  删除文章
     * @param articleId
     * @return
     */
    @DeleteMapping("/del/{id}")
    public Result deleteArticle(@PathVariable(value = "id") Integer articleId) {
        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        String username = (String) claims.get("username");
        Integer adminId = (Integer) claims.get("id");
        String ipAddress = ThreadLocalUtil.getIP();

        Log log = new Log();
        log.setOperator(username);
        log.setAdminId(adminId);
        log.setOperationType("删除文章");
        log.setLogLevel("danger");
        Article article = articleService.getById(articleId);
        log.setDetails("删除文章：" + article.getTitle());
        log.setIpAddress(ipAddress);
        logService.save(log);

        boolean result = articleService.removeById(articleId);
        if (!result) {
            return Result.error("删除文章失败！");
        }

        return Result.success();
    }


    /**
     *  更新文章
     * @param articleTagRequestVo
     * @return
     */
    @PutMapping
    public Result updateArticle(@RequestBody ArticleTagRequestVo articleTagRequestVo) {
        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        String username = (String) claims.get("username");
        Integer adminId = (Integer) claims.get("id");
        String ipAddress = ThreadLocalUtil.getIP();

        boolean updateResult = articleService.updateArticle(articleTagRequestVo);

        if (!updateResult) {
            return Result.error("更新文章失败，文章不存在或参数错误！");
        }

        Log log = new Log();
        log.setOperator(username);
        log.setAdminId(adminId);
        log.setOperationType("更新文章");
        log.setLogLevel("success");
        Article article = articleService.getById(articleTagRequestVo.getId());
        log.setDetails("更新后的文章：" + article.getTitle());
        log.setIpAddress(ipAddress);
        logService.save(log);

        return Result.success();
    }
}
