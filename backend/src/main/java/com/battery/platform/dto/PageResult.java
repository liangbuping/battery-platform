package com.battery.platform.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果封装类
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    public PageResult() {
        this.current = 1L;
        this.size = 10L;
        this.total = 0L;
        this.pages = 0L;
        this.records = Collections.emptyList();
    }

    public PageResult(Long current, Long size, Long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
        this.pages = (total + size - 1) / size;
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> build(Long current, Long size, Long total, List<T> records) {
        return new PageResult<>(current, size, total, records);
    }

    /**
     * 构建空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }
}
