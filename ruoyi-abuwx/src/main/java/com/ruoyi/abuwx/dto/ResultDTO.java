package com.ruoyi.abuwx.dto;

import com.ruoyi.abuwx.domain.DisasterReliefApplication;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户基本信息对象 user_info
 * 
 * @author ruoyi
 * @date 2025-04-07
 */
public class ResultDTO
{
    public ResultDTO() {
    }

    /** 主键ID */
    private List<DisasterReliefApplication> list;

    private BigDecimal count;

    public ResultDTO(List<DisasterReliefApplication> list, BigDecimal count) {
        this.list = list;
        this.count = count;
    }


    public List<DisasterReliefApplication> getList() {
        return list;
    }

    public void setList(List<DisasterReliefApplication> list) {
        this.list = list;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }
}
