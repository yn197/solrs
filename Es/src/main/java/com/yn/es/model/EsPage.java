package com.yn.es.model;

import org.springframework.util.CollectionUtils;

import java.util.List;

public class EsPage<T> {

    //当前页的数量
    private int size;

    /**
     * 当前页
     */
    private int pageNum;
    /**
     * 每页显示多少条
     */
    private int pageSize;

    /**
     * 总记录数
     */
    private int total;
    /**
     * 本页的数据列表
     */
    private List<T> list;

    /**
     * 总页数
     */
    private int pages;
    /**
     * 页码列表的开始索引（包含）
     */
    private int beginPageIndex;
    /**
     * 页码列表的结束索引（包含）
     */
    private int endPageIndex;

    /**
     * 只接受前4个必要的属性，会自动的计算出其他3个属性的值
     *
     * @param pageNum
     * @param pageSize
     * @param total
     * @param list
     */
    public EsPage(int pageNum, int pageSize, int total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.size = CollectionUtils.isEmpty(list)?0:list.size();
        // 计算总页码
        pages =pageSize==0?0:(total + pageSize - 1) / pageSize;

        // 计算 beginPageIndex 和 endPageIndex
        // >> 总页数不多于10页，则全部显示
        if (pages <= 10) {
            beginPageIndex = 1;
            endPageIndex = pages;
        }
        // >> 总页数多于10页，则显示当前页附近的共10个页码
        else {
            // 当前页附近的共10个页码（前4个 + 当前页 + 后5个）
            beginPageIndex = pageNum - 4;
            endPageIndex = pageNum + 5;
            // 当前面的页码不足4个时，则显示前10个页码
            if (beginPageIndex < 1) {
                beginPageIndex = 1;
                endPageIndex = 10;
            }
            // 当后面的页码不足5个时，则显示后10个页码
            if (endPageIndex > pages) {
                endPageIndex = pages;
                beginPageIndex = pages - 10 + 1;
            }
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getBeginPageIndex() {
        return beginPageIndex;
    }

    public void setBeginPageIndex(int beginPageIndex) {
        this.beginPageIndex = beginPageIndex;
    }

    public int getEndPageIndex() {
        return endPageIndex;
    }

    public void setEndPageIndex(int endPageIndex) {
        this.endPageIndex = endPageIndex;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}