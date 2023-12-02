package com.lyu.broken.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyu.broken.listener.BrokenPriceListener;
import com.lyu.broken.mapper.BrokenPriceMapper;
import com.lyu.broken.pojo.vo.BrokenPriceVo;
import com.lyu.broken.pojo.BrokenPrice;
import com.lyu.broken.service.BrokenPriceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
@Service
@Slf4j
public class BrokenPriceServiceImpl extends ServiceImpl<BrokenPriceMapper, BrokenPrice> implements BrokenPriceService {
    @Resource
    private BrokenPriceMapper brokenPriceMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;
    @CacheEvict(value = "brokenPrice",allEntries = true)
    public void importBrokenPricesData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), BrokenPriceVo.class,new BrokenPriceListener(brokenPriceMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMsg(String id) {
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setHeader("x-delay", 300000);
            Message message = MessageBuilder.withBody(id.getBytes()).andProperties(messageProperties).build();
            rabbitTemplate.convertAndSend("exchange.delay","plugins",message);
            log.info("消息 {} 发送完毕，发送时间为：{}",id,new Date());

    }

    @Override
    public List<BrokenPrice> getBrokenPrices(String platform, int count, String batchNo) {
        QueryWrapper<BrokenPrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("platform", platform)
                .eq("batch_no", batchNo)
                .eq("used", 0)
                .last("LIMIT "+count);
        List<BrokenPrice> dataList = brokenPriceMapper.selectList(queryWrapper);
        for (BrokenPrice brokenPrice : dataList) {
            UpdateWrapper<BrokenPrice> updateWrapper = new UpdateWrapper<>();
            brokenPrice.setUsed(1);
            updateWrapper.eq("id", brokenPrice.getId());

            brokenPriceMapper.update(brokenPrice,updateWrapper);

        }


        return dataList;
    }

    @Override
    public Integer getUrlStatus(String body) {
        QueryWrapper<BrokenPrice> wrapper = new QueryWrapper<BrokenPrice>();
        wrapper.eq("id",body);
        Integer used = brokenPriceMapper.selectOne(wrapper).getUsed();
        return used;
    }

    @Override
    public void updateUrlStatus(String body,Integer used) {
        QueryWrapper<BrokenPrice> wrapper = new QueryWrapper<>();
        wrapper.eq("id",body);
        BrokenPrice brokenPrice = new BrokenPrice();
        brokenPrice.setUsed(used);
        brokenPriceMapper.update(brokenPrice,wrapper);
    }

    @Override
    public String getPlatform(String id) {
        BrokenPrice brokenPrice = brokenPriceMapper.selectById(id);
        String platform = brokenPrice.getPlatform();

        return platform;
    }


}
