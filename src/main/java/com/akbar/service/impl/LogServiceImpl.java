package com.akbar.service.impl;

import com.akbar.entity.Log;
import com.akbar.mapper.LogMapper;
import com.akbar.service.LogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {
}
