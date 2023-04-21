package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.LayoutDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.*;
import com.sunzy.vulfocus.mapper.LayoutMapper;
import com.sunzy.vulfocus.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.service.LayoutService;
import com.sunzy.vulfocus.utils.Utils;
import com.sunzy.vulfocus.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static com.sunzy.vulfocus.common.ErrorClass.JSONTOYAMLException;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-17
 */
@Service
@Transactional
public class LayoutServiceImpl extends ServiceImpl<LayoutMapper, Layout> implements LayoutService {
    @Resource
    private LayoutServiceService layoutServiceService;

    @Resource
    private LayoutDataService layoutDataService;

    @Resource
    private ImageInfoService imageService;

    @Resource
    private NetWorkInfoService networkService;

    @Resource
    private LayoutServiceNetworkService layoutServiceNetworkService;

    @Resource
    private LayoutServiceContainerService layoutContainerService;

    @Resource
    private LayoutServiceContainerScoreService layoutScoreService;

    @Resource
    private SysLogService logService;

    /**
     * 创建或者更新场景信息
     * @param layoutDTO 前端传递参数
     * @return 响应信息
     */
    @Override
    public Result CreateLayout(LayoutDTO layoutDTO) {
        UserDTO user = UserHolder.getUser();
        if(!user.getSuperuser()){
            return Result.fail("权限不足");
        }
        Object data = layoutDTO.getData();

        String name = layoutDTO.getName();
        String desc = layoutDTO.getDesc();
        String id = layoutDTO.getId();
        if(StrUtil.isBlank(name)){
            return Result.fail("名称不能为空");
        }
        if(data == null){
            return Result.fail("参数不能为空");
        }
        String jsonStr = "{'nodes': [{'name': 'Container', 'type': 'Container', 'id': '63yorm3qmro0', 'x': 140, 'y': 120, 'icon': 'data:image/png;base64,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', 'width': 200, 'height': 120, 'initW': 200, 'initH': 120, 'classType': 'T1', 'isLeftConnectShow': False, 'isRightConnectShow': True, 'containNodes': [], 'attrs': {'id': 'f009a105-44f7-4111-8e5a-cecc556515af', 'name': 'redis:3.2-alpine', 'desc': 'redis', 'port': '6379', 'open': True, 'vul_name': 'redis', 'raw': {'image_id': 'f009a105-44f7-4111-8e5a-cecc556515af', 'status': {'status': '', 'is_check': False, 'container_id': '', 'start_date': '', 'end_date': '', 'host': '', 'port': '', 'progress': 0, 'progress_status': '', 'task_id': '', 'now': 1607486074}, 'image_name': 'redis:3.2-alpine', 'image_vul_name': 'redis', 'image_port': '6379', 'image_desc': 'redis', 'rank': 2.5, 'is_ok': True, 'is_share': False, 'create_date': '2020-12-07T16:45:17.387204', 'update_date': '2020-12-07T16:45:17.420568'}}, 'isSelect': False}, {'name': 'Network', 'type': 'Network', 'id': '1860gad1nlpc', 'x': 440, 'y': 200, 'icon': 'data:image/png;base64,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', 'width': 200, 'height': 100, 'initW': 200, 'initH': 100, 'classType': 'T1', 'isLeftConnectShow': True, 'isRightConnectShow': False, 'containNodes': [], 'attrs': {'id': 'd9f9a0fb-c0d0-4831-9821-9947028c5a73', 'name': 'network-002', 'subnet': '172.13.2.0/24', 'gateway': '172.13.2.1', 'raw': {'net_work_id': 'd9f9a0fb-c0d0-4831-9821-9947028c5a73', 'net_work_client_id': '0489dcf918d39034bd39b96c6cb33f596a8cfc2262791d657d9974a6ffc20be2', 'create_user': 1, 'net_work_name': 'network-002', 'net_work_subnet': '172.13.2.0/24', 'net_work_gateway': '172.13.2.1', 'net_work_scope': 'local', 'net_work_driver': 'bridge', 'enable_ipv6': False, 'create_date': '2020-12-08T16:29:10.312053', 'update_date': '2020-12-08T16:29:10.312070'}}, 'isSelect': False}, {'name': 'Container', 'type': 'Container', 'id': 'gargmjrhv2o', 'x': 140, 'y': 280, 'icon': 'data:image/png;base64,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', 'width': 200, 'height': 120, 'initW': 200, 'initH': 120, 'classType': 'T1', 'isLeftConnectShow': False, 'isRightConnectShow': True, 'containNodes': [], 'attrs': {'id': 'f009a105-44f7-4111-8e5a-cecc556515af', 'name': 'redis:3.2-alpine', 'desc': 'redis', 'port': '6379', 'open': False, 'vul_name': 'redis', 'raw': {'image_id': 'f009a105-44f7-4111-8e5a-cecc556515af', 'status': {'status': '', 'is_check': False, 'container_id': '', 'start_date': '', 'end_date': '', 'host': '', 'port': '', 'progress': 0, 'progress_status': '', 'task_id': '', 'now': 1607486081}, 'image_name': 'redis:3.2-alpine', 'image_vul_name': 'redis', 'image_port': '6379', 'image_desc': 'redis', 'rank': 2.5, 'is_ok': True, 'is_share': False, 'create_date': '2020-12-07T16:45:17.387204', 'update_date': '2020-12-07T16:45:17.420568'}}, 'isSelect': True}], 'connectors': [{'id': 'm9o84w8vp1', 'type': 'Line', 'strokeW': 3, 'color': '#768699', 'targetNode': {'x': 440, 'y': 200, 'id': '1860gad1nlpc', 'width': 200, 'height': 100}, 'sourceNode': {'x': 140, 'y': 120, 'id': '63yorm3qmro0', 'width': 200, 'height': 120}, 'isSelect': False}, {'id': '1ebww27fghe', 'type': 'Line', 'strokeW': 3, 'color': '#768699', 'targetNode': {'x': 440, 'y': 200, 'id': '1860gad1nlpc', 'width': 200, 'height': 100}, 'sourceNode': {'x': 140, 'y': 280, 'id': 'gargmjrhv2o', 'width': 200, 'height': 120}}]}";

        jsonStr =(String) layoutDTO.getData();
        JSONObject jsonObj = new JSONObject(jsonStr);
        Set<Map.Entry<String, Object>> entries = jsonObj.entrySet();
        boolean checkOpen = false;
        ArrayList<JSONObject> connectors = new ArrayList<>();
        ArrayList<JSONObject> nodes = new ArrayList<>();
        HashMap<String, JSONObject> networkDict = new HashMap<>();
        ArrayList<String> checkNetworkNameList = new ArrayList<>();
        JSONArray connectorsJSONArray = new JSONArray();
        JSONArray nodesJSONArray = new JSONArray();
        for (Map.Entry<String, Object> entry : entries) {
            if ("connectors".equals(entry.getKey())) {
                connectorsJSONArray = JSONUtil.parseArray(entry.getValue());
            } else if ("nodes".equals(entry.getKey())) {
                nodesJSONArray = JSONUtil.parseArray(entry.getValue());
            }
        }

        for (Object object : connectorsJSONArray) {
            connectors.add((JSONObject) object);
        }

        for (Object object : nodesJSONArray) {
            nodes.add((JSONObject) object);
        }
        ArrayList<JSONObject> containerNodes = new ArrayList<>();
//        ArrayList<JSONObject> networkNodes = new ArrayList<>();
        for (JSONObject node : nodes) {
            String nodeStr = node.toString();
            JSONObject nodeJson = new JSONObject(nodeStr);
            String nodeId = nodeJson.get("id").toString();
            JSONObject nodeAttrs = new JSONObject(nodeJson.get("attrs").toString());
            if (nodeAttrs.size() == 0) {
                return Result.fail("节点属性不能为空");
            }

            if ("Container".equals(nodeJson.get("type"))) {
                boolean nodeOpen = (boolean) nodeAttrs.get("open");
                String nodePort = nodeAttrs.get("port").toString();
                if (nodeOpen && !StrUtil.isBlank(nodePort)) {
                    checkOpen = true;
                }
                containerNodes.add(nodeJson);
            } else if ("Network".equals(nodeJson.get("type"))) {
                String networkName = nodeJson.get("name").toString();
                if (StrUtil.isBlank(networkName)) {
                    return Result.fail("网卡不能为空");//网卡不能为空
                }
                if (checkNetworkNameList.contains(networkName)) {
                    return Result.fail("不能重复设置网卡");//不能重复设置网卡
                }
                checkNetworkNameList.add(networkName);
                networkDict.put(nodeId, node);
            }
        }

        if (!checkOpen) {
            return Result.fail("请开放可访问入口");//请开放可访问入口
        }
        if (containerNodes.size() == 0) {
            return Result.fail("容器环境不能为空");// 容器环境不能为空
        }

        if (networkDict.size() == 0) {
            for (JSONObject containerNode : containerNodes) {
                JSONObject nodeAttrs = new JSONObject(containerNode.get("attrs").toString());
                boolean nodeOpen = (boolean) nodeAttrs.get("open");
                if (!nodeOpen) {
                    return Result.fail("在不配置网卡段情况下请保证所有的环境开放访问权限"); // 在不配置网卡段情况下请保证所有的环境开放访问权限
                }
            }
        } else if (connectors.size() == 0) {
            return Result.fail("在配置网卡的情况下连接点不能为空"); // 在配置网卡的情况下连接点不能为空
        }
        // TODO:构建 dockercompose.yml 文件
        try {
            JSONObject ymlContent = buildYml(containerNodes, networkDict, connectors);
            System.out.println(ymlContent.toString());
            JSONObject ymlData = (JSONObject)ymlContent.get("content");
            ArrayList envData = JSON.parseObject(ymlContent.get("env").toString(), ArrayList.class);
//            ArrayList envData = (ArrayList)ymlContent.get("env");
            StringBuilder envContent = new StringBuilder();
            if(envData.size() > 0) {
                for(int i = 0; i < envData.size(); i ++){
                    envContent.append(envData.get(i)).append("\n");
                }
//                ArrayList envList = JSON.parseObject(envData.toString(), ArrayList.class);
//                for (Object env : envList) {
//                    envContent.append(env.toString()).append("\n");
//                }
            }

            Layout layout = new Layout();
            String operationName = "创建";
            if(!StrUtil.isBlank(id)){
                // id不为空说明该环境已经存在
                layout = getById(id);
            } else {
                layout.setLayoutId(Utils.getUUID());
                id = layout.getLayoutId();
                layout.setCreateDate(LocalDateTime.now());
                layout.setUpdateDate(LocalDateTime.now());
            }

            LayoutData layoutData = layoutDataService.query().eq("layout_id", layout.getLayoutId()).one();
            if(layoutData != null && "running".equals(layoutData.getStatus())){
                return Result.build("环境正在运行中，请首先停止运行", null);
            }
            layout.setLayoutName(name);
            layout.setLayoutDesc(desc);
            layout.setCreateUserId(user.getId());
            layout.setImageName(layoutDTO.getImg());
            layout.setRawContent(layoutDTO.getData().toString());
            try {
                String yaml = Utils.jsonToYaml(ymlData.toString());
                layout.setYmlContent(yaml);
            } catch(Exception e) {
                e.printStackTrace();
                throw JSONTOYAMLException;
            }
            layout.setEnvContent(envContent.toString());
            save(layout);
            List<com.sunzy.vulfocus.model.po.LayoutService> layoutServices = layoutServiceService.query().eq("layout_id", id).select("service_id").list();


            JSONObject services =(JSONObject) ymlData.get("services");
            Set<Map.Entry<String, Object>> entrySet = services.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                String serviceName = entry.getKey();
                JSONObject service = (JSONObject) entry.getValue();
                String image = service.get("image").toString();
                ImageInfo imageInfo = imageService.query().eq("image_name", image).one();
                if(imageInfo == null){
                    return Result.fail(image + "镜像不存在");
                }
                boolean isExpose = false;
                StringBuffer portsSB = new StringBuffer();
                if(service.get("ports") != null && !StrUtil.isBlank(service.get("ports").toString())){
                    isExpose = true;
                }
                String ports = "";
                if (!StrUtil.isBlank(imageInfo.getImagePort())){
                    ports = imageInfo.getImagePort();
                }
                com.sunzy.vulfocus.model.po.LayoutService layoutService = layoutServiceService.query().eq("layout_id", id).eq("service_name", serviceName).one();
                if (layoutService == null){
                    layoutService = new com.sunzy.vulfocus.model.po.LayoutService();
                    layoutService.setServiceId(Utils.getUUID());
                    layoutService.setLayoutId(id);
                    layoutService.setServiceName(serviceName);
                    layoutService.setCreateDate(LocalDateTime.now());
                    layoutService.setUpdateDate(LocalDateTime.now());
                }

                for (com.sunzy.vulfocus.model.po.LayoutService layoutServiceData : layoutServices) {
                    if (layoutServiceData.getServiceId().equals(layoutService.getServiceId())){
                        layoutServices.remove(layoutServiceData);
                        break;
                    }
                }
                layoutService.setImageId(imageInfo.getImageId());
                layoutService.setServiceName(serviceName);
                layoutService.setExposed(isExpose);
                layoutService.setExposedSourcePort(ports);
                layoutServiceService.save(layoutService);

                if(service.get("networks") == null){
                    continue;
                }
                List<LayoutServiceNetwork> serviceNetworkList = layoutServiceNetworkService.query().eq("service_id", id).select("layout_service_network_id").list();
//                JSONObject networks = (JSONObject) service.get("networks");
                ArrayList networks = JSON.parseObject(service.get("networks").toString(), ArrayList.class);
                for (Object network : networks) {
                    String networkName = (String) network;
                    NetWorkInfo netWorkInfo = networkService.query().eq("net_work_name", networkName).one();
                    if(netWorkInfo == null){
                        return Result.fail(networkName + "网卡不存在");
                    }
                  LayoutServiceNetwork layoutServiceNetwork = layoutServiceNetworkService.query().eq("service_id", id).eq("network_id", netWorkInfo.getNetWorkId()).one();
                    if (layoutServiceNetwork == null){
                        layoutServiceNetwork = new LayoutServiceNetwork();
                        layoutServiceNetwork.setLayoutServiceNetworkId(Utils.getUUID());
                        layoutServiceNetwork.setServiceId(id);
                        layoutServiceNetwork.setNetworkId(netWorkInfo.getNetWorkId());
                        layoutServiceNetwork.setCreateDate(LocalDateTime.now());
                        layoutServiceNetwork.setUpdateDate(LocalDateTime.now());

                    }
                    for (LayoutServiceNetwork serviceNetwork : serviceNetworkList) {
                        if(serviceNetwork.getLayoutServiceNetworkId().equals(layoutServiceNetwork.getLayoutServiceNetworkId())){
                            serviceNetworkList.remove(serviceNetwork);
                            break;
                        }
                    }
                    layoutServiceNetworkService.saveOrUpdate(layoutServiceNetwork);
                }
//                for (Map.Entry<String, Object> network : networkEntry) {
//                    String networkName = network.getKey();
//
//                }
                // 删除不存在的网卡
                if(serviceNetworkList.size() > 0){
                    for (LayoutServiceNetwork layoutServiceNetwork : serviceNetworkList) {
                        layoutServiceNetworkService.removeById(layoutServiceNetwork.getLayoutServiceNetworkId());
                    }
                }
            }
            // 删除服务数据
            for (com.sunzy.vulfocus.model.po.LayoutService layoutService : layoutServices) {
                String serviceId = layoutService.getServiceId();
                LambdaQueryWrapper<com.sunzy.vulfocus.model.po.LayoutService> deleteWrapper = new LambdaQueryWrapper<>();
                deleteWrapper.eq(true, com.sunzy.vulfocus.model.po.LayoutService::getServiceId, serviceId);
                deleteWrapper.eq(true, com.sunzy.vulfocus.model.po.LayoutService::getLayoutId, id);
                layoutServiceService.remove(deleteWrapper);
                if(layoutData != null){
                    // 删除容器
                    LambdaQueryWrapper<LayoutServiceContainer> deleteContainer = new LambdaQueryWrapper<>();
                    deleteContainer.eq(true, LayoutServiceContainer::getServiceId, serviceId);
                    deleteContainer.eq(true, LayoutServiceContainer::getLayoutUserId, layoutData.getLayoutUserId());
                    layoutContainerService.remove(deleteContainer);
                    // 删除分数
                    LambdaQueryWrapper<LayoutServiceContainerScore> deleteScore = new LambdaQueryWrapper<>();
                    deleteScore.eq(true, LayoutServiceContainerScore::getLayoutId, id);
                    deleteScore.eq(true, LayoutServiceContainerScore::getLayoutId, layoutData.getLayoutId());
                    deleteScore.eq(true, LayoutServiceContainerScore::getServiceId, serviceId);
                    layoutScoreService.remove(deleteScore);

                } else {
                    // 删除容器
                    LambdaQueryWrapper<LayoutServiceContainer> deleteContainer = new LambdaQueryWrapper<>();
                    deleteContainer.eq(true, LayoutServiceContainer::getServiceId, serviceId);
                    layoutContainerService.remove(deleteContainer);
                    // 删除分数
                    LambdaQueryWrapper<LayoutServiceContainerScore> deleteScore = new LambdaQueryWrapper<>();
                    deleteScore.eq(true, LayoutServiceContainerScore::getLayoutId, id);
                    deleteScore.eq(true, LayoutServiceContainerScore::getServiceId, serviceId);
                    layoutScoreService.remove(deleteScore);
                }

            }
            logService.sysLayoutLog(user, layout, operationName);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("服务器内部错误，请联系管理员");
        }
        return Result.ok();
    }

    private JSONObject buildYml(ArrayList<JSONObject> containerNodes,
                                       HashMap<String, JSONObject> networkDict,
                                       ArrayList<JSONObject> connectors) {
        JSONObject ymlData = new JSONObject();
        JSONObject services = new JSONObject();
        JSONObject allNetworks = new JSONObject();
        ArrayList<Object> envList = new ArrayList<>();
        ArrayList<Object> imageList = new ArrayList<>();
//        ArrayList<Object> networkList = new ArrayList<>();

        Set<Map.Entry<String, JSONObject>> entries = networkDict.entrySet();
        for (Map.Entry<String, JSONObject> networkEntry : entries) {
            JSONObject networkJSON = networkEntry.getValue();
            JSONObject attrs = new JSONObject(networkJSON.get("attrs").toString());
            String networkName = attrs.get("name").toString();
            JSONObject external = new JSONObject();
            external.set("external", true);
            allNetworks.set(networkName, external);
        }

        for (JSONObject containerNode : containerNodes) {
            String id = containerNode.get("id").toString();
            JSONObject attrs = new JSONObject(containerNode.get("attrs").toString());
            String imageName = attrs.get("name").toString();
            boolean open = (boolean) attrs.get("open");
            String port = attrs.get("port").toString();
            ArrayList<String> portList = new ArrayList<>();
            ArrayList<String> networkList = new ArrayList<>();
            if (open && !StrUtil.isBlank(port)) {
                String[] portArr = port.split(",");
                for (String tmpPort : portArr) {
                    String baseTargetPort = id + "-" + tmpPort;
                    String encodeBaseTargetPort = Base64.encode(baseTargetPort);
                    char[] encodeHex = HexUtil.encodeHex(encodeBaseTargetPort, StandardCharsets.UTF_8);
                    encodeBaseTargetPort = "VULFOCUS" + new String(encodeHex);
                    if (!envList.contains(encodeBaseTargetPort)) {
                        envList.add(encodeBaseTargetPort);
                    }
                    String portStr = "${" + encodeBaseTargetPort + "}:" + tmpPort + "";
                    portList.add(portStr);
                }
            }
            JSONObject service = new JSONObject();
            service.set("image", imageName);
            if (portList.size() > 0) {
                service.set("ports", portList);
            }

            for (JSONObject connector : connectors) {
                JSONObject targetNode = new JSONObject(connector.get("targetNode").toString());
                String targetNodeId = targetNode.get("id").toString();
                JSONObject sourceNode = new JSONObject(connector.get("sourceNode").toString());
                String sourceNodeId = sourceNode.get("id").toString();
                String network = null;
                if (id.equals(targetNodeId)) {
                    JSONObject networkJson = networkDict.get(sourceNodeId);
                    network = new JSONObject(networkJson.get("attrs").toString()).get("name").toString();
                } else if (id.equals(sourceNodeId)) {
                    JSONObject networkJson = networkDict.get(targetNodeId);
                    network = new JSONObject(networkJson.get("attrs").toString()).get("name").toString();
                }
                if (network != null) {
                    networkList.add(network);
                }
            }
            if(networkList.size() > 0){
                service.set("networks", networkList);
            }
            services.set(id, service);
            HashMap<String, Object> imageMap = new HashMap<>();
            imageMap.put("open", open);
            imageMap.put("image_id", attrs.get("id"));
            imageMap.put("networks", networkList);
            imageList.add(imageMap);
        }
        ymlData.set("version", "3.2");
        ymlData.set("services", services);
        if(allNetworks.size() > 0){
            ymlData.set("networks", allNetworks);
        }


        JSONObject ymlContent = new JSONObject();
        ymlContent.set("content", ymlData);
        ymlContent.set("env", envList);

        return ymlContent;
    }

}
