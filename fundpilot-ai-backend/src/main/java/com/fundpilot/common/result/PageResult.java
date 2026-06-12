package com.fundpilot.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 通用分页结果
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 当前页数据 */
    private List<T> records;

    /** 当前页码 */
    private int pageNum;

    /** 每页条数 */
    private int pageSize;

    /** 总页数 */
    private int pages;

    public PageResult() {
        this.records = Collections.emptyList();
    }

    public PageResult(long total, List<T> records, int pageNum, int pageSize) {
        this.total = total;
        this.records = records;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }

    public static <T> PageResult<T> of(long total, List<T> records, int pageNum, int pageSize) {
        return new PageResult<>(total, records, pageNum, pageSize);
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0, Collections.emptyList(), 1, 20);
    }
}
