package com.lyu.broken.pojo.vo;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
* 返回破价链接到客户端
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokenPriceVo {
    @ExcelProperty(value = "id",index = 0)
    private String id;

    @ExcelProperty(value = "批次号",index = 1)
    private String batchNo;

    @ExcelProperty(value = "平台",index = 2)
    private String platform;

    @ExcelProperty(value = "页面链接",index = 3)
    private String pageUrl;

    @ExcelProperty(value = "skuId",index = 4)
    private String skuId;

}
