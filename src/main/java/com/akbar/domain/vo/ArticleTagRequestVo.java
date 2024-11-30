package com.akbar.domain.vo;

import com.akbar.domain.entity.Article;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class ArticleTagRequestVo extends Article {
    private List<Integer> tagIds;

    public List<Integer> getTagIds() {
        return tagIds;
    }
    public void setTagIds(List<Integer> tagIds) {
        this.tagIds = tagIds;
    }
}
