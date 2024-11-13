# myblog-project

## 介绍
springboot，vue3个人博客项目。

## 数据库设计
**代码：**
```sql
CREATE DATABASE IF NOT EXISTS db_blog;
USE db_blog;

-- 管理员
CREATE TABLE IF NOT EXISTS tb_admin (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    salt VARCHAR(100) NOT NULL COMMENT '盐',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像',
    github_url VARCHAR(255) COMMENT 'GitHub地址',
    gitee_url VARCHAR(255) COMMENT 'Gitee地址',
    bili_url VARCHAR(255) COMMENT '哔哩哔哩地址',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 访客（暂时这样，后续进行验证码登录或者邮箱登录的功能）
CREATE TABLE IF NOT EXISTS tb_visitor (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    salt VARCHAR(100) NOT NULL COMMENT '盐',
    nickname VARCHAR(50) COMMENT '昵称',
    is_registered BOOLEAN DEFAULT FALSE COMMENT '是否为注册用户'
);

-- 分类
CREATE TABLE IF NOT EXISTS tb_category (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    category_name VARCHAR(30) NOT NULL COMMENT '分类名称',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    admin_id INT UNSIGNED COMMENT '管理员ID',
    FOREIGN KEY (admin_id) REFERENCES tb_admin(id)
);

-- 标签
CREATE TABLE IF NOT EXISTS tb_tag (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    tag_name VARCHAR(30) NOT NULL COMMENT '标签名称',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    admin_id INT UNSIGNED COMMENT '管理员ID',
    FOREIGN KEY (admin_id) REFERENCES tb_admin(id)
);

-- 文章
CREATE TABLE IF NOT EXISTS tb_article (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    summary VARCHAR(500) COMMENT '摘要',
    content TEXT NOT NULL COMMENT '内容',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    cover_image VARCHAR(255) COMMENT '封面图片',
    comment_count INT UNSIGNED DEFAULT 0 COMMENT '评论数',
    view_count INT UNSIGNED DEFAULT 0 COMMENT '观看次数',
    state ENUM('draft', 'published') DEFAULT 'draft' COMMENT '文章状态',
    admin_id INT UNSIGNED COMMENT '管理员ID',
    category_id INT UNSIGNED COMMENT '分类ID',
    is_top BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
	can_comment BOOLEAN DEFAULT FALSE COMMENT '是否可以评论',
    FOREIGN KEY (admin_id) REFERENCES tb_admin(id),
    FOREIGN KEY (category_id) REFERENCES tb_category(id)
);

-- 文章标签关联
CREATE TABLE IF NOT EXISTS tb_article_tag (
    article_id INT UNSIGNED COMMENT '文章ID',
    tag_id INT UNSIGNED COMMENT '标签ID',
    PRIMARY KEY (article_id, tag_id),
    FOREIGN KEY (article_id) REFERENCES tb_article(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tb_tag(id) ON DELETE CASCADE
);

-- 评论
CREATE TABLE IF NOT EXISTS tb_comment (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    content TEXT NOT NULL COMMENT '内容',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    visitor_id INT UNSIGNED COMMENT '访客ID',
    article_id INT UNSIGNED COMMENT '文章ID',
    admin_id INT UNSIGNED COMMENT '管理员ID',
    FOREIGN KEY (visitor_id) REFERENCES tb_visitor(id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES tb_article(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES tb_admin(id)
);

-- 音乐
CREATE TABLE IF NOT EXISTS tb_music (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    music_name VARCHAR(30) NOT NULL COMMENT '名称',
    artist VARCHAR(30) NOT NULL COMMENT '艺术家',
    audio_url VARCHAR(255) NOT NULL COMMENT '音频URL',
    cover_url VARCHAR(255) COMMENT '封面URL',
    duration INT UNSIGNED COMMENT '音频时长（秒）',
    admin_id INT UNSIGNED COMMENT '管理员ID',
    FOREIGN KEY (admin_id) REFERENCES tb_admin(id)
);

-- 轮播图(也可以是网站首页图片)
CREATE TABLE IF NOT EXISTS tb_slider (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    sort_order INT UNSIGNED DEFAULT 0 COMMENT '排序值',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    admin_id INT UNSIGNED COMMENT '管理员ID',
    FOREIGN KEY (admin_id) REFERENCES tb_admin(id)
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS tb_log (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    operation_type VARCHAR(30) NOT NULL COMMENT '操作类型',
    operator VARCHAR(30) COMMENT '操作者',
    details TEXT COMMENT '详情',
    operated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    ip_address VARCHAR(50) COMMENT '操作IP地址',
    user_agent VARCHAR(255) COMMENT '操作User-Agent',
    admin_id INT UNSIGNED COMMENT '管理员ID',
    FOREIGN KEY (admin_id) REFERENCES tb_admin(id)
);
```
**解释：**
1. `ON DELETE CASCADE`： 是一个外键约束操作，它指定当父表中的一条记录被删除时，所有与其相关联的子表中的记录也会被自动删除。
2. TIMESTAMP 是一种数据类型，用于存储日期和时间。
3. CURRENT_TIMESTAMP 是一个动态的时间戳，返回当前的日期和时间。
4. TIMESTAMP DEFAULT CURRENT_TIMESTAMP 会在记录插入时，如果没有指定时间字段的值，自动填充为当前的时间。