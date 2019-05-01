package com.edu.factory.factorymethod;

import com.edu.factory.ICourse;
import com.edu.factory.PythonCourse;

/**
 * @Description TODO
 * @Author "zhouhai"
 * @Date2019/5/111:52
 **/
public class PythonCourseFactory implements ICourseFactory{


    public ICourse course() {return new PythonCourse();
    }
}
