package com.akbar.controller;

import com.akbar.domain.entity.Log;
import com.akbar.domain.entity.Tag;
import com.akbar.service.LogService;
import com.akbar.service.TagService;
import com.akbar.utils.Result;
import com.akbar.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    private final LogService logService;

    @Autowired
    public TagController(TagService tagService, LogService logService) {
        this.tagService = tagService;
        this.logService = logService;
    }


    /**
     * 获取标签列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    public Result<Page<Tag>> getTagList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        Page<Tag> tagPage = new Page<>(pageNum, pageSize);
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.orderByDesc("updated_time");
        tagPage = tagService.page(tagPage, tagQueryWrapper);

        return Result.success(tagPage);
    }


    /**
     * 获取标签列表（不分页）
     * @return
     */
    @GetMapping("/noPage")
    public Result<List<Tag>> getNoPageTagList() {
        return Result.success(tagService.list());
    }

    /**
     * 添加标签
     * @param tag
     * @return
     */
    @PostMapping
    public Result addTag(@RequestBody Tag tag) {
        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        Integer adminId = (Integer) claims.get("id");
        String username = (String) claims.get("username");
        String ipAddress = ThreadLocalUtil.getIP();

        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("tag_name",tag.getTagName());
        Tag getOne = tagService.getOne(tagQueryWrapper);
        if(getOne!= null){
            return Result.error("该标签名已存在！");
        }

        tag.setAdminId(adminId);

        boolean save = tagService.save(tag);

        if (!save) {
            return Result.error("添加标签失败！");
        }

        Log log = new Log();
        log.setOperationType("添加");
        log.setOperator(username);
        log.setAdminId(adminId);
        log.setLogLevel("success");
        log.setIpAddress(ipAddress);
        log.setDetails("添加标签：" + tag.getTagName());
        logService.save(log);

        return Result.success("添加标签成功！");
    }


    /**
     * 删除标签
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteTag(@PathVariable(value = "id") Integer id) {
        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        Integer adminId = (Integer) claims.get("id");
        String username = (String) claims.get("username");
        String ipAddress = ThreadLocalUtil.getIP();

        Log log = new Log();
        log.setOperationType("删除");
        log.setOperator(username);
        log.setAdminId(adminId);
        log.setLogLevel("success");
        log.setIpAddress(ipAddress);
        Tag tag = tagService.getById(id);
        log.setDetails("删除标签：" + tag.getTagName());

        boolean remove = tagService.removeById(id);

        if (!remove) {
            return Result.error("删除标签失败！");
        }

        logService.save(log);

        return Result.success("删除标签成功！");
    }


    /**
     * 更新标签
     * @param tag
     * @return
     */
    @PutMapping
    public Result updateTag(@RequestBody @Validated(Tag.Edit.class) Tag tag) {
        Map<String, Object> claims = ThreadLocalUtil.getClaims();
        Integer adminId = (Integer) claims.get("id");
        String username = (String) claims.get("username");
        String ipAddress = ThreadLocalUtil.getIP();

        Log log = new Log();
        log.setOperationType("更新");
        log.setOperator(username);
        log.setAdminId(adminId);
        log.setLogLevel("success");
        log.setIpAddress(ipAddress);
        log.setDetails("更新后的标签名：" + tag.getTagName());
        logService.save(log);

        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("tag_name",tag.getTagName());
        Tag getOne = tagService.getOne(tagQueryWrapper);

        if(getOne!= null){
            return Result.error("该标签名已存在！");
        }

        LocalDateTime currentTime = LocalDateTime.now();
        tag.setUpdatedTime(currentTime);
        boolean update = tagService.updateById(tag);

        if (!update) {
            return Result.error("更新标签失败！");
        }

        return Result.success("更新标签成功！");
    }
}
