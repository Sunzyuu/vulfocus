package com.sunzy.vulfocus.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.dockerjava.api.model.Image;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.mapper.ImageInfoMapper;
import com.sunzy.vulfocus.model.po.LocalImage;
import com.sunzy.vulfocus.service.ImageInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.DockerTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@Service
public class ImageInfoServiceImpl extends ServiceImpl<ImageInfoMapper, ImageInfo> implements ImageInfoService {

    @Resource
    private ImageInfoMapper imageInfoMapper;


    @Override
    public Result getLocalImages() {
        /**
         * data
         * :
         * [{name: "alpine:latest", flag: false, image_id: "9ed4aefc74"},…]
         * 0
         * :
         * {name: "alpine:latest", flag: false, image_id: "9ed4aefc74"}
         * 1
         * :
         * {name: "wurstmeister/zookeeper:latest", flag: false, image_id: "3f43f72cb2"}
         * msg
         * :
         * "OK"
         * status
         * :
         * 200
         */
        List<Image> images = DockerTools.imageList();

        LambdaQueryWrapper<ImageInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImageInfo::getOk, "0");
        List<ImageInfo> imageInfos = imageInfoMapper.selectList(wrapper);

        ArrayList<String> nameList = new ArrayList<>();
        for (ImageInfo imageInfo : imageInfos) {
            nameList.add(imageInfo.getImageName());
        }
        ArrayList<LocalImage> localImages = new ArrayList<>();
        for (Image image : images) {
            for (String tag : image.getRepoTags()) {
                LocalImage localImage = new LocalImage();
                localImage.setName(tag);
                localImage.setFlag(false);
                if(nameList.contains(tag)){
                    localImage.setFlag(true);
                }
                localImage.setId(getImageId(image.getId()).substring(0,10));
                localImages.add(localImage);
            }
        }
        return Result.ok(localImages);
    }

    @Override
    public boolean importImage() {
        // 1.从docker服务器中获取images,讲镜像信息导入数据库中
        List<Image> images = DockerTools.imageList();
        /**
         *
         *     imageinfo
         *      private String imageId;
         *
         *     private String imageName;
         *
         *     private String imageVulName;
         *
         *     private String imagePort;
         *
         *     private String imageDesc;
         *
         *     private Double rank;
         *
         *     @TableField("is_ok")
         *     private Boolean ok;
         *
         *     private LocalDateTime createDate;
         *
         *     private LocalDateTime updateDate;
         *
         *     @TableField("is_share")
         *     private Boolean share;
         *
         *     private String degree;
         *
         *     private String isStatus;
         */
// Image(created=1680113964, id=sha256:9ed4aefc74f6792b5a804d1d146fe4b4a2299147b0f50eaf2b08435d7b38c27e,
// parentId=, repoTags=[alpine:latest], repoDigests=[alpine@sha256:124c7d2707904eea7431fffe91522a01e5a861a624ee31d03372cc1d138a3126],
// size=7049701, virtualSize=7049701, sharedSize=-1, labels=null, containers=-1)
        List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
        for (Image image : images) {

        }

        try {

        } catch (Exception e){
            throw new RuntimeException(e);
        }



        return false;
    }



    private ImageInfo imageToImageInfo(Image image) {
        String id = getImageId(image.getId());
        String name = getImageName(image.getRepoTags());
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setImageId(id);
        imageInfo.setImageName(name);
        imageInfo.setImageVulName(name);

        imageInfo.setImagePort("80");
        imageInfo.setImageDesc(name);
        imageInfo.setDegree("1");
        imageInfo.setIsStatus("1");
        imageInfo.setOk(true);
        imageInfo.setShare(false);

        return imageInfo;
    }

    private String getImageId(String id) {
        return id.split(":")[1];
    }

    private String getImageName(String[] repoTags) {
        return repoTags[0];
    }
}
