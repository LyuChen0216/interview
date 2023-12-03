# 客户获取并处理破价链接

## 完成情况

完成拉取破价链接和截图上传接口的设计

## 技术栈
- Java21
- Spring Boot 3.1.5
- MySQL
- RabbitMQ
- EasyExcel

## 解题思路

- 拉取破价链接：
将样例文件导入MySQL数据库，添加一个属性“used”表示该条链接的处理状态：0->未处理，1->已获取但是没有截图，2->处理完毕，借用这个可以满足不被重复获取的需求
- 上传截图：
获取到的为图片Base64编码，先将其解码，再使用Graphics2D为其添加水印。
- 其他问题
采用RabbitMQ延时队列，将被拉取的一条条数据作为消息发送，若在规定时间（五分钟）内处理完成，上传截图修改该条链接状态为“2”，接收者监听队列，若消息中的"used"不为“2”则修改其为“0”，就可以继续被获。

## 安装和运行

1. 克隆项目到本地
2. 配置JDK21，安装依赖
3. 启动项目，访问“localhost:8001/swagger-ui/index.html”查看接口

## 项目结构


```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.lyu
│   │   │       └── broken
│   │   │           ├── controller
│   │   │           ├── service
│   │   │           ├── mapper
│   │   │           └── pojo
│   │   └── resources
│   │       └── application.yml
│   └── test
│       ├── java
│       │   └── com.lyu
│       │       └── broken
│       │           ├── controller
│       │           ├── service
│       │           ├── mapper
│       │           └── pojo
│       └── resources
├── target
├── pom.xml
└── README.md
```
## 运行结果
- 拉取链接：
```
{
  "data": [
    {
      "id": "1504020",
      "batchNo": "2023102801",
      "platform": "京东",
      "pageUrl": "https://item.jd.com/10076215197557.html",
      "skuId": "10076215197557"
    },
    {
      "id": "1504021",
      "batchNo": "2023102801",
      "platform": "京东",
      "pageUrl": "https://item.jd.com/10070875868491.html",
      "skuId": "10070875868491"
    },
    {
      "id": "1504022",
      "batchNo": "2023102801",
      "platform": "京东",
      "pageUrl": "https://item.jd.com/100034858621.html",
      "skuId": "100034858621"
    },
    {
      "id": "1504023",
      "batchNo": "2023102801",
      "platform": "京东",
      "pageUrl": "https://item.jd.com/100034428735.html",
      "skuId": "100034428735"
    },
    {
      "id": "1504024",
      "batchNo": "2023102801",
      "platform": "京东",
      "pageUrl": "https://item.jd.com/100034428747.html",
      "skuId": "100034428747"
    },
    {
      "id": "1504025",
      "batchNo": "2023102801",
      "platform": "京东",
      "pageUrl": "https://item.jd.com/10075150457284.html",
      "skuId": "10075150457284"
    },
    {
      "id": "1504026",
      "batchNo": "2023102801",
      "platform": "京东",
      "pageUrl": "https://item.jd.com/10073236623252.html",
      "skuId": "10073236623252"
    },
    {
      "id": "1504027",
      "batchNo": "2023102801",
      "platform": "京东",
      "pageUrl": "https://item.jd.com/10073236623253.html",
      "skuId": "10073236623253"
    },
    {
      "id": "1504028",
      "batchNo": "2023102801",
      "platform": "京东",
      "pageUrl": "https://item.jd.com/10073236623254.html",
      "skuId": "10073236623254"
    },
    {
      "id": "1504029",
      "batchNo": "2023102801",
      "platform": "京东",
      "pageUrl": "https://item.jd.com/10026565214500.html",
      "skuId": "10026565214500"
    }
  ],
  "status": true,
  "url": null
}
```
- 上传截图
```
{
  "data": null,
  "status": true,
  "url": "https://images.bxtdata.com/snapshot/ahuatian/jingdong/20231202143846697.png"
}
```