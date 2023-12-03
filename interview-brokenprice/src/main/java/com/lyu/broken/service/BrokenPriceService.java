package com.lyu.broken.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyu.broken.pojo.BrokenPrice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
* Service层
* */
public interface BrokenPriceService extends IService<BrokenPrice> {
    //将xlsx文件导入数据库
    void importBrokenPricesData(MultipartFile file);
    //发送消息到rabbitmq
    void sendMsg(String id);
    //获取count条链接
    List<BrokenPrice> getBrokenPrices(String platform, int count, String batchNo);
    //获取该条数据的处理情况
    Integer getUrlStatus(String body);
    //更新数据的处理情况：0->未处理，1->已获取但是没有截图，2->处理完毕
    void updateUrlStatus(String body,Integer used);
    //获取该条数据的平台
    String getPlatform(String id);
}
