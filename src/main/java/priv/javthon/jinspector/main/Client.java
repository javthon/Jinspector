package priv.javthon.jinspector.main;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import priv.javthon.jinspector.JinspectorApplication;
import priv.javthon.jinspector.entity.Schedule;
import priv.javthon.jinspector.inspector.component.DataCenter;
import priv.javthon.jinspector.utils.GenericUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Client or agent
 * @author Javthon
 * @version 1.0
 */
@Slf4j
public class Client {

    private ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    private void work() {
        DataCenter.refreshAll();
        //server
        if (DataCenter.IS_MASTER) {
            SpringApplication.run(JinspectorApplication.class);
        }else {
            Schedule inspectSchedule = DataCenter.INSPECT_SCHEDULE;
            String initialTime = inspectSchedule.getInitialTime();
            log.info("Client inspection started");
            Scheduler.doScheduledInspectTask();
            log.info("Client inspection ended");
            log.info("----Waiting for the next inspection schedule at {}", initialTime);
            executor.scheduleAtFixedRate(Scheduler::doScheduledInspectTask, GenericUtil.getDelay(initialTime),inspectSchedule.getInterval(), TimeUnit.SECONDS);
        }

    }



    public static void main(String[] args) {
        Client client = new Client();
        client.work();
    }

}
