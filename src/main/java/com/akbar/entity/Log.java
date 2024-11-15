package com.akbar.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("tb_log")
public class Log {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String operationType;
    private String operator;
    private String details;
    private LocalDateTime operatedTime;
    private Integer adminId;
    private String logLevel;


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperationType() {
        return operationType;
    }
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getOperatedTime() {
        return operatedTime;
    }
    public void setOperatedTime(LocalDateTime operatedTime) {
        this.operatedTime = operatedTime;
    }

    public Integer getAdminId() {
        return adminId;
    }
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getLogLevel() {
        return logLevel;
    }
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
}
