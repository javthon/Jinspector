package priv.javthon.jinspector.inspector.manager;

import priv.javthon.jinspector.entity.InspectionResult;
import priv.javthon.jinspector.inspector.component.AbstractInspector;
import priv.javthon.jinspector.inspector.component.DataCenter;
import priv.javthon.jinspector.utils.ServiceUtil;

import java.util.List;
import java.util.Map;

public class LocalInspector extends AbstractInspector {

    public static List<InspectionResult> inspect() {
        return new LocalInspector().inspect(DataCenter.LOCAL_SERVERS);
    }


    public List<InspectionResult> inspect(List<Map<String, Object>> servers) {
        if (servers != null) {
            for (Map<String, Object> key : servers) {
                Map<String, Object> server = (Map<String, Object>) key.get("server");
                String id = String.valueOf(server.get("id"));
                if ("null".equals(id)) {
                    throw new NullPointerException("id is required, please check the local.yml");
                }
                boolean isThisHost = DataCenter.isThisHost(id);
                if (isThisHost) {
                    String host = String.valueOf(server.get("name"));
                    if("null".equals(host)){
                        host=id;
                    }
                    List<Map<String, Object>> services = (List<Map<String, Object>>) server.get("services");
                    if(servers.size() == 0){
                        throw new NullPointerException("at least one service should be defined, please check the local.yml");
                    }
                    for (Map<String, Object> service : services) {
                        Map<String, Object> temp = (Map<String, Object>) service.get("service");
                        if(temp==null){
                            throw new NullPointerException("service is required, please check the local.yml");
                        }
                        String name = String.valueOf(temp.get("name"));
                        String inspectCommand = String.valueOf(temp.get("inspectCommand"));
                        String expectedResult = String.valueOf(temp.get("expectedResult"));
                        if("null".equals(expectedResult)){
                            throw new NullPointerException("expectedResult is required, please check the local.yml");
                        }
                        if("null".equals(name)){
                            name=expectedResult+"#service";
                        }
                        String description = String.valueOf(temp.get("description"));
                        if("null".equals(description)){
                            description="No description";
                        }
                        boolean available = ServiceUtil.execute(inspectCommand, expectedResult);
                        addResult(name, description, host, available);
                    }
                    break;
                }
            }
        }
        return results;
    }

    public static void main(String[] args) {
        List<InspectionResult> inspect = inspect();
        System.out.println(inspect);
    }
}
