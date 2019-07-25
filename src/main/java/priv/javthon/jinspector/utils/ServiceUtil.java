package priv.javthon.jinspector.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 服务检查工具类
 * @author xxf
 * @version 1.0
 */
public class ServiceUtil {

    /**
     * windows查看运行服务指令
     */
    private static final String COMMAND_SERVICE = "net start";
    /**
     * windows查看进程指令
     */
    private static final String COMMAND_TASK = "tasklist";
    /**
     * windows查看网络状态指令
     */
    private static final String COMMAND_PORT_LISTENING = "netstat -na";
    /**
     * windows查看IP指令
     */
    private static final String COMMAND_IP_CONFIG_WINDOWS = "ipconfig";
    /**
     * Linux查看IP指令
     */
    private static final String COMMAND_IP_CONFIG_LINUX = "ifconfig";


    /**
     * 判断Windows被检测服务是否在运行
     * @param serviceName 服务名称
     * @return true or false
     */
    private static boolean isServiceRunnung(String serviceName){
        return execute(COMMAND_SERVICE, serviceName);
    }

    /**
     * 判断Windows被检测进程是否在运行
     * @param taskName 进程名
     * @return true or false
     */
    private static boolean isTaskRunnung(String taskName){
        return execute(COMMAND_TASK, taskName);
    }

    /**
     * 判断Windows被检测端口是否在运行
     * @param port 端口号
     * @return true or false
     */
    private static boolean isPortListening(String port){
        return execute(COMMAND_PORT_LISTENING, port);
    }


    /**
     * 执行指令判断有没有匹配的字符串
     * @param command 指令
     * @param name 所需字符串
     * @return true or false
     */
    public static boolean execute(String command, String name){
        try {
            Process process = Runtime.getRuntime().exec(command);//查询所有启动的服务
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));//注意中文编码问题
            StringBuilder result = new StringBuilder();
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                result.append(temp);
            }//result结果中判断服务是否存在
            bufferedReader.close();
            inputStream.close();
            return result.toString().contains(name);
        } catch (IOException e) {
            return false;
        }
    }


    public static void main(String[] args) {

        boolean python = ServiceUtil.execute(args[0],args[1]);
        System.out.println(python);
    }
}
