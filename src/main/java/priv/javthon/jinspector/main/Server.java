package priv.javthon.jinspector.main;

import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.javthon.jinspector.entity.InspectionResult;
import priv.javthon.jinspector.entity.Schedule;
import priv.javthon.jinspector.inspector.component.DataCenter;
import priv.javthon.jinspector.utils.GenericUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 接收数据并发送邮件
 * @author xxf
 * @version 1.0
 */
@RestController
@Slf4j
public class Server {


    /**
     * 所有服务器的检测结果
     */
    public static Map<String, List<InspectionResult>> resultMap;

    static {
        // expire map data according to the interval
        Schedule inspectSchedule = DataCenter.INSPECT_SCHEDULE;
        Schedule reportSchedule = DataCenter.REPORT_SCHEDULE;
        int minInterval = Math.min(inspectSchedule.getInterval(), reportSchedule.getInterval());
        resultMap = ExpiringMap.builder()
                .maxSize(1000)
                .expiration(minInterval, TimeUnit.SECONDS)
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .variableExpiration()
                .build();
    }

    /**
     * 将url解析带的参数解析成map，存放至{@link #resultMap} 中
     * 如果所有服务器的检测结果都接收到了就发送邮件
     */
    @PostMapping("")
    public void handleResult(String token, String key, String result){
        log.info("Received data from server: {}, the inspect result is: {}", key,result);
        if(DataCenter.SERVER_TOKEN.equals(token)){
            List<InspectionResult> decode = GenericUtil.decodeResult(result);
            resultMap.put(key,decode);
        }

    }

}
