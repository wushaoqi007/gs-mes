package com.greenstone.mes.job.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.base.api.RemoteMaterialStockService;
import com.greenstone.mes.base.api.RemoteOaService;
import com.greenstone.mes.base.api.RemoteWarehouseService;
import com.greenstone.mes.common.core.constant.ScheduleConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.job.domain.SysJob;
import com.greenstone.mes.job.mapper.ISysWarehouseJobMapper;
import com.greenstone.mes.job.service.ISysJobService;
import com.greenstone.mes.job.service.ISysWarehouseJobService;
import com.greenstone.mes.job.service.ISysWarehouseJobUserService;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.response.StockTimeOutListResp;
import com.greenstone.mes.oa.request.WxMsgSendCmd;
import com.greenstone.mes.system.domain.SysWarehouseJob;
import com.greenstone.mes.system.domain.SysWarehouseJobUser;
import com.greenstone.mes.system.dto.cmd.SysWarehouseJobAddReq;
import com.greenstone.mes.system.dto.cmd.SysWarehouseJobEditReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2022-11-01-8:43
 */
@Slf4j
@Service
public class SysWarehouseJobServiceImpl extends ServiceImpl<ISysWarehouseJobMapper, SysWarehouseJob> implements ISysWarehouseJobService {

    @Autowired
    private ISysWarehouseJobUserService warehouseJobUserService;

    @Autowired
    private ISysJobService sysJobService;

    @Autowired
    private RemoteMaterialStockService materialStockService;

    @Autowired
    private RemoteOaService oaService;

    @Autowired
    private RemoteWarehouseService warehouseService;


    @Override
    @Transactional
    public void insertJob(SysWarehouseJobAddReq job) {
        // 新增仓库任务
        SysWarehouseJob sysWarehouseJob = SysWarehouseJob.builder().jobName(job.getJobName()).warehouseId(job.getWarehouseId())
                .containsChildren(job.getContainsChildren()).timeout(job.getTimeout())
                .cron(job.getCron()).status(job.getStatus()).build();
        save(sysWarehouseJob);
        // 新增仓库任务提醒发送人员
        if (CollUtil.isNotEmpty(job.getSendList())) {
            for (SysWarehouseJobAddReq.SendUser sendUser : job.getSendList()) {
                warehouseJobUserService.save(SysWarehouseJobUser.builder().jobId(sysWarehouseJob.getId()).userId(sendUser.getUserId()).build());
            }
        }

        // 插入系统任务
        // invokeTarget:调用目标字符串
        SysJob sysJob = SysJob.builder().warehouseJobId(sysWarehouseJob.getId()).jobGroup("DEFAULT")
                .invokeTarget(StrUtil.format("sysWarehouseTask.remind('{}')", sysWarehouseJob.getId()))
                .jobName(job.getJobName()).cronExpression(job.getCron())
                .misfirePolicy(ScheduleConstants.MISFIRE_IGNORE_MISFIRES).concurrent("1")
                .status(job.getStatus()).build();
        try {
            sysJobService.insertJob(sysJob);
        } catch (Exception e) {
            log.error("insert system job error，{}", e.getMessage());
            throw new ServiceException(StrUtil.format("保存任务失败：{}", e.getMessage()));
        }

    }

    @Override
    @Transactional
    public void updateJob(SysWarehouseJobEditReq job) {
        SysWarehouseJob byId = getById(job.getId());
        if (Objects.isNull(byId)) {
            log.error("update system job error，not find job ,id:{}", job.getId());
            throw new ServiceException(StrUtil.format("更新仓库任务失败，任务未找到，id:{}", job.getId()));
        }
        // 更新仓库任务
        SysWarehouseJob sysWarehouseJob = SysWarehouseJob.builder().id(job.getId()).jobName(job.getJobName()).warehouseId(job.getWarehouseId())
                .containsChildren(job.getContainsChildren()).timeout(job.getTimeout())
                .cron(job.getCron()).status(job.getStatus()).build();
        updateById(sysWarehouseJob);
        // 更新仓库任务提醒发送人员
        if (CollUtil.isNotEmpty(job.getSendList())) {
            // 先删除原来人员
            warehouseJobUserService.remove(Wrappers.query(SysWarehouseJobUser.builder().jobId(job.getId()).build()));
            for (SysWarehouseJobEditReq.SendUser sendUser : job.getSendList()) {
                warehouseJobUserService.save(SysWarehouseJobUser.builder().jobId(sysWarehouseJob.getId()).userId(sendUser.getUserId()).build());
            }
        }

        // 更新系统任务
        // invokeTarget:调用目标字符串
        SysJob sysJob = SysJob.builder().warehouseJobId(sysWarehouseJob.getId()).jobGroup("DEFAULT")
                .invokeTarget(StrUtil.format("sysWarehouseTask.remind('{}')", sysWarehouseJob.getId()))
                .jobName(job.getJobName()).cronExpression(job.getCron())
                .misfirePolicy(ScheduleConstants.MISFIRE_IGNORE_MISFIRES).concurrent("1")
                .status(job.getStatus()).build();
        try {
            sysJobService.updateJobByWarehouseJobId(sysJob);
        } catch (Exception e) {
            log.error("update system job error，{}", e.getMessage());
            throw new ServiceException(StrUtil.format("更新任务失败：{}", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public void deleteJobById(Long id) {
        SysWarehouseJob byId = getById(id);
        if (Objects.isNull(byId)) {
            log.error("delete system job error，not find job ,id:{}", id);
            throw new ServiceException(StrUtil.format("删除仓库任务失败，任务未找到，id:{}", id));
        }
        // 删除系统任务
        try {
            sysJobService.deleteJobByWarehouseId(SysJob.builder().warehouseJobId(id).build());
        } catch (Exception e) {
            log.error("delete system job error，{}", e.getMessage());
            throw new ServiceException("删除系统任务错误");
        }
        // 删除仓库任务
        removeById(id);
        // 删除人员关联
        warehouseJobUserService.remove(Wrappers.query(SysWarehouseJobUser.builder().jobId(id).build()));

    }

    @Override
    @Transactional
    public void changeStatus(SysWarehouseJob job) {
        SysWarehouseJob warehouseJob = getById(job.getId());
        if (Objects.isNull(warehouseJob)) {
            log.error("delete system job error，not find job ,id:{}", job.getId());
            throw new ServiceException(StrUtil.format("删除仓库任务失败，任务未找到，id:{}", job.getId()));
        }
        warehouseJob.setStatus(job.getStatus());
        // 更新仓库任务状态
        updateById(warehouseJob);
        // 更新系统任务状态
        try {
            sysJobService.changeStatusByWarehouseId(SysJob.builder().warehouseJobId(job.getId()).status(job.getStatus()).build());
        } catch (Exception e) {
            log.error("update system job status error，{}", e.getMessage());
            throw new ServiceException("修改系统任务状态错误");
        }

    }

    @Override
    public List<SysWarehouseJob> selectJobList(SysWarehouseJob job) {
        QueryWrapper<SysWarehouseJob> queryWrapper = Wrappers.query(SysWarehouseJob.builder().build());
        if (StrUtil.isNotBlank(job.getJobName())) {
            queryWrapper.like("job_name", job.getJobName());
        }
        List<SysWarehouseJob> jobList = list(queryWrapper);
        if (CollUtil.isNotEmpty(jobList)) {

            for (SysWarehouseJob warehouseJob : jobList) {
                // 补充人员信息
                List<SysWarehouseJobUser> userList = warehouseJobUserService.list(Wrappers.query(SysWarehouseJobUser.builder().jobId(warehouseJob.getId()).build()));
                warehouseJob.setSendList(userList);
                // 补充仓库名称
                R<BaseWarehouse> resultR = warehouseService.getWarehouse(warehouseJob.getWarehouseId());
                if (resultR.isFail()) {
                    log.error("not find base warehouse ,warehouse id: {}", warehouseJob.getWarehouseId());
                    throw new ServiceException(StrUtil.format("未找到仓库，仓库id：{}", warehouseJob.getWarehouseId()));
                }
                warehouseJob.setWarehouseName(resultR.getData().getName());
            }
        }
        return jobList;
    }

    @Override
    public SysWarehouseJob getJobById(Long id) {
        SysWarehouseJob warehouseJob = getById(id);
        if (Objects.isNull(warehouseJob)) {
            log.error("warehouse job not find,id:{}", id);
            throw new ServiceException("仓库任务未找到，id:" + id);
        }
        // 补充人员信息
        List<SysWarehouseJobUser> userList = warehouseJobUserService.list(Wrappers.query(SysWarehouseJobUser.builder().jobId(warehouseJob.getId()).build()));
        warehouseJob.setSendList(userList);
        // 补充仓库名称
        R<BaseWarehouse> resultR = warehouseService.getWarehouse(warehouseJob.getWarehouseId());
        if (resultR.isFail()) {
            log.error("not find base warehouse ,warehouse id: {}", warehouseJob.getWarehouseId());
            throw new ServiceException(StrUtil.format("未找到仓库，仓库id：{}", warehouseJob.getWarehouseId()));
        }
        warehouseJob.setWarehouseName(resultR.getData().getName());
        return warehouseJob;
    }

    @Override
    public void timeOutRemind(String warehouseJobId) {
        QueryWrapper<SysWarehouseJob> queryWrapper = Wrappers.query(SysWarehouseJob.builder().id(Long.parseLong(warehouseJobId)).build());
        SysWarehouseJob sysWarehouseJob = getOneOnly(queryWrapper);
        if (Objects.isNull(sysWarehouseJob)) {
            log.error("warehouse job not find,id:{}", warehouseJobId);
            throw new ServiceException("仓库任务未找到，id:" + warehouseJobId);
        }
        // 检验仓库
        R<BaseWarehouse> warehouseResultR = warehouseService.getWarehouse(sysWarehouseJob.getWarehouseId());
        if (warehouseResultR.isFail()) {
            log.error("not find base warehouse ,warehouse id: {}", sysWarehouseJob.getWarehouseId());
            throw new ServiceException(StrUtil.format("未找到仓库，仓库id：{}", sysWarehouseJob.getWarehouseId()));
        }
        // 查询指定库大于指定超时时间的库存
        R<List<StockTimeOutListResp>> resultR = materialStockService.searchTimeout(sysWarehouseJob.getWarehouseId(), sysWarehouseJob.getTimeout(), sysWarehouseJob.getContainsChildren());
        if (resultR.isFail()) {
            log.error("Error search timeout stock: {}", resultR.getMsg());
            throw new ServiceException(StrUtil.format("查询仓库滞留库存失败：{}", resultR.getMsg()));
        }
        List<StockTimeOutListResp> stockTimeOutList = resultR.getData();
        // 总滞留数量大于零，通知相关人员
        int total = 0;
        if (CollUtil.isNotEmpty(stockTimeOutList)) {
            for (StockTimeOutListResp stockTimeOutListResp : stockTimeOutList) {
                total += stockTimeOutListResp.getNumber().intValue();
            }
        }
        log.info("system warehouse remind ,warehouse:{},timeout num:{}", warehouseResultR.getData().getName(), total);
        //通知人员
        if (total > 0) {
            // 查询相关人员
            QueryWrapper<SysWarehouseJobUser> userQueryWrapper = Wrappers.query(SysWarehouseJobUser.builder().jobId(sysWarehouseJob.getId()).build());
            List<SysWarehouseJobUser> userList = warehouseJobUserService.list(userQueryWrapper);
            if (CollUtil.isNotEmpty(userList)) {
                List<WxMsgSendCmd.WxMsgUser> toUser = new ArrayList<>();
                for (SysWarehouseJobUser sysWarehouseJobUser : userList) {
                    toUser.add(WxMsgSendCmd.WxMsgUser.builder().sysUserId(sysWarehouseJobUser.getUserId()).build());
                }
                String content = StrUtil.format("零件滞留提醒：{}存在{}件滞留件，请及时进行检验。", warehouseResultR.getData().getName(), total);
                log.info("prepare to send remind,target:{},content:{}", toUser, content);
                // 发送通知
                oaService.sendMsgToWx(WxMsgSendCmd.builder().toUser(toUser).content(content).build());
            }
        }

    }

}
