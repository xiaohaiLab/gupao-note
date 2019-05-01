package com.edu.factory.factorymethod;

import com.edu.factory.ICourse;

/**
 * @Description TODO
 * @Author "zhouhai"
 * @Date2019/5/112:18
 **/
public class FactoryMethodTest {

    public static void main(String[] args) {
        ICourseFactory factory = new PythonCourseFactory();

        ICourse course = factory.course();

        course.record();
    }

}
