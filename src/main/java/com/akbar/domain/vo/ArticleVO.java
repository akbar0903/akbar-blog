package com.akbar.domain.vo;

import com.akbar.domain.entity.Article;

public class ArticleVO extends Article {
    private String categoryName;
    // 这里的tagNames按理来说应该是一个集合，但是为了简单起见，这里用String类型
    private String tagNames;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTagNames() {
        return tagNames;
    }

    public void setTagNames(String tagNames) {
        this.tagNames = tagNames;
    }
}
