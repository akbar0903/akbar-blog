package com.akbar.controller;

import com.akbar.entity.Admin;
import com.akbar.entity.Log;
import com.akbar.service.AdminService;
import com.akbar.service.LogService;
import com.akbar.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin")
@Validated   // 开启参数校验(@Validated用于单个字段或者方法参数的校验，@Valid用于对象校验)
public class AdminController {

    // 注入redisTemplate
    private final StringRedisTemplate redisTemplate;

    private final AdminService adminService;

    private final LogService logService;

    @Autowired
    public AdminController(AdminService adminService, StringRedisTemplate redisTemplate, LogService logService) {
        this.adminService = adminService;
        this.redisTemplate = redisTemplate;
        this.logService = logService;
    }

    /**
     * 管理员登录
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    public Result login(
            @RequestParam(value = "username") @Pattern(regexp = "^\\S{5,16}$", message = "用户名长度至少5位，最多16位") String username,
            @RequestParam(value = "password") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "密码至少6位，且必须包含大小写字母和数字") String password) {

        // 判断用户是否存在(根据用户名查询用户)
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Admin admin = adminService.getOne(queryWrapper);
        if (admin == null) {
            return Result.error("该用户不存在！");
        }

        // 判断密码是否正确
        String salt = admin.getSalt();
        if (Md5Util.getMD5String(password + salt).equals(admin.getPassword())) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", admin.getId());
            claims.put("username", admin.getUsername());
            String token = JwtUtil.generateToken(claims);

            // 把token存储到redis中
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.set(token, token, 12, TimeUnit.HOURS);

            // 记录登录日志
            Log log = new Log();
            log.setOperationType("登录");
            log.setOperator(admin.getUsername());
            log.setDetails("登录成功！");
            log.setLogLevel("primary");
            log.setAdminId(admin.getId());
            logService.save(log);

            // 返回token
            return Result.success(token);
        }

        return Result.error("密码错误！");
    }


    /**
     * 更新管理员密码
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @param token
     * @return
     */
    @PatchMapping
    public Result updatePassword(
            @RequestParam(value = "oldPassword") String oldPassword,
            @RequestParam(value = "newPassword") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "密码至少6位，且必须包含大小写字母和数字") String newPassword,
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
            return Result.error("身份验证失败！");
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

            // 记录修改密码日志
            Log log = new Log();
            log.setOperationType("修改密码");
            log.setOperator(admin.getUsername());
            log.setDetails("修改密码成功！");
            log.setLogLevel("danger");
            log.setAdminId(admin.getId());
            logService.save(log);

            // 清除redis中的token
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.getOperations().delete(token);
            return Result.success("密码修改成功！");
        }

        return Result.error("密码修改失败！");
    }


    /**
     * 更新管理员信息
     * @param nickname
     * @param avatar
     * @param githubUrl
     * @param giteeUrl
     * @param biliUrl
     * @return
     */
    @PatchMapping("/updateInfo")
    public Result updateInfo(
            @RequestParam(value = "nickname",required = false) String nickname,
            @RequestParam(value = "avatar",required = false) @URL(message = "无效的头像 URL") String avatar,
            @RequestParam(value = "githubUrl",required = false) @URL(message = "无效的 Github URL") String githubUrl,
            @RequestParam(value = "giteeUrl",required = false) @URL(message = "无效的 Gitee URL") String giteeUrl,
            @RequestParam(value = "biliUrl",required = false) @URL(message = "无效的 Bilibili URL") String biliUrl) {

        Admin admin = new Admin();
        // 判断参数是否为空，防止不必要的更新
        if (nickname != null) {
            admin.setNickname(nickname);
        }
        if (avatar != null) {
            admin.setAvatar(avatar);
        }
        if (githubUrl != null) {
            admin.setGithubUrl(githubUrl);
        }
        if (giteeUrl != null) {
            admin.setGiteeUrl(giteeUrl);
        }
        if (biliUrl != null) {
            admin.setBiliUrl(biliUrl);
        }

        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);

        boolean result = adminService.update(admin, queryWrapper);

        if (!result) {
            return Result.error("更新失败！");
        }

        // 记录修改信息日志
        Log log = new Log();
        log.setOperationType("修改信息");
        log.setOperator((String) map.get("username"));
        log.setDetails("管理员信息修改成功！");
        log.setLogLevel("warning");
        log.setAdminId(id);
        logService.save(log);

        return Result.success("更新成功！");
    }


    /**
     * 获取管理员信息
     * @return
     */
    @GetMapping
    public Result<Admin> getAdminInfo() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");

        Admin admin = adminService.getById(id);

        return Result.success(admin);
    }
}
