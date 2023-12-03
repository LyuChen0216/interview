package com.lyu.broken.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.lyu.broken.mapper.BrokenPriceMapper;
import com.lyu.broken.pojo.vo.BrokenPriceVo;
import com.lyu.broken.pojo.BrokenPrice;
import org.springframework.beans.BeanUtils;
/**
* 处理xlsx文件导入数据库
* */
public class BrokenPriceListener extends AnalysisEventListener<BrokenPriceVo> {

    private BrokenPriceMapper brokenPriceMapper;
    public BrokenPriceListener(BrokenPriceMapper brokenPriceMapper) {
        this.brokenPriceMapper = brokenPriceMapper;
    }

    //一行一行读取
    public void invoke(BrokenPriceVo brokenPriceVo, AnalysisContext analysisContext) {
        //调用方法添加数据库
        BrokenPrice brokenPrice = new BrokenPrice();
        BeanUtils.copyProperties(brokenPriceVo, brokenPrice);
        System.out.println(brokenPrice.getId());
        brokenPrice.setUsed(0);
        brokenPriceMapper.insert(brokenPrice);
    }

    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
