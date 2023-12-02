package com.lyu.csv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CSVParser {
    //初始样例
    private static final String INPUT_CSV_FILE = "D:\\20230907\\a_work\\interview\\source\\sample.csv";
    //第一步生成新的csv文件
    private static final String OUTPUT_HALF_CSV_FILE = "D:\\20230907\\a_work\\interview\\source\\output-half.csv";
    //匹配完成生成的文件
    private static final String OUTPUT_CSV_FILE = "D:\\20230907\\a_work\\interview\\source\\output-last.csv";
    //第一步生成新的csv文件的表头
    private static final String[] OUTPUT_HALF_HEADER = {"task_id", "storeId", "storeName"};
    //匹配完成生成的文件的表头
    private static final String[] OUTPUT_HEADER = {"task_id", "storeId", "storeName", "tag"};
    //标签库
    private static final String DICTIONARY_FILE = "D:\\20230907\\a_work\\interview\\标签词库1026.xlsx";
    //设置线程池大小
    private static final int THREAD_POOL_SIZE = 7;


    public static void main(String[] args) {
        try {
            // 创建CSV Parser和Writer
            org.apache.commons.csv.CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(new FileReader(INPUT_CSV_FILE));
            CSVWriter csvWriter = new CSVWriter(new FileWriter(OUTPUT_HALF_CSV_FILE));

            // 解析原始CSV文件的表头
            String[] header = csvParser.getHeaderMap().keySet().toArray(new String[0]);
            int taskIndex = getIndex(header, "task_id");
            int dataIndex = getIndex(header, "data");

            // 写入新的CSV文件的表头
            csvWriter.writeNext(OUTPUT_HALF_HEADER);

            // 逐行解析原始CSV文件，提取JSON数据并写入新的CSV文件
            for (CSVRecord record : csvParser) {
                String taskId = record.get(taskIndex);
                String dataJson = record.get(dataIndex);
                List<String[]> records = parseData(dataJson, taskId);
                csvWriter.writeAll(records);
            }

            // 关闭CSV Parser和Writer
            csvParser.close();
            csvWriter.close();

            System.out.println("CSV文件转化成功");
            //生成新的csv后开始匹配了
            addTags();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 解析数据JSON并生成记录列表
     * @param dataJson 数据JSON字符串
     * @param taskId 任务ID
     */
    private static List<String[]> parseData(String dataJson, String taskId) throws IOException {
        List<String[]> records = new ArrayList<>();
        Set<String> uniqueStores = new HashSet<>(); // 用于存储唯一的店铺记录
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(dataJson);
        JsonNode dataArray = root.at("/result/data");

        for (JsonNode dataNode : dataArray) {
            JsonNode storeIdNode = dataNode.at("/data/storeId");
            if (storeIdNode == null || storeIdNode.isMissingNode()) {
                storeIdNode = dataNode.at("/data/resources/storeId");
            }

            JsonNode storeNameNode = dataNode.at("/data/storeName");
            if (storeNameNode == null || storeNameNode.isMissingNode()) {
                storeNameNode = dataNode.at("/data/resources/storeName");
            }

            String storeId = storeIdNode != null ? storeIdNode.asText() : "";
            if (storeId.isEmpty()) {
                continue; // 如果storeId为空，跳过当前记录
            }

            String storeName = storeNameNode != null ? storeNameNode.asText() : "";
            if (storeName.isEmpty()) {
                continue; // 如果storeName为空，跳过当前记录
            }
            String storeInfo = taskId + "_" + storeId + "_" + storeName; // 使用任务ID、店铺ID和店铺名称作为唯一标识
            if (uniqueStores.contains(storeInfo)) {
                continue; // 如果已存在相同的店铺信息，跳过当前记录
            }
            uniqueStores.add(storeInfo); // 将唯一的店铺信息加入Set中

            records.add(new String[]{taskId, storeId, storeName});
        }

        return records;
    }
    private static void addTags() {
        try {
            // 创建CSV Parser和Writer
            org.apache.commons.csv.CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(new FileReader(OUTPUT_HALF_CSV_FILE));
            CSVWriter csvWriter = new CSVWriter(new FileWriter(OUTPUT_CSV_FILE));

            // 解析原始CSV文件的表头
            String[] header = csvParser.getHeaderMap().keySet().toArray(new String[0]);
            int taskIndex = getIndex(header, "task_id");
            int storeIdIndex = getIndex(header, "storeId");
            int storeNameIndex = getIndex(header, "storeName");

            // 写入新的CSV文件的表头
            csvWriter.writeNext(OUTPUT_HEADER);

            // 加载词典
            Map<String, String> dictionary = loadDictionary(DICTIONARY_FILE);

            // 逐行解析原始CSV文件，提取店铺名称并匹配词典
            for (CSVRecord record : csvParser) {
                String taskId = record.get(taskIndex);
                String storeId = record.get(storeIdIndex);
                String storeName = record.get(storeNameIndex);

                String tag = matchDictionary(storeName, dictionary);

                String[] outputRecord = {taskId, storeId, storeName, tag};
                csvWriter.writeNext(outputRecord);
            }

            // 关闭CSV Parser和Writer
            csvParser.close();
            csvWriter.close();

            System.out.println("完成匹配");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载词典
     * @param dictionaryFile 词典文件路径
     */
    public static Map<String, String> loadDictionary(String dictionaryFile) throws IOException, InterruptedException {
        Map<String, String> dictionary = new HashMap<>();

        FileInputStream fis = new FileInputStream(dictionaryFile);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (Row row : sheet) {
            executor.execute(() -> {
                try {
                    Cell keyword1Cell = row.getCell(0);
                    Cell keyword2Cell = row.getCell(1);
                    Cell keyword3Cell = row.getCell(2);
                    Cell tagCell = row.getCell(4);

                    String keyword1 = getStringCellValueOrDefault(keyword1Cell);
                    String keyword2 = getStringCellValueOrDefault(keyword2Cell);
                    String keyword3 = getStringCellValueOrDefault(keyword3Cell);
                    String tag = getStringCellValueOrDefault(tagCell);

                    dictionary.put(keyword1 + "," + keyword2 + "," + keyword3, tag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        workbook.close();
        fis.close();

        return dictionary;
    }
    /**
     * 获取单元格字符串值或默认值
     * @param cell 单元格
     */
    private static String getStringCellValueOrDefault(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            DataFormatter dataFormatter = new DataFormatter();
            return dataFormatter.formatCellValue(cell);
        }
    }

    /**
     * 匹配词典
     * @param storeName 店铺名称
     * @param dictionary 词典映射
     */
    private static String matchDictionary(String storeName, Map<String, String> dictionary) {
        return dictionary.entrySet().parallelStream()
                .filter(entry -> matchKeywords(storeName, entry.getKey()))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse("");
    }
    /**
     * 匹配关键词
     * @param storeName 店铺名称
     * @param keywords 关键词
     */
    private static boolean matchKeywords(String storeName, String keywords) {
        String[] keywordArray = keywords.split(",");
        return Arrays.stream(keywordArray).allMatch(storeName::contains);
    }
    /**
     * 获取列索引
     * @param header 表头
     * @param columnName 列名
     */
    private static int getIndex(String[] header, String columnName) {
        for (int i = 0; i < header.length; i++) {
            if (columnName.equals(header[i])) {
                return i;
            }
        }
        return -1;
    }
}
