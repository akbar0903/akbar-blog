package com.akbar.service.impl;

import com.akbar.domain.entity.Article;
import com.akbar.domain.vo.ArticleTagRequestVo;
import com.akbar.domain.vo.ArticleVO;
import com.akbar.mapper.ArticleMapper;
import com.akbar.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final ArticleMapper articleMapper;

    @Autowired
    public ArticleServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }


    /**
     * 分页查询文章列表
     * @param categoryId
     * @param tagId
     * @param state
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<ArticleVO> getArticleList(Integer categoryId, Integer tagId, String state, int pageNum, int pageSize) {
        // 创建分页对象
        Page<ArticleVO> page = new Page<>(pageNum, pageSize);

        // 调用 Mapper 层的分页查询方法
        return articleMapper.getArticleList(page, categoryId, tagId, state);
    }


    /**
     * 新增文章
     * @param articleTagRequestVo
     * @return
     */
    @Override
    public boolean addArticle(ArticleTagRequestVo articleTagRequestVo) {
        Article article = new Article();

        // 检查文章标题是否重复
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", articleTagRequestVo.getTitle());
        Article article1 = this.getOne(queryWrapper);
        if (article1!= null) {
            return false;
        }

        // 实体类之间属性映射
        BeanUtils.copyProperties(articleTagRequestVo, article);

        // 存储文章
        boolean articleSaved = this.save(article);

        // 标签关联操作
        if (articleSaved && articleTagRequestVo.getTagIds() != null && !articleTagRequestVo.getTagIds().isEmpty()) {
            Integer articleId = article.getId();
            List<Integer> tagIds = articleTagRequestVo.getTagIds();

            articleMapper.insertArticleTags(articleId, tagIds);
        }

        return articleSaved;
    }
}
