package com.jgdabc.reggle_demo_jgdabc;

import org.junit.jupiter.api.Test;

public class UploadFileTest {
    @Test
   public  void test1()
   {
        String fileName = "dsnjksdnkjsd.jpg";
       String substring = fileName.substring(fileName.lastIndexOf("."));
       System.out.println(substring);

   }
}
