package priv.javthon.jinspector.inspector.component;


import priv.javthon.jinspector.entity.InspectionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractInspector {
    /**
     * An inspector's result will be stored in this list
     */
    protected List<InspectionResult> results = new ArrayList<>();

    /**
     *
     */
    public abstract List<InspectionResult> inspect(List<Map<String, Object>> connections);

    /**
     * add inspection result to the list
     * @param name service name
     * @param description service description
     * @param host host ip
     * @param expected is it as expected
     */
    protected void addResult(String name, String description, String host, boolean expected){
        InspectionResult result = new InspectionResult();
        result.setName(name);
        result.setDescription(description);
        result.setHost(host);
        result.setExpected(expected);
        results.add(result);
    }

}
