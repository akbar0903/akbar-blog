package com.akbar.controller;

import com.akbar.entity.Category;
import com.akbar.entity.Log;
import com.akbar.service.CategoryService;
import com.akbar.service.LogService;
import com.akbar.utils.Result;
import com.akbar.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    private final LogService logService;

    @Autowired
    public CategoryController(CategoryService categoryService, LogService logService) {
        this.categoryService = categoryService;
        this.logService = logService;
    }


    /**
     * 获取文章分类列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping
    public Result<Page<Category>> getCategoryList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        Page<Category> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        // 如果修改了，按修改时间排序updated_time
        // 如果是新增，按创建时间排序created_time
        queryWrapper.orderByDesc("(CASE WHEN created_time = updated_time THEN created_time ELSE updated_time END)");
        Page<Category> categoryPage = categoryService.page(page, queryWrapper);

        return Result.success(categoryPage);
    }


    /**
     * 添加文章分类
     * @param category
     * @return
     */
    @PostMapping
    public Result addCategory(@RequestBody Category category) {

        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_name", category.getCategoryName());
        Category getOne = categoryService.getOne(queryWrapper);

        if (getOne != null) {
            return Result.error("该分类名称已存在！");
        }

        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        Integer adminId = (Integer) claims.get("id");
        String username = (String) claims.get("username");
        String ipAddress = ThreadLocalUtil.getIP();

        category.setAdminId(adminId);

        boolean result = categoryService.save(category);

        if (!result) {
            return Result.error("添加分类失败！");
        }

        Log log = new Log();
        log.setOperationType("添加");
        log.setDetails("添加分类：" + category.getCategoryName());
        log.setLogLevel("success");
        log.setOperator(username);
        log.setAdminId(adminId);
        log.setIpAddress(ipAddress);
        logService.save(log);

        return Result.success("添加分类成功！");
    }


    /**
     * 删除文章分类
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteCategory(@PathVariable Integer id) {
        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        Integer adminId = (Integer) claims.get("id");
        String username = (String) claims.get("username");
        String ipAddress = ThreadLocalUtil.getIP();

        // 先存储一下日志，再删除分类
        Log log = new Log();
        log.setOperationType("删除");
        Category category = categoryService.getById(id);
        log.setDetails("删除分类：" + category.getCategoryName());
        log.setLogLevel("danger");
        log.setOperator(username);
        log.setAdminId(adminId);
        log.setIpAddress(ipAddress);

        boolean result = categoryService.removeById(id);

        if (!result) {
            return Result.error("删除分类失败！");
        }

        logService.save(log);

        return Result.success("删除分类成功！");
    }

    @PutMapping
    public Result updateCategory(@RequestBody Category category) {
        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        Integer adminId = (Integer) claims.get("id");
        String username = (String) claims.get("username");
        String ipAddress = ThreadLocalUtil.getIP();

        Log log = new Log();
        log.setOperationType("更新");
        log.setDetails("更新后的分类名称：" + category.getCategoryName());
        log.setLogLevel("warning");
        log.setOperator(username);
        log.setAdminId(adminId);
        log.setIpAddress(ipAddress);

        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_name", category.getCategoryName());
        Category getOne = categoryService.getOne(queryWrapper);

        if (getOne != null) {
            return Result.error("该分类名称已存在！");
        }

        boolean result = categoryService.updateById(category);

        if (!result) {
            return Result.error("更新分类失败！");
        }

        logService.save(log);

        return Result.success("更新分类成功！");
    }
}
