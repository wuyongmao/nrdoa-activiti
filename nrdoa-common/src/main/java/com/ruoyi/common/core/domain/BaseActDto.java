package com.ruoyi.common.core.domain;

import java.io.Serializable;

/**
 * ActEntity基类
 * 
 * @author ruoyi
 *
 */
public class BaseActDto implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;

    private Integer offset = 0;
    
    public Integer getOffset() {
    	if(pageNum !=null && pageNum>1) {
    		
    		offset = pageSize * (pageNum-1);
    	}
    	
    	
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	/** 总记录数 */
    private Integer total;

    public Integer getPageNum()
    {
        if (pageNum == null)
        {
            pageNum = 0;
        }
        pageNum = (pageNum - 1) * getPageSize();
        if (pageNum < 0)
        {
            pageNum = 0;
        }
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        if (pageSize == null)
        {
            pageSize = 10;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public Integer getTotal()
    {
        return total;
    }

    public void setTotal(Integer total)
    {
        this.total = total;
    }
}
