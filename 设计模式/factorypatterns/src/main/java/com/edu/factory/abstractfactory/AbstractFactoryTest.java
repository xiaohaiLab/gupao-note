package com.edu.factory.abstractfactory;

/**
 * @Description TODO
 * @Author "zhouhai"
 * @Date2019/5/113:23
 **/
public class AbstractFactoryTest {

    public static void main(String[] args) {
        JavaCourseFactory javafactory = new JavaCourseFactory();
        javafactory.createCourese().record();
        javafactory.createNote();
        javafactory.createVideo();
    }

}
