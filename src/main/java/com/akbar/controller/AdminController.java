package com.akbar.controller;

import com.akbar.entity.Admin;
import com.akbar.service.AdminService;
import com.akbar.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin")
public class AdminController {

    // 注入redisTemplate
    @Autowired
    private StringRedisTemplate redisTemplate;

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result login(
            @RequestParam(value = "username") @Pattern(regexp = "^\\S{5,16}$", message = "用户名长度至少5位，最多16位") String username,
            @RequestParam(value = "password") /*@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d).{6,}$", message = "密码至少6位，且必须包含大小写字母和数字")*/ String password) {

        // 判断用户是否存在(根据用户名查询用户)
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Admin admin = adminService.getOne(queryWrapper);
        System.out.println("------------------"+admin+"------------------");
        if (admin == null) {
            return Result.error("该用户不存在！");
        }

        // 判断密码是否正确
        String salt = admin.getSalt();
        if (Md5Util.getMD5String(password + salt).equals(admin.getPassword())) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", admin.getId());
            claims.put("username", admin.getUsername());
            System.out.println("------------------"+claims+"------------------");
            String token = JwtUtil.generateToken(claims);

            // 把token存储到redis中
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.set(token, token, 12, TimeUnit.HOURS);

            // 返回token
            return Result.success(token);
        }

        return Result.error("密码错误！");
    }


    /**
     * 更新管理员密码
     */
    @PatchMapping
    public Result updatePassword(
            @RequestParam(value = "oldPassword") String oldPassword,
            @RequestParam(value = "newPassword") /*@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d).{6,}$", message = "密码至少6位，且必须包含大小写字母和数字") */String newPassword,
            @RequestParam(value = "confirmPassword") String confirmPassword,
            @RequestHeader(value = "Authorization") String token) {

        // 如果三个密码中任何一个没有传过来，返回错误
        if (!StringUtils.hasLength(oldPassword) || !StringUtils.hasLength(newPassword) || !StringUtils.hasLength(confirmPassword)) {
            return Result.error("缺少必要的参数！");
        }

        //校验原密码是否正确
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Admin admin = adminService.getOne(queryWrapper);
        String oldSalt = admin.getSalt();
        if (!Md5Util.getMD5String(oldPassword + oldSalt).equals(admin.getPassword())) {
            return Result.error("原密码错误！");
        }

        // 判断新密码和确认密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            return Result.error("两次密码输入不一致！");
        }

        // 生成新的盐，并更新密码
        String newSalt = RandomStringUtil.generateRandomString();
        admin.setSalt(newSalt);
        admin.setPassword(Md5Util.getMD5String(newPassword + newSalt));
        boolean result = adminService.updateById(admin);
        if (result) {
            // 清除redis中的token
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.getOperations().delete(token);
            return Result.success("密码修改成功！");
        }

        return Result.error("密码修改失败！");
    }
}
