import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

class HandleFileUtilTest {

    @Test
    public void testGetFiles() throws ParseException {
        InfectStatistic infectStatistic = new InfectStatistic();
        infectStatistic.setLogFilesMap(HandleFileUtil.getFiles(infectStatistic.getLogFilePath(), null));
        // Ensure files are read in chronological order
        Map<String, File> fileMap = infectStatistic.getLogFilesMap();
        String preDate = "2000-02-02";
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date pre = sd.parse(preDate);
        //assert (now.compareTo(sd.parse(infectStatistic.getDate())) >= 0);// now >= date
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            String[] strs = entry.getKey().split("/");
            String[] tempStrs = strs[strs.length - 1].split("\\.");
            Date temp = sd.parse(tempStrs[0]);
            assert(pre.compareTo(temp) < 0);// pre < temp
            pre = temp;
        }
    }

    @Test
    public void testReadLogFile() throws IOException, ParseException {
        InfectStatistic infectStatistic = new InfectStatistic();
        infectStatistic.setDate("2020-01-22");
        infectStatistic.setLogFilesMap(
                HandleFileUtil.getFiles(infectStatistic.getLogFilePath(), infectStatistic.getDate()));
        HandleFileUtil.readLogFile(infectStatistic.getLogFilesMap(), infectStatistic);
        // command line : list -log D:\log\ -out D:\ListOut1.txt -date 2020-01-22
        String resultFilePath = "/home/yyx/JavaWorkspace/InfectStatistic-main/221701232/result/ListOut1.txt";
        File file = new File(resultFilePath);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        Container container = new Container();
        int index = 0;
        while((line = br.readLine()) != null) {
            String data [] = line.split(" ");
            if (data[0].equals("//")) {
                break;
            }
            Record record = new Record();
            record.setProvinceName(data[0]);
            record.setIpNum(CommonUtil.parserStringToInt(data[1]));
            record.setSpNum(CommonUtil.parserStringToInt(data[2]));
            record.setCureNum(CommonUtil.parserStringToInt(data[3]));
            record.setDeadNum(CommonUtil.parserStringToInt(data[4]));
            if (index == 0) {
                assert(CommonUtil.compareRecord(record, infectStatistic.getCountry()));
            } else {
                container.addRecord(record);
            }
            index++;
        }
        assert(CommonUtil.compareContainer(container, infectStatistic.getContainer()));
    }

    @Test
    public void testWriteOutFile() throws ParseException, IOException {
        InfectStatistic infectStatistic = new InfectStatistic();
        // command line : list -log D:\log\ -out D:\ListOut2.txt -date 2020-01-22 -province 福建 河北
        // infectStatistic.setDate("2020-01-22");
        // infectStatistic.addProvince(new String[] {"福建", "河北"});
        // String resultFilePath = "/home/yyx/JavaWorkspace/InfectStatistic-main/221701232/result/ListOut2.txt";

        // command line :
        // list -log D:\log\ -out D:\ListOut3.txt -date 2020-01-23 -type cure dead ip -province 全国 浙江 福建
        infectStatistic.setDate("2020-01-23");
        infectStatistic.addType(new String[] {"cure", "dead", "ip"});
        infectStatistic.addProvince(new String[] {"全国", "浙江", "福建"});
        String resultFilePath = "/home/yyx/JavaWorkspace/InfectStatistic-main/221701232/result/ListOut3.txt";
        infectStatistic.setLogFilesMap(
                HandleFileUtil.getFiles(infectStatistic.getLogFilePath(), infectStatistic.getDate()));
        HandleFileUtil.readLogFile(infectStatistic.getLogFilesMap(), infectStatistic);
        HandleFileUtil.writeOutFile(infectStatistic.getOutFilePath(), infectStatistic);
        Container container = CommonUtil.readOutFile(resultFilePath);
        // read my output file
        String outFilePath = "/home/yyx/JavaWorkspace/InfectStatistic-main/221701232/result/output.txt";
        Container container1 = CommonUtil.readOutFile(outFilePath);
        assert(CommonUtil.compareContainer(container, container1));
    }
}