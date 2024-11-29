package com.akbar.service.impl;

import com.akbar.domain.entity.UserAvatarHistory;
import com.akbar.mapper.UserAvatarHistoryMapper;
import com.akbar.service.UserAvatarHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserAvatarHistoryServiceImpl extends ServiceImpl<UserAvatarHistoryMapper, UserAvatarHistory> implements UserAvatarHistoryService {
}
