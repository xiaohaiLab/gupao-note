package com.edu.factory.simplefactory;

import com.edu.factory.ICourse;

/**
 * @Description TODO 客户端只需要传入工厂类的参数，对于如何创建对象的逻辑不需要关心，适合对象较少的情况，
 * TODO 缺点:工厂类的职责相对过重，增加新的产品时需要修改工厂类的判断逻辑，违背开闭原则，不易于扩展过于复杂的产品结构
 *
 *
 * @Author "zhouhai"
 * @Date2019/5/110:18
 **/
public class CourseFactory {

    /*public ICourse create(String name) {

        if ("java".equals(name)) {
            return new JavaCourse();
        } else {
            return null;
        }

    }*/


    public ICourse course(Class clazz) {
        try {
            if (null != clazz) {
                return (ICourse) clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
