package priv.javthon.jinspector.inspector.component;


import priv.javthon.jinspector.entity.InspectionResult;
import priv.javthon.jinspector.utils.InspectUtil;

import java.util.List;
import java.util.Map;

public class PortInspector extends AbstractInspector {

    @Override
    public List<InspectionResult> inspect(List<Map<String, Object>> connections) {
        for(Map<String,Object> connection: connections){
            Map<String,Object> temp = (Map<String, Object>) connection.get("connection");
            if(temp!=null){
                String name = String.valueOf(temp.get("name"));
                String host = String.valueOf(temp.get("host"));
                if("null".equals(host)){
                    throw new NullPointerException("general host is required, please check the expose.yml");
                }
                Integer port;
                try{
                    port = (Integer) temp.get("port");
                }catch (ClassCastException e){
                    throw new NumberFormatException("general port must be an integer value, please check the expose.yml");
                }
                if(port==null){
                    throw new NullPointerException("general port is required, please check the expose.yml");
                }
                if("null".equals(name)){
                    name=port+"#port";
                }
                String description = String.valueOf(temp.get("description"));
                if("null".equals(description)){
                    description="No description";
                }
                boolean accessible = InspectUtil.isPortListening(host, port);
                addResult(name, description,  "expose", accessible);
            }
        }
        return results;
    }

}
