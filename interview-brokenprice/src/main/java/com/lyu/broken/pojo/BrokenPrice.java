package com.lyu.broken.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 映射数据库
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("broken_price")
public class BrokenPrice {
    @TableField(value = "id")
    private String id;

    @TableField(value = "batch_no")
    private String batchNo;

    @TableField(value = "platform")
    private String platform;

    @TableField(value = "page_url")
    private String pageUrl;

    @TableField(value = "sku_id")
    private String skuId;
    //0--未处理；1--提交但是未处理，2--处理完成
    @TableField(value = "used")
    private Integer used;

}
