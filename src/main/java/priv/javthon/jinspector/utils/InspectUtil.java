package priv.javthon.jinspector.utils;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import priv.javthon.jinspector.entity.InspectionResult;
import priv.javthon.jinspector.inspector.component.DataCenter;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspectUtil {



    /**
     * @param url
     * @return 响应的数据
     */
    public static boolean isWebAccessible(String url) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(url);
            httpclient.execute(httpget);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isPortListening(String host, int port) {
        try {
            new Socket(host,port);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void sendResult(List<InspectionResult> localInspection){
        String token = DataCenter.SERVER_TOKEN; //TODO Springboot启动时校验
        String jsonString = GenericUtil.encodeResult(localInspection);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("key", DataCenter.getHostId());
        map.put("result", jsonString);
        HttpClientUtil.doPost(DataCenter.SERVER_URL, map);
    }

    public static void main(String[] args) {
        boolean portListening = InspectUtil.isWebAccessible("nanmatou.club");
        System.out.println(portListening);
    }

}
