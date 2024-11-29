package com.akbar.controller;

import com.akbar.domain.vo.ArticleTagRequestVo;
import com.akbar.domain.vo.ArticleVO;
import com.akbar.service.ArticleService;
import com.akbar.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;
    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
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
    @GetMapping
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
     *  添加文章
     * @param articleTagRequestVo
     * @return
     */
    @PostMapping
    public Result addArticle(@RequestBody ArticleTagRequestVo articleTagRequestVo) {
        boolean result = articleService.addArticle(articleTagRequestVo);

        if (!result) {
            return Result.error("添加文章失败！");
        }

        return Result.success();
    }
}
