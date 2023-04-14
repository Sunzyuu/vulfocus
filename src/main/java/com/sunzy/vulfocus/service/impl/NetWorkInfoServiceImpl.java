package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.model.Network;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.NetworkDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.NetWorkInfo;
import com.sunzy.vulfocus.mapper.NetWorkInfoMapper;
import com.sunzy.vulfocus.service.NetWorkInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.DockerTools;
import com.sunzy.vulfocus.utils.GetIdUtils;
import com.sunzy.vulfocus.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-14
 */
@Service
@Transactional
public class NetWorkInfoServiceImpl extends ServiceImpl<NetWorkInfoMapper, NetWorkInfo> implements NetWorkInfoService {

    @Override
    public Result createNetWorkInfo(NetworkDTO networkDTO) {
        UserDTO user = UserHolder.getUser();
        if(!user.getSuperuser()){
            return Result.fail("权限不足");
        }
        String netWorkName = networkDTO.getNetWorkName();
        if(StrUtil.isBlank(netWorkName)){
            return Result.fail("网卡名称不能为空！");
        }

        String netWorkSubnet = networkDTO.getNetWorkSubnet();
        if(netWorkSubnet == null){
            return Result.fail("子网不能为空！");
        }
        String netWorkGateway = networkDTO.getNetWorkGateway();
        if(StrUtil.isBlank(netWorkGateway)){
            return Result.fail("网关不能为空！");
        }
        String netWorkScope = networkDTO.getNetWorkScope();
        if(StrUtil.isBlank(netWorkScope)){
            netWorkScope = "local";
        }
        String netWorkDriver = networkDTO.getNetWorkDriver();
        if(StrUtil.isBlank(netWorkDriver)){
            netWorkDriver = "bridge";
        }
        Boolean enableIpv6 = networkDTO.getEnableIpv6();
        if(enableIpv6 == null){
            enableIpv6 = false;
        }
        Integer count = query().eq("net_work_name", netWorkName).count();
        if(count > 0){
            return Result.fail("网卡名称不能重复！");
        }

        count = query().eq("net_work_subnet", netWorkSubnet).count();
        if(count > 0){
            return Result.fail("子网不能重复！");
        }
        count = query().eq("net_work_gateway", netWorkGateway).count();
        if(count > 0){
            return Result.fail("网关不能重复！");
        }
        String netWorkClientId = "";
        if("".equals(netWorkSubnet)){
            List<Network> networkList = DockerTools.getNetworkList();
            for (Network network : networkList) {
                List<Network.Ipam.Config> config = network.getIpam().getConfig();
                if(config.size() > 0){
                    String subnet = config.get(0).getSubnet();
                    netWorkGateway = config.get(0).getGateway();
                    if(!netWorkSubnet.equals(subnet)){
                        continue;
                    }
                    netWorkClientId = network.getId();
                    netWorkScope = network.getScope();
                    netWorkDriver = network.getDriver();
                    enableIpv6 = network.getEnableIPv6();
                    break;
                }
            }
        } else {
            // 创建网卡
            try {
                Network network = DockerTools.createNetwork(networkDTO);
                netWorkClientId = network.getId();
                netWorkGateway = network.getIpam().getConfig().get(0).getGateway();
            } catch (Exception e){
                e.printStackTrace();
                return Result.fail("服务器内部错误");
            }
        }
        NetWorkInfo netWorkInfo = new NetWorkInfo();
        netWorkInfo.setNetWorkId(GetIdUtils.getUUID());
        netWorkInfo.setNetWorkName(netWorkName);
        netWorkInfo.setNetWorkClientId(netWorkClientId);
        netWorkInfo.setNetWorkScope(netWorkScope);
        netWorkInfo.setNetWorkSubnet(netWorkSubnet);
        netWorkInfo.setNetWorkGateway(netWorkGateway);
        netWorkInfo.setNetWorkDriver(netWorkDriver);
        netWorkInfo.setCreateUser(user.getId());
        netWorkInfo.setEnableIpv6(enableIpv6);
        netWorkInfo.setCreateDate(LocalDateTime.now());
        netWorkInfo.setUpdateDate(LocalDateTime.now());
        save(netWorkInfo);
        return Result.ok(netWorkInfo);
    }
}
