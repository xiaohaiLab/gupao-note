package com.edu.factory.simplefactory;

import com.edu.factory.ICourse;
import com.edu.factory.PythonCourse;

/**
 * @Description TODO
 * @Author "zhouhai"
 * @Date2019/5/110:07
 **/
public class SimpleFactoryTest {

    public static void main(String[] args) {


        CourseFactory courseFactory = new CourseFactory();

       /* ICourse java = courseFactory.create("java");
        java.record();*/


        ICourse course = courseFactory.course(PythonCourse.class);
        course.record();



    }
}
