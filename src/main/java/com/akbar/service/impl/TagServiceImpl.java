package com.akbar.service.impl;

import com.akbar.entity.Tag;
import com.akbar.mapper.TagMapper;
import com.akbar.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}
