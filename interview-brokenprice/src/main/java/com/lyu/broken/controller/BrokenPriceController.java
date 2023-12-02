package com.lyu.broken.controller;
import java.util.*;
import com.alibaba.druid.util.StringUtils;
import com.lyu.broken.pojo.result.Result;
import com.lyu.broken.pojo.BrokenPrice;
import com.lyu.broken.pojo.vo.BrokenPriceVo;
import com.lyu.broken.pojo.vo.ImageVo;
import com.lyu.broken.service.BrokenPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
/**
* Controller层
* */
@Tag(name = "BrokenPriceController",description = "破价商品处理")
@RestController
@Slf4j
public class BrokenPriceController {
    @Resource
    private BrokenPriceService brokenPriceService;

    @Operation(summary = "获取破价链接")
    @GetMapping("/breakPriceUrls")
    public Result getBrokenPriceUrls(
            @RequestParam("platform") String platform,
            @RequestParam(value = "count", defaultValue = "10") int count,
            @RequestParam("batchNo") String batchNo) {

        long startTime = System.currentTimeMillis();
        List<BrokenPrice> brokenPrices = brokenPriceService.getBrokenPrices(platform, count, batchNo);
        for (int i = 0; i < brokenPrices.size(); i++) {
            System.out.println(brokenPrices.get(i));
            brokenPriceService.sendMsg(brokenPrices.get(i).getId());
        }
        List<BrokenPriceVo> list = brokenPrices.stream()
                .map(brokenPrice -> new BrokenPriceVo(brokenPrice.getId(), brokenPrice.getBatchNo(), brokenPrice.getPlatform(), brokenPrice.getPageUrl(), brokenPrice.getSkuId()))
                .toList();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("代码运行时长：" + duration + " 毫秒");

        return new Result(list,true);

    }

    @Operation(summary = "上传图片")
    @PostMapping("/uploadImage")
    public Result uploadImage111(@RequestBody ImageVo request) {
        if (StringUtils.isEmpty(request.getImg())) {
            return new Result("图片base64编码不能为空",false);
        }
        try {
            // 将base64编码转换为字节数组
            byte[] imageBytes = Base64.decodeBase64(request.getImg());
            // 创建保存图片的目录
            String saveDir = "D:\\20230907\\boring\\picture\\";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 生成文件名，包含当前时间戳
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String fileName = sdf.format(new Date()) + ".png";

            // 拼接保存路径
            String filePath = saveDir + fileName;

            String id = request.getId();
            String platform = brokenPriceService.getPlatform(id);
            // 添加当前时间水印
            addTimestampWatermark(imageBytes, filePath);

            // 返回截图访问地址
            String url = "https://images.bxtdata.com/snapshot/ahuatian/"+toPinyin(platform)+"/" + fileName;

            brokenPriceService.updateUrlStatus(id,2);
            return new Result(url,true);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result("图片上传失败",false);
        }
    }

    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result importData(@RequestParam("file") MultipartFile file){
        long startTime = System.currentTimeMillis();
        brokenPriceService.importBrokenPricesData(file);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("代码运行时长：" + duration + " 毫秒");
        return new Result("success",true);
    }

    //为图片添加水印
    private void addTimestampWatermark(byte[] imageBytes, String filePath) throws IOException {
        // 将字节数组保存为图片文件
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(imageBytes);
        fos.close();

        // 添加当前时间水印，使用Graphics2D绘制文字
        File imageFile = new File(filePath);
        BufferedImage image = ImageIO.read(imageFile);

        Graphics2D graphics = image.createGraphics();
        graphics.setFont(new Font("Arial", Font.BOLD, 16));
        graphics.setColor(Color.BLACK);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String watermarkText = sdf.format(new Date());
        int x = image.getWidth() - graphics.getFontMetrics().stringWidth(watermarkText) - 10;
        int y = 20;

        graphics.drawString(watermarkText, x, y);
        graphics.dispose();

        ImageIO.write(image, "png", imageFile);
    }
    //将获取到的平台转为拼音作为存储路径的一级返回
    public static String toPinyin(String chinese) {
        StringBuilder pinyinBuilder = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
                String[] pinyinArray = new String[0];
                try {
                    pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    throw new RuntimeException(e);
                }
                if (pinyinArray != null && pinyinArray.length > 0) {
                    for (String pinyin : pinyinArray) {
                        // 移除每个拼音后的数字
                        pinyin = pinyin.replaceAll("\\d", "");
                        pinyinBuilder.append(pinyin);
                    }
                }
            } else {
                pinyinBuilder.append(c);
            }
        }
        return pinyinBuilder.toString();
    }

}




