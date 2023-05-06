# vulfocus of java
原项目地址 [vulfocus](https://github.com/fofapro/vulfocus)。

使用java重构vulfocus后端项目 3.2.3版本

## 环境搭建

- java 8

- springboot 2.3.7.RELEASE

- mysql5.7

- docker Desktop

  Docker version 20.10.24, build 297e128

- RabbitMQ

- redis

## 使用技术

- JWT + redis 单点登录
- Threadlocal
- docker-api
- docker-compose
- yml文件构建
- RabbitMQ死信队列执行延时任务
- 前后端分离
- springboot异步执行@Asyc
- redis缓存

## 已实现功能

| 用户             | 镜像             | 容器           | 任务              | 系统日志         | 网卡模块 | 场景模式 | 计时模式 |
| ---------------- | ---------------- | -------------- | ----------------- | ---------------- | ---------------- | ---------------- | ---------------- |
| 注册、登录、注销 | 获取本地镜像，拉取镜像     | 启动容器       | 创建镜像任务      | 获取任务列表     | 创建网卡 | 构建docker-compose.yml | 计时模板 |
| 获取全部用户     | 批量导入本地镜像 | 停止容器       | 创建/启动容器任务 | 获取系统配置     | 删除网卡 | 创建场景 | 计时题目列表 |
| 获取用户排名     | 获取镜像列表     | 删除容器       | 停止容器任务      | 记录镜像相关日志 | 获取所有网卡 | 启动,关闭场景 | 计时模块排行 |
| 修改密码         | 修改镜像信息     | 获取容器列表   | 删除容器任务      | 记录容器相关日志 |  | 删除场景 | 计时模式关闭与开启 |
| 获取用户信息     | 删除镜像         | 校验flag       | 获取单个任务信息  | 记录用户相关日志 |  | 获取场景信息           |            |
|                  | 由镜像创建容器   | 倒计时关闭容器 | 批量获取任务信息  |                  |  | 提交flag，并计分 |  |

- 创建场景，就是使用docker-compose创建容器，实现复杂网络环境漏洞的复现，需要使用上面的网卡功能(实现docker-compose的功能)

  关键在于，将前端传递的图信息转换为docker-compose.yaml文件，使用docker-compose.yaml创建多个关联容器

  前端传递的图的节点关系

  ![image-20230421171330458](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230421171330458.png)

  转换为json格式就是下面的数据

  ```java
  {"nodes":[
      {"name":"Network","type":"Network","id":"2ihp4sdd27y0","x":360,"y":40,"icon":"data:image/png;base64,","width":200,"height":100,"initW":200,"initH":100,"classType":"T1","isLeftConnectShow":true,"isRightConnectShow":true,"containNodes":[],"attrs":{"id":"c392f65a-9302-4382-a668-53ccee8e4798","name":"demo","subnet":"192.168.5.1/24","gateway":"192.168.5.1","raw":{"net_work_id":"c392f65a-9302-4382-a668-53ccee8e4798","net_work_client_id":"3ac8dca0f95cf8a63f45a9e73f0d4b26bf0a8e1cea3d76817b305a1abad0ca2f","create_user":1,"net_work_name":"demo","net_work_subnet":"192.168.5.1/24","net_work_gateway":"192.168.5.1","net_work_scope":"local","net_work_driver":"bridge","enable_ipv6":false,"create_date":"2023-04-18T23:20:05.917299","update_date":"2023-04-18T23:20:05.917299"}},"isSelect":false},
      {"name":"Container","type":"Container","id":"49mc1oqura0","x":60,"y":160,"icon":"data:image/png;base64,","width":200,"height":120,"initW":200,"initH":120,"classType":"T1","isLeftConnectShow":false,"isRightConnectShow":true,"containNodes":[],"attrs":{"id":"34e27d68-f81b-47ad-baae-dda1a4edd55d","vul_name":"redis","name":"redis:latest","desc":"redis","port":"6379","open":true,"raw":{"image_id":"34e27d68-f81b-47ad-baae-dda1a4edd55d","status":{"status":"","is_check":false,"container_id":"","start_date":"","end_date":"","host":"","port":"","progress":0,"progress_status":"","task_id":"","now":1681831285},"image_name":"redis:latest","image_vul_name":"redis","image_port":"6379","image_desc":"redis","rank":2.5,"is_ok":true,"is_share":false,"create_date":"2023-04-18T23:20:55.651619","update_date":"2023-04-18T23:20:56.312785"}},"isSelect":false},
      {"name":"Container","type":"Container","id":"5hry5ipy6ok0","x":620,"y":220,"icon":"data:image/png;base64,","width":200,"height":120,"initW":200,"initH":120,"classType":"T1","isLeftConnectShow":true,"isRightConnectShow":false,"containNodes":[],"attrs":{"id":"34e27d68-f81b-47ad-baae-dda1a4edd55d","vul_name":"redis","name":"redis:latest","desc":"redis","port":"6379","open":true,"raw":{"image_id":"34e27d68-f81b-47ad-baae-dda1a4edd55d","status":{"status":"","is_check":false,"container_id":"","start_date":"","end_date":"","host":"","port":"","progress":0,"progress_status":"","task_id":"","now":1681831299},"image_name":"redis:latest","image_vul_name":"redis","image_port":"6379","image_desc":"redis","rank":2.5,"is_ok":true,"is_share":false,"create_date":"2023-04-18T23:20:55.651619","update_date":"2023-04-18T23:20:56.312785"}},"isSelect":true}],
      "connectors":[
          {"id":"1oo4tk0gc4m","type":"Line","strokeW":3,"color":"#768699","targetNode":{"x":360,"y":40,"id":"2ihp4sdd27y0","width":200,"height":100},"sourceNode":{"x":60,"y":160,"id":"49mc1oqura0","width":200,"height":120},"isSelect":false},
          {"id":"2poxxpk4rso","type":"Line","strokeW":3,"color":"#768699","targetNode":{"x":620,"y":220,"id":"5hry5ipy6ok0","width":200,"height":120},"sourceNode":{"x":360,"y":40,"id":"2ihp4sdd27y0","width":200,"height":100},"isSelect":false}]}
  ```

  所以后端要做的就是根据上面的json数据，构建出相对应的docker-compose.yml文件，如下图

  ```yaml
  networks:
    demo:
      external: true
  services:
    7h4vhg3c58w0:
      image: redis:latest
      networks:
      - demo
      ports:
      - ${VULFOCUS4E326730646D686E4D324D314F4863774C54597A4E7A6B3D}:6379
    t3a2i35fqv4:
      image: redis:latest
      networks:
      - demo
      ports:
      - ${VULFOCUS64444E684D6D6B7A4E575A78646A51744E6A4D334F513D3D}:6379
  version: '3.2'
  ```
  .env文件中保存映射端口的信息
  ```javascript
  VULFOCUS4E326730646D686E4D324D314F4863774C54597A4E7A6B3D=25138
  VULFOCUS64444E684D6D6B7A4E575A78646A51744E6A4D334F513D3D=20328
  ```


## TODO:5.5

花了一个月的时间，将上述提到的功能完成，前后端联调的过程很是折磨人，不想再改前端代码了。不过vue的鉴权和动态路由那一部分的代码还挺有意思的。

添加了时间模式，算是完成了所有的功能，但是测试不够多，可能还存在一些bug，后续遇到再改。

## 优化

### 镜像缓存优化

本想着把镜像信息按照每页的数量缓存到redis中，以减少数据库操作，提高效率，但是反而效率更低了，属实反向优化了

![image-20230429225623539](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230429225623539.png)

```java
List<String> imageStringList = stringRedisTemplate.opsForList()
    .range("cache:image:" + page, 0, -1);
if(imageStringList == null || imageStringList.size() == 0){
    wrapper.eq(true, ImageInfo::getOk, true);
    wrapper.orderBy(true, false, ImageInfo::getCreateDate);
    page(imageInfoPage, wrapper);
    // 向redis存数据
    List<ImageInfo> imageInfoList = imageInfoPage.getRecords();
    for (ImageInfo imageInfo : imageInfoList) {
        stringRedisTemplate.opsForList().rightPush("cache:image:" + page, JSON.toJSONString(imageInfo));
    }
} else {
    List<ImageInfo> imageInfoList = new ArrayList<>();
    // 从redis中取数据并解析
    for (String imageString : imageStringList) {
        ImageInfo imageInfo = JSON.parseObject(imageString, ImageInfo.class);
        imageInfoList.add(imageInfo);
    }
    imageInfoPage.setRecords(imageInfoList);
    imageInfoPage.setTotal(count());
}
```

![image-20230429225734252](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230429225734252.png)

![image-20230429225741398](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230429225741398.png)

加了缓存后的速度是没加的1/4，这是没想到的。

可能是json反序列化过程效率比较低导致的吧，所以个优化只能放弃。

### 用户rank优化

在统计用户分数时涉及到查询比较多，所以是一个很耗时的操作

优化前代码

```java
    @Override
    public Result getAllUser(int currentPage) {
        UserDTO user = UserHolder.getUser();
        System.out.println(user.getId());
        if (!user.getSuperuser()) {
            return Result.fail("权限不足！");
        }
//        LambdaQueryWrapper<UserUserprofile> queryWrapper = new LambdaQueryWrapper<>();
        Page<UserUserprofile> userprofilePage = new Page<>(currentPage, SystemConstants.PAGE_SIZE);
        page(userprofilePage);
        List<UserUserprofile> allUser = userprofilePage.getRecords();
        ArrayList<UserInfo> userInfos = new ArrayList<>();
        for (UserUserprofile userprofile : allUser) {
            userInfos.add(handleUserInfo(userprofile));
        }

        Page<UserInfo> userInfoPage = new Page<>();
        BeanUtil.copyProperties(userprofilePage, userInfoPage);
        userInfos.sort(new Comparator<UserInfo>() {
            @Override
            public int compare(UserInfo u1, UserInfo u2) {
                double diff = u2.getRank() - u1.getRank();
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0;
            }
        });
//        userInfos.sort(Comparator.comparing(UserInfo::getRank));
        userInfoPage.setRecords(userInfos);
        return Result.ok(userInfoPage);
    }
```

加入缓存优化的代码

```java
 @Override
    public Result getAllUser(int currentPage) {
        UserDTO user = UserHolder.getUser();
        System.out.println(user.getId());
        if (!user.getSuperuser()) {
            return Result.fail("权限不足！");
        }
        int total = count();
        int start = (currentPage - 1) * SystemConstants.PAGE_SIZE;
        int end = Math.min(currentPage * SystemConstants.PAGE_SIZE, total);
        Page<UserInfo> userInfoPage = new Page<>();
        List<UserInfo> userInfos = new ArrayList<>();
        List<String> userInfoStrs = stringRedisTemplate.opsForList().range("user:rank", start, end);
        if (userInfoStrs == null || userInfoStrs.size() == 0) {
            // 获取所有用户列表
            List<UserUserprofile> userList = list();
            total = userList.size();
            for (UserUserprofile userprofile : userList) {
                userInfos.add(handleUserInfo(userprofile));
            }


            userInfos.sort(new Comparator<UserInfo>() {
                @Override
                public int compare(UserInfo u1, UserInfo u2) {
                    double diff = u2.getRank() - u1.getRank();
                    if (diff > 0) {
                        return 1;
                    } else if (diff < 0) {
                        return -1;
                    }
                    return 0;
                }
            });
            userInfos = userInfos.subList(start, end);
            // 根据rank排序后将信息放入到redis
            for (UserInfo u : userInfos) {
                stringRedisTemplate.opsForList().rightPush("user:rank", JSON.toJSONString(u));
            }
        } else {
            for (String userInfoStr : userInfoStrs) {
                userInfos.add(JSON.parseObject(userInfoStr, UserInfo.class));
            }
        }

        userInfoPage.setRecords(userInfos);
        userInfoPage.setTotal(total);
        return Result.ok(userInfoPage);
    }
```

优化前后的效率对比

![image-20230430133724595](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230430133724595.png)

![image-20230430133655549](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230430133655549.png)

提升了至原来接近原来的1/7，此次优化很成功

# 效果展示

![image-20230427223800640](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230427223800640.png)

![image-20230430231553176](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230430231553176.png)

![image-20230501162806138](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230501162806138.png)



![image-20230427223852365](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230427223852365.png)

![image-20230505104813350](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230505104813350.png)

![image-20230427223920314](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230427223920314.png)



![image-20230427223934286](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230427223934286.png)



![image-20230427223954846](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230427223954846.png)



![image-20230427224011800](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230427224011800.png)

![image-20230505103702625](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230505103702625.png)

![image-20230505104000844](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230505104000844.png)

![image-20230430231528634](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230430231528634.png)

![image-20230427224127039](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230427224127039.png)

![image-20230427224024460](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230427224024460.png)



![image-20230427224033903](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230427224033903.png)



![image-20230427224047546](https://raw.githubusercontent.com/sunzhengyu99/image/master/img/image-20230427224047546.png)
