package com.akbar.service.impl;

import com.akbar.entity.Admin;
import com.akbar.mapper.AdminMapper;
import com.akbar.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
}
