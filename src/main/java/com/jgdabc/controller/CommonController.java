package com.jgdabc.controller;

import com.jgdabc.common.R_;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

//进行文件的上传和现在的处理
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basepath;
    @PostMapping("/upload")
    public R_<String> upload(MultipartFile file) throws IOException {

//        file 是一个临时文件，需要转存到指定位置，否则本次请求完成后，临时文件就会自动被删除。
        log.info(file.toString());
//        获得原始的问价名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString()+suffix;
//        创建一个目录
        File file1 = new File(basepath);
        if(file1.exists())
        {
//            如果目录不存在，就创建出来
            file1.mkdirs();
        }
//        使用UUID重新生成文件名，防止文件名重复造成覆盖
        log.info("原始文件名{}",originalFilename);
        file.transferTo(new File(basepath+fileName));
        log.info("传输访问");
       return R_.success(fileName);

    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
//        输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basepath) + name);

//        输出流，通过输出流将文件写回到浏览器
//        要向浏览器写回数据，所以通过response响应对象进行写回
        ServletOutputStream outputStream = response.getOutputStream();
//       设置响应类型

        response.setContentType("image/jpeg");
        int len=0;
        byte[] bytes = new byte[1024];
        while((len=fileInputStream.read(bytes))!=-1)
       {
          outputStream.write(bytes,0,len);
          outputStream.flush();
       }
//      关闭资源
      outputStream.close();
      fileInputStream.close();


    }
}
