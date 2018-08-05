package org.myapp.event;


import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

/**
 * 开发一个简单的Esper程序，需要了解如下的API：
 *
 * 1> Configuration 引擎配置API，可以配置关系型数据访问的一些必须参数、Virtual Data Window (虚拟数据窗)、插件等。
 *
 * 2> EPServiceProvider，定义Esper服务提供，也就是Esper引擎。通过EPServiceProvierManager.getDefaultProvder()获取默认的引擎实例。
 *
 * 3> EPAdministrator，statement管理接口，管理引擎实例注册的EPL statement。
 *
 * 4> EPStatement  一个EPL语句。通过EPAdministrator.createEPL这一类的API创建。
 *
 * 5> EPRuntime 运行接口，通过EPServceProvider.getEPRuntime获取。其作用是向引擎实例发送事件。
 *
 * 其中Configuration中很多配置无需设置，只有在使用plug-in或者关系型数据访问的时候，必须设置相关参数。
 */
public class Main {

    public static void main(String[] args) {
        // 创建配置
        Configuration config = new Configuration();

        //添加包路径，这样在查询表达式中就不需要写类的全路径了
        config.addEventTypeAutoName("org.myapp.event");

        // 获取默认的引擎实例
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
        String expression = "select avg(price) from OrderEvent.win:time(30 sec)";

        //注册EPL，获取statement
        EPStatement statement = epService.getEPAdministrator().createEPL(expression);

        // 通过对statement添加监听，引擎实例会将statement的运行结果提供给监听程序
        MyListener listener = new MyListener();
        statement.addListener(listener);

        // 发送事件
        OrderEvent event = new OrderEvent("shirt", 74.50);
        epService.getEPRuntime().sendEvent(event);
    }

}