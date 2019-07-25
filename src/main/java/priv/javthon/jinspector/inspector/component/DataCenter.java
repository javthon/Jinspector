package priv.javthon.jinspector.inspector.component;


import lombok.extern.slf4j.Slf4j;
import priv.javthon.jinspector.entity.MailConfig;
import priv.javthon.jinspector.entity.MailSender;
import priv.javthon.jinspector.entity.Schedule;
import priv.javthon.jinspector.utils.GenericUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
public class DataCenter {

    public static String SERVER_URL;
    public static String SERVER_TOKEN;
    public static boolean IS_MASTER;
    public static Map<String,Object> EXPOSE_CONNECTIONS;
    public static List<Map<String, Object>> LOCAL_SERVERS;
    public static MailConfig MAIL_CONFIG;

    public static Schedule REPORT_SCHEDULE;
    public static Schedule INSPECT_SCHEDULE;

    public static List<String> REQUIRED_HOST_IDS;


    public static void refreshAll(){
        log.info("----Configuration refresh task started");
        SERVER_URL= getServerUrl();
        SERVER_TOKEN= getServerToken();
        IS_MASTER= isMaster();
        EXPOSE_CONNECTIONS=getExposeConnections();
        LOCAL_SERVERS=getLocalServers();
        MAIL_CONFIG=getMailSender();
        REPORT_SCHEDULE=getScheduler("report");
        INSPECT_SCHEDULE=getScheduler("inspect");
        REQUIRED_HOST_IDS=getRequiredHostIds();
        log.info("----Configuration refresh task ended");
    }


    private static Schedule getScheduler(String type){
        Map<String, Object> objectMap = GenericUtil.getObjectMapFromSource("schedule.yml");
        Map<String,Object> schedule = (Map<String,Object>) objectMap.get("schedule");
        Map<String,Object> concreteSchedule = (Map<String, Object>) schedule.get(type);
        String initialTime = String.valueOf(concreteSchedule.get("initialTime"));
        if("null".equals(initialTime)){
            throw new NullPointerException("");
        }
        String interval = String.valueOf(concreteSchedule.get("interval"));
        if("null".equals(interval)){
            throw new NullPointerException("");
        }
        String[] split = interval.split("\\*");
        int period=1;
        for(String item : split){
            period*=Integer.valueOf(item);
        }
        return new Schedule(initialTime,period);
    }



    private static MailConfig getMailSender(){
        Map<String, Object> objectMap = GenericUtil.getObjectMapFromSource("schedule.yml");
        Map<String,Object> mail = (Map<String, Object>) objectMap.get("mail");
        Map<String,Object> sender = (Map<String, Object>) mail.get("sender");
        MailConfig mailConfig = new MailConfig();
        MailSender mailSender=new MailSender();
        //TODO verify
        mailSender.setHost(String.valueOf(sender.get("host")));
        mailSender.setPort((Integer) sender.get("port"));
        mailSender.setAddress(String.valueOf(sender.get("address")));
        mailSender.setPassword(String.valueOf(sender.get("password")));
        mailSender.setNickname(String.valueOf(sender.get("nickname")));
        mailConfig.setMailSender(mailSender);

        List<String> receivers = (List<String>) mail.get("receivers");
        String titlePrefix = String.valueOf(mail.get("title"));
        mailConfig.setReceivers(receivers);
        mailConfig.setTitle(titlePrefix);
        return mailConfig;
    }

    private static List<String> getRequiredHostIds(){
        Map<String, Object> objectMap = GenericUtil.getObjectMapFromSource("server.yml");
        return (List<String>) objectMap.get("requiredHostIds");
    }

    private static Map<String,Object> getExposeConnections(){
        Map<String, Object> objectMap = GenericUtil.getObjectMapFromSource("expose.yml");
        if(objectMap==null){
            return new HashMap<>();
        }
        return (Map<String, Object>) objectMap.get("connections");
    }
    private static List<Map<String, Object>> getLocalServers(){
        Map<String, Object> objectMap = GenericUtil.getObjectMapFromSource("local.yml");
        if(objectMap==null){
            return new ArrayList<>();
        }
        return (List<Map<String, Object>>) objectMap.get("servers");
    }

    private static String getServerUrl(){
        Map<String, Object> objectMap = GenericUtil.getObjectMapFromSource("server.yml");
        Map<String,Object> server = (Map<String, Object>) objectMap.get("server");
        String ip = (String) server.get("ip");
        Integer port = (Integer) server.get("port");
        return "http://"+ip+":"+port;
    }

    private static String getServerToken(){
        Map<String, Object> objectMap = GenericUtil.getObjectMapFromSource("server.yml");
        Map<String,Object> server = (Map<String, Object>) objectMap.get("server");
        return (String) server.get("token");
    }

    private static boolean isMaster(){
        Map<String, Object> objectMap = GenericUtil.getObjectMapFromSource("server.yml");
        Map<String,Object> server = (Map<String, Object>) objectMap.get("server");
        String id = (String) server.get("id");
        return isThisHost(id);
    }

    public static boolean isThisHost(String id){
        return getHostId().equals(id);
    }

    public static String getHostId(){
        Map<String, Object> objectMap = GenericUtil.getObjectMapFromSource("id.yml");
        String configId = String.valueOf(objectMap.get("id"));
        if("null".equals(configId)){
            throw new NullPointerException("id can not be null");
        }
        return configId;
    }
}
