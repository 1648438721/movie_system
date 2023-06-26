package com.system.controller;
import com.system.common.Result;
import com.system.utils.OSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/oss")
public class OssController {
    @Autowired
    private OSSUtil ossUtils;
    /*** @Description: 上传单个文件*/
    @PostMapping("/uploadOneFile")
    public Result uploadFile(@RequestParam("file") MultipartFile file)
    {
        String imgs = ossUtils.uploadOneFile(file);
        if (imgs == null) {
            return Result.fail("上传文件失败");
        }
        return Result.success(imgs);
    }
}