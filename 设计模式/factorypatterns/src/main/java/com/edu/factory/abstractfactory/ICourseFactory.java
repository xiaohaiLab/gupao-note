package com.edu.factory.abstractfactory;

import com.edu.factory.ICourse;

//要求所有的子工厂都实现这个工厂
//(一个品牌的抽象)

/**
 * TODO 抽象工厂不符合开闭原则，扩展性强
 * TODO 适用场景: 客户端不依赖于产品类实例如何被创建、实现等细节
 * TODO 强调一系列相关产品对象（属于同一产品族）一起使用创建对象需要大量重复的代码
 * TODO 提供一个产品类的库,所有的产品以同样的接口出现，从而使客户端不依赖于具体实现
 * TODO 规定了所有可能被创建的产品集合，产品族中扩展新的产品困难，需要修改抽象工厂的接口，增加了系统的抽象性和理解难度
 */
public interface ICourseFactory {

    ICourse createCourese();

    INote createNote();

    ICourse createSource();

    IVideo createVideo();

}
