package com.akbar.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("tb_article_tag")
public class ArticleTag {
    @TableId(value = "article_id")
    private Integer articleId;
    private Integer tagId;

    public Integer getArticleId() {
        return articleId;
    }
    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getTagId() {
        return tagId;
    }
    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
}
