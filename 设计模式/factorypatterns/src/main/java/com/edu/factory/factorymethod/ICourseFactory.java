package com.edu.factory.factorymethod;

import com.edu.factory.ICourse;

/**
 * @Description TODO 定义一个创建对象的接口，但让实现这个接口的类来决定实例化哪个类，工厂方法让类的实例化推迟到子类中进行
 * TODO 使用场景:  创建对象需要大量重复的代码，客户端不依赖产品类实例如何被创建、实现等细节，一个类通过其子类指定创建哪个对象，复合开闭原则，提高系统的可扩展性   
 * @Author "zhouhai"
 * @Date2019/5/111:51
 **/
public interface ICourseFactory {

    ICourse course();

}
