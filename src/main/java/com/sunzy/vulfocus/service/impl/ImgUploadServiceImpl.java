package com.sunzy.vulfocus.service.impl;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.common.SystemConstants;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.service.ImgUploadService;
import com.sunzy.vulfocus.utils.Utils;
import com.sunzy.vulfocus.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ImgUploadServiceImpl implements ImgUploadService {

    @Override
    public Result upload(MultipartFile file) {
        String basePath = SystemConstants.IMG_UPLOAD_DIR;
        UserDTO user = UserHolder.getUser();
        if(!user.getSuperuser()){
            return Result.fail("权限不足");
        }

        if(file == null){
            return Result.fail("请上传图片");
        }
        // file是一个临时文件，后续需要进行转存
        // file需要与前端上传的参数名保持一致，否则无法获取到上传的文件
        // 转存之前需要对文件名进行处理
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = Utils.getUUID() + suffix;
        if(!SystemConstants.ALLOWED_IMG_SUFFIX.contains(suffix)){
            return Result.fail("不支持此格式图片文件，请上传该格式文件");
        }
        File dir = new File(basePath); // 判断文件夹是否存在不存在则创建
        if (!dir.exists()){
            dir.mkdir();
        }
        // 将图片保存的指定位置
        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("上传失败！");
        }

        return Result.ok("上传成功",filename);
    }
}
