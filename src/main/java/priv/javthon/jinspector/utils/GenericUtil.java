package priv.javthon.jinspector.utils;

import com.alibaba.fastjson.JSONArray;
import org.yaml.snakeyaml.Yaml;
import priv.javthon.jinspector.JinspectorApplication;
import priv.javthon.jinspector.entity.InspectionResult;
import priv.javthon.jinspector.entity.Schedule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class GenericUtil {
    public static Map<String,Object> getObjectMapFromSource(String source){
        Yaml yaml = new Yaml();
        InputStream is = null;
        try {
             is=new FileInputStream(new File(getJarPath()+"config/"+source));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return yaml.loadAs(is, Map.class);
    }

    public static String encodeResult(List<InspectionResult> result){
        return JSONArray.toJSONString(result);
    }

    public static List<InspectionResult> decodeResult(String result){
        return JSONArray.parseArray(result, InspectionResult.class);
    }

    public static int getDelay(String initialTime){
        String[] hourMinute = initialTime.split(":");
        LocalTime scheduledTime = LocalTime.of(Integer.valueOf(hourMinute[0]), Integer.valueOf(hourMinute[1]));
        LocalTime now = LocalTime.now();
        return Math.abs(scheduledTime.toSecondOfDay() - now.toSecondOfDay());
    }

    public static String getJarPath() {
        URL url = JinspectorApplication.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"
            // 截取路径中的jar包名
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }

        File file = new File(filePath);
        filePath = file.getAbsolutePath();//得到windows下的正确路径
        int index = filePath.indexOf("file:");
        if(index!=-1){
            filePath=filePath.substring(0,index);
        }
        char[] chars = filePath.toCharArray();
        if(chars[chars.length-1]!='\\'||chars[chars.length-1]!='/'){
            filePath=filePath.concat("/");
        }
        return filePath;
    }


    public static LocalDateTime getNextScheduleTime(Schedule schedule){
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        int nowSeconds = now.toSecondOfDay();
        Schedule inspectSchedule = schedule;
        int scheduleSeconds = inspectSchedule.getInterval() + nowSeconds;
        LocalTime localTime = LocalTime.ofSecondOfDay(scheduleSeconds);
        LocalDateTime nextScheduleTime = LocalDateTime.of(today, localTime);
        return nextScheduleTime;
    }

    public static void main(String[] args) {
        String path = GenericUtil.getJarPath();
        System.out.println(path);
    }
}
