package com.springboot.dubbo.dubboprovider;

import com.gupaoedu.dubbo.ISayHelloService;
import org.apache.dubbo.config.annotation.Service;

/**
 * 腾讯课堂搜索【咕泡学院】
 * 官网：www.gupaoedu.com
 * 风骚的Mic 老师
 * create-date: 2019/7/21-20:32
 */
@Service(loadbalance = "random",timeout = 50000,cluster = "failsafe")
public class SayHelloServiceImpl implements ISayHelloService {

    @Override
    public String sayHello() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Come in SayHello()");
        return "Hello Dubbo";
    }
}
