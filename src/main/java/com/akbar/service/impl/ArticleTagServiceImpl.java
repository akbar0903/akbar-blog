package com.akbar.service.impl;

import com.akbar.domain.entity.ArticleTag;
import com.akbar.mapper.ArticleTagMapper;
import com.akbar.service.ArticleTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
}
