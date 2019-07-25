package priv.javthon.jinspector.inspector.component;


import priv.javthon.jinspector.entity.InspectionResult;
import priv.javthon.jinspector.utils.InspectUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class WebInspector extends AbstractInspector {

    @Override
    public List<InspectionResult> inspect(List<Map<String, Object>> connections) {
        for(Map<String,Object> connection: connections){
            Map<String,Object> temp = (Map<String, Object>) connection.get("connection");
            if(temp!=null){
                String urlStr = String.valueOf(temp.get("url"));
                if("null".equals(urlStr)){
                    throw new NullPointerException("web url is required, please check the expose.yml");
                }
                URL url;
                try {
                    url = new URL(urlStr);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("web url format is incorrect, please check the expose.yml");
                }
                String name = String.valueOf(temp.get("name"));
                if("null".equals(name)){
                    name = url.getHost()+"#web";
                }
                String description = String.valueOf(temp.get("description"));
                if("null".equals(description)){
                    description="No description";
                }
                boolean expected = InspectUtil.isWebAccessible(urlStr);
                addResult(name, description,  "expose", expected);
            }
        }
        return results;
    }

}
