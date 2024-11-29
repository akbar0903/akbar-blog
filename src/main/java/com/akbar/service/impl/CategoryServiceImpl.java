package com.akbar.service.impl;

import com.akbar.domain.entity.Category;
import com.akbar.mapper.CategoryMapper;
import com.akbar.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
