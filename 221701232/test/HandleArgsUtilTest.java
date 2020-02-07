import org.junit.jupiter.api.Test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class HandleArgsUtilTest {
    @Test
    public void testNoArgs() throws ParseException {
         Date now = new Date();
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
         String date = sdf.format(now);
         InfectStatistic infectStatistic = new InfectStatistic();
         String[] args = new String[1];
         args[0] = "";
         HandleArgsUtil.handleArgs(args,infectStatistic);
         assert(infectStatistic.getDate().equals(date));
         assert(infectStatistic.getOutFilePath().equals(
                 "/home/yyx/JavaWorkspace/InfectStatistic-main/221701232/result/"));
         assert(infectStatistic.getLogFilePath().equals(
                 "/home/yyx/JavaWorkspace/InfectStatistic-main/221701232/log"));
    }

    @Test
    public void testArgsWithValidDate() throws ParseException {
        String[] args = new String[2];
        args[0] = "-date";
        args[1] = "2020-02-05";
        InfectStatistic infectStatistic = new InfectStatistic();
        HandleArgsUtil.handleArgs(args, infectStatistic);
        assert(infectStatistic.getDate().equals(args[1]));
    }

    @Test
    public void testArgsWithInvalidDate() throws ParseException {
        String[] args = new String[2];
        args[0] = "-date";
        args[1] = "2020-88-05";// invalid date
        InfectStatistic infectStatistic = new InfectStatistic();
        HandleArgsUtil.handleArgs(args, infectStatistic);
        boolean convertSuccess=true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            format.parse(infectStatistic.getDate());
        } catch (ParseException e) {
            convertSuccess=false;
        }
        assert(convertSuccess == true);
    }

    @Test
    public void testArgsWithDateCompareToNow() throws ParseException {
        String[] args = new String[2];
        args[0] = "-date";
        args[1] = "2099-12-06";// now date is 2020-02-06
        InfectStatistic infectStatistic = new InfectStatistic();
        HandleArgsUtil.handleArgs(args, infectStatistic);
        Date now = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        assert (now.compareTo(sd.parse(infectStatistic.getDate())) >= 0);// now >= date
    }
}