package com.gupaoedu.spi;

import java.util.ServiceLoader;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ServiceLoader<DatabaseDriver> serviceLoader=ServiceLoader.load(DatabaseDriver.class);
        for(DatabaseDriver databaseDriver:serviceLoader){
            System.out.println(databaseDriver.buildConnect("test"));
        }
    }
}
