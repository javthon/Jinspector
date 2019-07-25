package priv.javthon.jinspector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import priv.javthon.jinspector.entity.InspectionResult;
import priv.javthon.jinspector.entity.Schedule;
import priv.javthon.jinspector.inspector.component.DataCenter;
import priv.javthon.jinspector.inspector.manager.LocalInspector;
import priv.javthon.jinspector.main.Scheduler;
import priv.javthon.jinspector.utils.GenericUtil;
import priv.javthon.jinspector.utils.InspectUtil;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class StartupRunner implements ApplicationRunner {

    private ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);

    @Override
    public void run(ApplicationArguments args) {

        List<InspectionResult> localInspection = LocalInspector.inspect();
        InspectUtil.sendResult(localInspection);

        Schedule mailSchedule = DataCenter.REPORT_SCHEDULE;
        String initialTimeMail = mailSchedule.getInitialTime();
        log.info("Waiting for first mail schedule at "+initialTimeMail);
        executor.scheduleAtFixedRate(Scheduler::doScheduledSendMailTask, GenericUtil.getDelay(initialTimeMail),mailSchedule.getInterval(), TimeUnit.SECONDS);

        Schedule inspectSchedule = DataCenter.INSPECT_SCHEDULE;
        String initialTimeInspect = inspectSchedule.getInitialTime();
        log.info("Waiting for first inspect schedule at "+initialTimeInspect);
        executor.scheduleAtFixedRate(Scheduler::doScheduledInspectTask, GenericUtil.getDelay(initialTimeInspect),inspectSchedule.getInterval(), TimeUnit.SECONDS);
    }

}
