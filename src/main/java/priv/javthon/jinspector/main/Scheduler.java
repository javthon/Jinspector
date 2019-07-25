package priv.javthon.jinspector.main;

import lombok.extern.slf4j.Slf4j;
import priv.javthon.jinspector.entity.InspectionResult;
import priv.javthon.jinspector.entity.MailConfig;
import priv.javthon.jinspector.inspector.component.DataCenter;
import priv.javthon.jinspector.inspector.manager.ExposeInspector;
import priv.javthon.jinspector.inspector.manager.LocalInspector;
import priv.javthon.jinspector.template.MailTemplate;
import priv.javthon.jinspector.utils.EmailUtil;
import priv.javthon.jinspector.utils.GenericUtil;
import priv.javthon.jinspector.utils.InspectUtil;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Scheduler {
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void doScheduledSendMailTask(){
        log.info("----Starting report schedule");

        DataCenter.refreshAll();
        MailConfig mailConfig = DataCenter.MAIL_CONFIG;
        Map<String, List<InspectionResult>> resultMap= new HashMap<>(Server.resultMap);
        List<InspectionResult> exposeResult = ExposeInspector.inspect();
        resultMap.put("expose",exposeResult);
        Set<String> keySet = resultMap.keySet();
        boolean isNoException=true;
        for(String key : keySet){
            List<InspectionResult> inspectionResults = resultMap.get(key);
            isNoException = inspectionResults.stream().filter(InspectionResult::isExpected).count() == inspectionResults.size();
            if(!isNoException){
                break;
            }
        }

        isNoException = checkReceivedHost(resultMap, isNoException);

        if(resultMap.size()!=0){
            String resultString = MailTemplate.generatorReport(resultMap);
            LocalDateTime now = LocalDateTime.now();
            String title;
            String time = timeFormatter.format(now);
            if(isNoException){
                title=mailConfig.getTitle()+"-"+time+"(OK)";
                log.info("There are no exceptions in this report");
            }else{
                title=mailConfig.getTitle()+"-"+time+"(Exception!!!)";
                log.error("There are exceptions in this report!!!");
            }

            try {
                EmailUtil.sendMail(mailConfig.getMailSender(),mailConfig.getReceivers(),title,resultString);
                log.info("The mail was sent successfully");
            } catch (MessagingException | UnsupportedEncodingException e) {
                e.printStackTrace();
                log.error("The mail was sent unsuccessfully!!!");
            }
        }

        LocalDateTime nextScheduleTime = GenericUtil.getNextScheduleTime(DataCenter.REPORT_SCHEDULE);
        log.info("----Current report schedule is over, the next schedule is at {},", nextScheduleTime);
    }


    public static void doScheduledInspectTask(){
        log.info("----Starting inspection schedule");
        DataCenter.refreshAll();
        if(DataCenter.IS_MASTER){
            Map<String, List<InspectionResult>> resultMapOnlyExceptions = new HashMap<>(Server.resultMap);
            List<InspectionResult> exposeResult = ExposeInspector.inspect();
            resultMapOnlyExceptions.put("expose",exposeResult);
            checkReceivedHost(resultMapOnlyExceptions,false);
            //有异常的
            resultMapOnlyExceptions.forEach((key,list)-> list.removeAll(list.stream().filter(InspectionResult::isExpected).collect(Collectors.toList())));
            if(resultMapOnlyExceptions.size()>0){
                File file=new File(GenericUtil.getJarPath()+"deleteMeAfterHandled");
                boolean exists = file.exists();
                if(!exists){
                    MailConfig mailConfig = DataCenter.MAIL_CONFIG;
                    String resultString = MailTemplate.generatorReport(resultMapOnlyExceptions);
                    LocalDateTime now = LocalDateTime.now();
                    String time = timeFormatter.format(now);
                    String title=mailConfig.getTitle()+"-"+time+"(Exception!!!)";
                    log.error("There are exceptions in this inspection!!!");
                    try {
                        EmailUtil.sendMail(mailConfig.getMailSender(),mailConfig.getReceivers(),title,resultString);
                        file.createNewFile();
                        log.info("The mail was sent successfully, you need to delete the 'deleteMeAfterHandled' file to recover the inspection schedule");
                    } catch (MessagingException | IOException e) {
                        e.printStackTrace();
                        log.error("The mail was sent unsuccessfully");
                    }
                }
            }
        }else{
            List<InspectionResult> localInspection = LocalInspector.inspect();
            if (localInspection.size() > 0) {
                InspectUtil.sendResult(localInspection);
            }
        }
        LocalDateTime nextScheduleTime = GenericUtil.getNextScheduleTime(DataCenter.INSPECT_SCHEDULE);
        log.info("----Current inspection schedule is over, the next schedule is at {},", nextScheduleTime);
    }

    private static boolean checkReceivedHost(Map<String, List<InspectionResult>> resultMap,boolean isNoException){
        for(String id:DataCenter.REQUIRED_HOST_IDS){
            if(resultMap.get(id)==null){
                isNoException=false;
                List<InspectionResult> unReceived=new ArrayList<>();
                InspectionResult result=new InspectionResult();
                result.setName("result not received");
                result.setDescription("Didn't receive inspection data from this host");
                result.setExpected(false);
                result.setHost("host id#"+id);
                unReceived.add(result);
                resultMap.put("id#"+id,unReceived);
            }
        }
        return isNoException;
    }
}
