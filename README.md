# myblog-project

## 介绍
springboot，vue3个人博客项目。

## 数据库设计
**代码：**
```sql
/*
 Navicat Premium Data Transfer

 Source Server         : MySQL8.0
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : localhost:3306
 Source Schema         : db_blog

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 30/11/2024 14:35:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_admin
-- ----------------------------
DROP TABLE IF EXISTS `tb_admin`;
CREATE TABLE `tb_admin`  (
                             `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                             `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
                             `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
                             `salt` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '盐',
                             `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
                             `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
                             `github_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'GitHub地址',
                             `gitee_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Gitee地址',
                             `bili_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '哔哩哔哩地址',
                             `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_article
-- ----------------------------
DROP TABLE IF EXISTS `tb_article`;
CREATE TABLE `tb_article`  (
                               `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
                               `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '摘要',
                               `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
                               `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
                               `comment_count` int UNSIGNED NULL DEFAULT 0 COMMENT '评论数',
                               `view_count` int UNSIGNED NULL DEFAULT 0 COMMENT '观看次数',
                               `state` enum('草稿','发布') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '草稿' COMMENT '文章状态',
                               `admin_id` int UNSIGNED NULL DEFAULT NULL COMMENT '管理员ID',
                               `category_id` int UNSIGNED NULL DEFAULT NULL COMMENT '分类ID',
                               `is_top` tinyint(1) NULL DEFAULT 1 COMMENT '是否置顶（0表示置顶，1表示不置顶）',
                               `can_comment` tinyint(1) NULL DEFAULT 1 COMMENT '是否可以评论（0表示允许，1表示不允许）',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `admin_id`(`admin_id` ASC) USING BTREE,
                               INDEX `category_id`(`category_id` ASC) USING BTREE,
                               UNIQUE INDEX `unique_title`(`title` ASC) USING BTREE,
                               CONSTRAINT `tb_article_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `tb_admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                               CONSTRAINT `tb_article_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `tb_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_article_cover_history
-- ----------------------------
DROP TABLE IF EXISTS `tb_article_cover_history`;
CREATE TABLE `tb_article_cover_history`  (
                                             `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                             `article_id` int UNSIGNED NOT NULL COMMENT '文章ID',
                                             `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '封面URL',
                                             `uploaded_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             INDEX `article_id`(`article_id` ASC) USING BTREE,
                                             CONSTRAINT `tb_article_cover_history_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_article_tag
-- ----------------------------
DROP TABLE IF EXISTS `tb_article_tag`;
CREATE TABLE `tb_article_tag`  (
                                   `article_id` int UNSIGNED NOT NULL COMMENT '文章ID',
                                   `tag_id` int UNSIGNED NOT NULL COMMENT '标签ID',
                                   PRIMARY KEY (`article_id`, `tag_id`) USING BTREE,
                                   INDEX `tag_id`(`tag_id` ASC) USING BTREE,
                                   CONSTRAINT `tb_article_tag_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                   CONSTRAINT `tb_article_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tb_tag` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_category
-- ----------------------------
DROP TABLE IF EXISTS `tb_category`;
CREATE TABLE `tb_category`  (
                                `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                `category_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
                                `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `admin_id` int UNSIGNED NULL DEFAULT NULL COMMENT '管理员ID',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `admin_id`(`admin_id` ASC) USING BTREE,
                                CONSTRAINT `tb_category_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `tb_admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_comment
-- ----------------------------
DROP TABLE IF EXISTS `tb_comment`;
CREATE TABLE `tb_comment`  (
                               `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
                               `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `visitor_id` int UNSIGNED NULL DEFAULT NULL COMMENT '访客ID',
                               `article_id` int UNSIGNED NULL DEFAULT NULL COMMENT '文章ID',
                               `admin_id` int UNSIGNED NULL DEFAULT NULL COMMENT '管理员ID',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `visitor_id`(`visitor_id` ASC) USING BTREE,
                               INDEX `article_id`(`article_id` ASC) USING BTREE,
                               INDEX `admin_id`(`admin_id` ASC) USING BTREE,
                               CONSTRAINT `tb_comment_ibfk_1` FOREIGN KEY (`visitor_id`) REFERENCES `tb_visitor` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                               CONSTRAINT `tb_comment_ibfk_2` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                               CONSTRAINT `tb_comment_ibfk_3` FOREIGN KEY (`admin_id`) REFERENCES `tb_admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_log`;
CREATE TABLE `tb_log`  (
                           `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `operation_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
                           `operator` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作者',
                           `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详情',
                           `operated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                           `admin_id` int UNSIGNED NULL DEFAULT NULL COMMENT '管理员ID',
                           `log_level` enum('success','warning','danger','primary') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'primary',
                           `ip_address` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户端ip地址',
                           PRIMARY KEY (`id`) USING BTREE,
                           INDEX `admin_id`(`admin_id` ASC) USING BTREE,
                           CONSTRAINT `tb_log_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `tb_admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 300 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_music
-- ----------------------------
DROP TABLE IF EXISTS `tb_music`;
CREATE TABLE `tb_music`  (
                             `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                             `music_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
                             `artist` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '艺术家',
                             `audio_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '音频URL',
                             `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面URL',
                             `duration` int UNSIGNED NULL DEFAULT NULL COMMENT '音频时长（秒）',
                             `admin_id` int UNSIGNED NULL DEFAULT NULL COMMENT '管理员ID',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `admin_id`(`admin_id` ASC) USING BTREE,
                             CONSTRAINT `tb_music_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `tb_admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_slider
-- ----------------------------
DROP TABLE IF EXISTS `tb_slider`;
CREATE TABLE `tb_slider`  (
                              `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                              `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
                              `sort_order` int UNSIGNED NULL DEFAULT 0 COMMENT '排序值',
                              `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
                              `admin_id` int UNSIGNED NULL DEFAULT NULL COMMENT '管理员ID',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `admin_id`(`admin_id` ASC) USING BTREE,
                              CONSTRAINT `tb_slider_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `tb_admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_tag
-- ----------------------------
DROP TABLE IF EXISTS `tb_tag`;
CREATE TABLE `tb_tag`  (
                           `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `tag_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
                           `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `admin_id` int UNSIGNED NULL DEFAULT NULL COMMENT '管理员ID',
                           PRIMARY KEY (`id`) USING BTREE,
                           INDEX `admin_id`(`admin_id` ASC) USING BTREE,
                           CONSTRAINT `tb_tag_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `tb_admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_user_avatar_history
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_avatar_history`;
CREATE TABLE `tb_user_avatar_history`  (
                                           `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                           `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
                                           `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '头像URL',
                                           `uploaded_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           INDEX `user_id`(`user_id` ASC) USING BTREE,
                                           CONSTRAINT `tb_user_avatar_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tb_admin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_visitor
-- ----------------------------
DROP TABLE IF EXISTS `tb_visitor`;
CREATE TABLE `tb_visitor`  (
                               `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
                               `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
                               `salt` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '盐',
                               `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
                               `is_registered` tinyint(1) NULL DEFAULT 0 COMMENT '是否为注册用户',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```
**解释：**
1. `ON DELETE CASCADE`： 是一个外键约束操作，它指定当父表中的一条记录被删除时，所有与其相关联的子表中的记录也会被自动删除。
2. TIMESTAMP 是一种数据类型，用于存储日期和时间。
3. CURRENT_TIMESTAMP 是一个动态的时间戳，返回当前的日期和时间。
4. TIMESTAMP DEFAULT CURRENT_TIMESTAMP 会在记录插入时，如果没有指定时间字段的值，自动填充为当前的时间。