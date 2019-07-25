package priv.javthon.jinspector.template;


import priv.javthon.jinspector.entity.InspectionResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MailTemplate {


  public static String generatorReport(Map<String, List<InspectionResult>> inspectResult){
    StringBuilder result = new StringBuilder();

    result.append("<table width=\"668\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:501.00pt;border-collapse:collapse;table-layout:fixed;\">\n"
        + "   <colgroup><col width=\"129\" style=\"mso-width-source:userset;mso-width-alt:4128;\">\n"
        + "   <col width=\"110\" style=\"mso-width-source:userset;mso-width-alt:3520;\">\n"
        + "   <col width=\"357\" style=\"mso-width-source:userset;mso-width-alt:11424;\">\n"
        + "   <col width=\"72\" style=\"width:54.00pt;\">\n"
        + "   </colgroup><tbody><tr height=\"18\" style=\"height:13.50pt;\">\n"
        + "    <td class=\"xl65\" height=\"18\" width=\"129\" style=\"height:13.50pt;width:96.75pt;border: 1.0pt double; text-align: center\" x:str=\"\">Host</td>\n"
        + "    <td class=\"xl65\" width=\"110\" style=\"width:82.50pt;border: 1.0pt double; text-align: center\" x:str=\"\">Service</td>\n"
        + "    <td class=\"xl65\" width=\"357\" style=\"width:277.75pt;border: 1.0pt double; text-align: center\" x:str=\"\">Description</td>\n"
        + "    <td class=\"xl65\" width=\"72\" style=\"width:54.00pt;border: 1.0pt double; text-align: center\" x:str=\"\">Status</td>\n"
        + "   </tr>" );

    Set<String> keys = inspectResult.keySet();
    for(String key: keys){
      List<InspectionResult> list = inspectResult.get(key);
      for(int i=0;i<list.size();i++){
        if(i==0){
          result.append("<tr height=\"18\" style=\"height:13.5pt;\">\n"
              + "        <td class=\"xl65\" height=\"220\" rowspan=\""+list.size()+"\" style=\"height:165.00pt;border:1.0pt double windowtext;\">"+list.get(i).getHost()+"</td>\n"
              + "        <td class=\"xl65\" style=\"border: 1.0pt double\">"+list.get(i).getName()+"</td>\n"
              + "        <td class=\"xl65\" style=\"border: 1.0pt double\">"+list.get(i).getDescription()+"</td>\n");
          if(list.get(i).isExpected()){
            result.append(" <td x:str=\"\" style='color: green; border: 1.0pt double; text-align: center'>OK</td>\n");
          }else{
            result.append(" <td x:str=\"\" style='color: red; border: 1.0pt double; text-align: center'>NO</td>\n");
          }
          result.append("    </tr>");
        }else{
          result.append("<tr height=\"18\" style=\"height:13.5pt;\">\n"
              + "        <td class=\"xl66\" style=\"border: 1.0pt double\">"+list.get(i).getName()+"</td>\n"
              + "        <td class=\"xl66\" style=\"border: 1.0pt double\">"+list.get(i).getDescription()+"</td>\n");
          if(list.get(i).isExpected()){
            result.append(" <td x:str=\"\" style='color: green; border: 1.0pt double; text-align: center'>OK</td>\n");
          }else{
            result.append(" <td x:str=\"\" style='color: red; border: 1.0pt double; text-align: center'>NO</td>\n");
          }
          result.append("    </tr>");
        }
      }
    }
    result.append("</tbody></table>");

    return result.toString();
  }
}
