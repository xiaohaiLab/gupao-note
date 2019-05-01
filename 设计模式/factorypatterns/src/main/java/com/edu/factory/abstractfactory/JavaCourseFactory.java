package com.edu.factory.abstractfactory;

import com.edu.factory.ICourse;
import com.edu.factory.JavaCourse;

/**
 * @Description TODO
 * @Author "zhouhai"
 * @Date2019/5/113:22
 **/
public class JavaCourseFactory implements ICourseFactory {


    public ICourse createCourese() {
        return new JavaCourse();
    }

    public INote createNote() {
        return new JavaNote();
    }

    public ICourse createSource() {
        return null;
    }

    public IVideo createVideo() {
        return new JavaVideo();
    }
}
