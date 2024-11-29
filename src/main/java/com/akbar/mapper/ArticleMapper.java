package com.akbar.mapper;

import com.akbar.domain.entity.Article;
import com.akbar.domain.vo.ArticleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 获取文章列表
     * @param page
     * @param categoryId
     * @param tagId
     * @param state
     * @return
     */
    @Select({
            "<script>",
            "SELECT a.*,",
            "c.category_name,",
            "GROUP_CONCAT(t.tag_name ORDER BY t.tag_name ASC) AS tag_names",
            "FROM tb_article a",
            "LEFT JOIN tb_category c ON a.category_id = c.id",
            "LEFT JOIN tb_article_tag at ON a.id = at.article_id",
            "LEFT JOIN tb_tag t ON at.tag_id = t.id",
            "WHERE 1=1",
            "<if test='categoryId != null'>AND a.category_id = #{categoryId}</if>",
            "<if test='tagId != null'>AND t.id = #{tagId}</if>",
            "<if test='state != null'>AND a.state = #{state}</if>",
            "GROUP BY a.id, c.category_name, a.state",
            "ORDER BY a.updated_time DESC",
            "</script>"
    })
    Page<ArticleVO> getArticleList(Page<ArticleVO> page, @Param("categoryId") Integer categoryId, @Param("tagId") Integer tagId, @Param("state") String state);


    /**
     * 插入文章标签
     * @param articleId
     * @param tagIds
     */
    @Insert("<script>" +
            "INSERT INTO tb_article_tag (article_id, tag_id) VALUES " +
            "<foreach collection='tagIds' item='tagId' separator=','>" +
            "(#{articleId}, #{tagId})" +
            "</foreach>" +
            "</script>")
    void insertArticleTags(@Param("articleId") Integer articleId, List<Integer> tagIds);
}
