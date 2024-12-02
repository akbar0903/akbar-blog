package com.akbar.service.impl;

import com.akbar.domain.entity.Article;
import com.akbar.domain.entity.ArticleTag;
import com.akbar.domain.vo.ArticleTagRequestVo;
import com.akbar.domain.vo.ArticleVO;
import com.akbar.mapper.ArticleMapper;
import com.akbar.mapper.ArticleTagMapper;
import com.akbar.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final ArticleMapper articleMapper;

    private final ArticleTagMapper articleTagMapper;

    @Autowired
    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleTagMapper articleTagMapper) {
        this.articleMapper = articleMapper;
        this.articleTagMapper = articleTagMapper;
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
    @Override
    public Page<ArticleVO> getArticleList(Integer categoryId, Integer tagId, String state, int pageNum, int pageSize) {
        // 创建分页对象
        Page<ArticleVO> page = new Page<>(pageNum, pageSize);

        // 调用 Mapper 层的分页查询方法
        return articleMapper.getArticleList(page, categoryId, tagId, state);
    }


    /**
     * 根据文章 ID 查询文章详情
     * @param articleId
     * @return
     */
    @Override
    public ArticleVO getArticleById(Integer articleId) {
        return articleMapper.getArticleById(articleId);
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


/*    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(ArticleTagRequestVo articleTagRequestVo) {
        Article article = new Article();
        BeanUtils.copyProperties(articleTagRequestVo, article);
        boolean articleUpdated = this.updateById(article);

        if (!articleUpdated) {
            throw new RuntimeException("文章更新失败！");
        }

        // 删除原有标签关联
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, article.getId());
        articleTagMapper.delete(queryWrapper);

        // 新的标签关联
        List<ArticleTag> articleTags = articleTagRequestVo.getTagIds().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        if (!articleTags.isEmpty()) {
            articleTagMapper.insert(articleTags);
        }

        return true;
    }*/

    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(ArticleTagRequestVo articleTagRequestVo) {
        // 参数校验
        if (articleTagRequestVo == null || articleTagRequestVo.getId() == null) {
            throw new RuntimeException("参数错误");
        }

        // 检查文章是否存在
        Article existArticle = this.getById(articleTagRequestVo.getId());
        if (existArticle == null) {
            throw new RuntimeException("文章不存在");
        }

        QueryWrapper<Article> titleWrapper = new QueryWrapper<>();
        titleWrapper.eq("title", articleTagRequestVo.getTitle());
        Article article1 = this.getOne(titleWrapper);
        if (article1 != null && !article1.getId().equals(articleTagRequestVo.getId())) {
            throw new RuntimeException("文章标题已存在！");
        }

        try {
            // 更新文章基本信息
            Article article = new Article();
            BeanUtils.copyProperties(articleTagRequestVo, article);
            LocalDateTime currentTime = LocalDateTime.now();
            article.setUpdatedTime(currentTime);
            boolean articleUpdated = this.updateById(article);

            if (!articleUpdated) {
                throw new RuntimeException("文章更新失败！");
            }

            // 删除原有标签关联
            LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
            // 设置查询条件：articleId 等于当前文章 id
            queryWrapper.eq(ArticleTag::getArticleId, article.getId());
            // 删除符合条件的所有记录
            articleTagMapper.delete(queryWrapper);

            // 新的标签关联
            // 检查是否有新的标签需要关联
            if (articleTagRequestVo.getTagIds() != null && !articleTagRequestVo.getTagIds().isEmpty()) {
                // 使用 Stream 将标签 ID 列表转换为 ArticleTag 对象列表
                List<ArticleTag> articleTags = articleTagRequestVo.getTagIds().stream()
                        .map(tagId -> new ArticleTag(article.getId(), tagId))  // 为每个标签 ID 创建一个新的 ArticleTag 对象
                        .toList(); // 收集为 List

                // 遍历 ArticleTag 列表，逐个插入数据库
                for (ArticleTag articleTag : articleTags) {
                    articleTagMapper.insert(articleTag);
                }
            }

            return true;
        } catch (Exception e) {
            log.error("更新文章失败：", e);
            throw new RuntimeException("更新文章失败：" + e.getMessage());
        }
    }
}
