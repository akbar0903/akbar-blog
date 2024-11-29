package com.akbar.service;

import com.akbar.domain.entity.Article;
import com.akbar.domain.vo.ArticleTagRequestVo;
import com.akbar.domain.vo.ArticleVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ArticleService extends IService<Article> {

    /**
     * 获取文章列表
     * @param categoryId
     * @param tagId
     * @param state
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<ArticleVO> getArticleList(Integer categoryId, Integer tagId, String state, int pageNum, int pageSize);


    /**
     * 新增文章
     * @param articleTagRequestVo
     * @return
     */
    boolean addArticle(ArticleTagRequestVo articleTagRequestVo);
}
