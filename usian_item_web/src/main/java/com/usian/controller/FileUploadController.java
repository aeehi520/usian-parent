package com.usian.controller;

import com.usian.utils.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName : FileUploadController
 * @Author : lenovo
 * @Date: 2021/1/6 20:14
 */
@RestController
@RequestMapping("/file")
public class FileUploadController {
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg","image/gif");
    /**
     * 图片上传
     */
    @RequestMapping("/upload")
    public Result fileUpload(MultipartFile file){
        try {
            String originalFilename = file.getOriginalFilename();
            //校验文件类型
            String contentType = file.getContentType();
            if (!CONTENT_TYPES.contains(contentType)){
                //文件类型不合法，直接返回
                return Result.error("文件类型不符合:"+originalFilename);
            }
            //检验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage==null){
                return Result.error("文件内容不合法:"+originalFilename);
            }
            //保存到服务器
            file.transferTo(new File("G:\\images\\"+originalFilename));
            //生成url 返回
            return Result.ok("http://image.usian.com/"+originalFilename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("服务器内部错误");
    }
}
