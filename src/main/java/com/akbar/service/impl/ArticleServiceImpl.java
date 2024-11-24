package com.akbar.service.impl;

import com.akbar.entity.Article;
import com.akbar.mapper.ArticleMapper;
import com.akbar.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
}
