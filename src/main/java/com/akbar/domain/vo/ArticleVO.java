package com.akbar.domain.vo;

import com.akbar.domain.entity.Article;

import java.util.Arrays;
import java.util.List;

public class ArticleVO extends Article {
    private String categoryName;
    // 这里的tagNames按理来说应该是一个集合，但是为了简单起见，这里用String类型
    private List<String> tagNames;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    // 将逗号分隔的字符串转换为 List<String>
    public void setTagNames(String tagNames) {
        if (tagNames != null && !tagNames.isEmpty()) {
            this.tagNames = Arrays.asList(tagNames.split(","));
        } else {
            this.tagNames = null; // 如果没有标签，则为 null
        }
    }

}
