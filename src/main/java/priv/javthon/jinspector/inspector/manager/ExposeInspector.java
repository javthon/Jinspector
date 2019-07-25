package priv.javthon.jinspector.inspector.manager;


import priv.javthon.jinspector.entity.InspectionResult;
import priv.javthon.jinspector.inspector.component.AbstractInspector;
import priv.javthon.jinspector.inspector.component.DataCenter;
import priv.javthon.jinspector.inspector.component.PortInspector;
import priv.javthon.jinspector.inspector.component.WebInspector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExposeInspector {

    private final static String KEY_WEB="web";
    private final static String KEY_PORTS="ports";


    private static AbstractInspector inspector;

    public static List<InspectionResult> inspect() {
        List<InspectionResult> results = new ArrayList<>();
        Map<String, Object> connections = DataCenter.EXPOSE_CONNECTIONS;
        Set<String> keySet = connections.keySet();
        for(String key: keySet){
            List<Map<String,Object>> connectionList = (List<Map<String,Object>>) connections.get(key);
            if(KEY_WEB.equals(key)){
                inspector=new WebInspector();
            }
            if(KEY_PORTS.equals(key)){
                inspector=new PortInspector();
            }
            List<InspectionResult> inspect = inspector.inspect(connectionList);
            results.addAll(inspect);

        }
        return results;
    }

}
