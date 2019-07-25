package priv.javthon.jinspector.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InspectionResult {
    private String name;
    private boolean isExpected;
    private String host;
    private String description;
}
