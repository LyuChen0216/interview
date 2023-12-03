# 解析店铺名称&匹配店铺名称

## 完成情况
成功处理CSV文件和JSON数据，并进行词典匹配。

## 技术栈
- Java21
- 外部库：Apache Commons CSV, OpenCSV, Apache POI, Jackson JSON

## 主要功能
1. 从原始CSV文件中提取数据并生成新的CSV文件
2. 使用词典对店铺名称进行匹配，生成带有标签的CSV文件

## 题目分析
1. 通过Apache Commons CSV解析原始CSV文件的表头和数据
2. 使用Jackson JSON解析数据JSON并生成记录列表
3. 多线程加载词典，对店铺名称进行匹配
4. 生成新的CSV文件，包括提取数据和匹配后的结果


## 注意事项
- 确保`标签词库1026.xlsx`文件存在且包含正确的词典数据
- 自定义INPUT_CSV_FILE，OUTPUT_HALF_CSV_FILE，OUTPUT_CSV_FILE文件路径

## 结果
控制台打印出“完成匹配”即为运行结束，生成[output-half.csv](https://docs.qq.com/sheet/DRU5MVUdEWUJlSEZv?tab=rsjtd6)和[output-last.csv](https://docs.qq.com/sheet/DRWtUQlZ2enR0Q3pk?tab=g5pwtx)两个文件

## 其他
1. 实际csv文件会有30G怎么读取大文件并解析
- 可以将大文件分成小块，逐块读取和处理。 
- 可以借助Hadoop处理大型CSV文件，将大型CSV文件存储在Hadoop分布式文件系统（HDFS）中，编写MapReduce作业来读取和解析CSV文件。MapReduce框架会自动将任务拆分成多个子任务，并在各个节点上并行执行，每个Mapper负责读取CSV文件的一部分，并解析成键值对进行处理。
2. 解析出来的店铺会有重复，需要去重
- 代码中加载文件时采用HashSet存储已解析的店铺信息避免重复。
3. 词典行数也比较多 注意匹配性能
- 将匹配任务拆分成多个子任务，并使用多线程或并行处理技术进行并发执行。这样可以同时处理多个匹配，提高匹配速度。
4. 词典排序，按照关键词多的排在前面，如先匹配3个关键词都有的，再匹配2个关键词都有的，最后匹配只有1个的
- 创建HashMap或者TreeMap来存储词典标签和关键词匹配的数量，遍历词典标签库中的每个标签，依次对每个标签与表格中的字段进行匹配，并记录匹配的关键词数量；根据匹配的关键词数量将词典标签进行分类和排序。使用一个List来存储匹配结果，并根据关键词数量进行排序；按照排序的结果进行处理，先处理匹配关键词数量最多的标签，然后是数量较少的标签，最后是只有一个关键词的标签。







