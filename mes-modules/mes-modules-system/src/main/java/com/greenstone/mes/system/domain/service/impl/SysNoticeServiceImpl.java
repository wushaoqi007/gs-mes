package com.greenstone.mes.system.domain.service.impl;

import com.greenstone.mes.system.domain.service.ISysNoticeService;
import com.greenstone.mes.system.domain.service.MessageService;
import com.greenstone.mes.system.domain.SysNotice;
import com.greenstone.mes.system.domain.service.SysUserService;
import com.greenstone.mes.system.dto.cmd.MessageSaveCmd;
import com.greenstone.mes.system.enums.MsgCategory;
import com.greenstone.mes.system.infrastructure.mapper.SysNoticeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公告 服务层实现
 *
 * @author ruoyi
 */
@AllArgsConstructor
@Service
public class SysNoticeServiceImpl implements ISysNoticeService {

    private final SysNoticeMapper noticeMapper;
    private final SysUserService userService;
    private final MessageService messageService;

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Transactional
    @Override
    public int insertNotice(SysNotice notice) {
        int insertResult = noticeMapper.insert(notice);
        List<Long> userIds = userService.getUserIds();
        MessageSaveCmd messageSaveCmd = MessageSaveCmd.builder().title("系统通知")
                .subTitle(notice.getNoticeTitle())
                .content(notice.getNoticeContent())
                .category(MsgCategory.ADMIN_NOTICE)
                .recipientIds(userIds)
                .sourceId(String.valueOf(notice.getNoticeId())).build();
        messageService.save(messageSaveCmd);
        return insertResult;
    }

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice) {
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId) {
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }
}
