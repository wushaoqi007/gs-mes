package com.greenstone.mes.oa.application.service.impl;

import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.application.service.CustomShiftService;
import com.greenstone.mes.oa.domain.entity.CustomShift;
import com.greenstone.mes.oa.domain.repository.CustomShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author wsqwork
 * @date 2024/12/12 15:50
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomShiftServiceImpl implements CustomShiftService {

    private final CustomShiftRepository customShiftRepository;

    @Override
    public List<CustomShift> list(CustomShift customShift) {
        return customShiftRepository.list(customShift);
    }

    @Override
    public CustomShift detail(Long id) {
        return customShiftRepository.detail(id);
    }

    @Override
    public void add(CustomShift customShift) {
        validateShift(customShift);
        customShiftRepository.insert(customShift);
    }

    private void validateShift(CustomShift customShift) {
        try {
            String dayShift = customShift.getDayShift();
            String[] splitShift = dayShift.split("-");
            LocalTime signIn = LocalTime.parse(splitShift[0], DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime singOut = LocalTime.parse(splitShift[1], DateTimeFormatter.ofPattern("HH:mm"));
            log.info("早班时段：{}-{}", signIn, singOut);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException("请按正确的格式填写早班时段，例：07:30-16:30");
        }
        try {
            String nightShift = customShift.getNightShift();
            String[] splitShift = nightShift.split("-");
            LocalTime signIn = LocalTime.parse(splitShift[0], DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime singOut = LocalTime.parse(splitShift[1], DateTimeFormatter.ofPattern("HH:mm"));
            log.info("晚班班时段：{}-{}", signIn, singOut);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException("请按正确的格式填写晚班时段，例：20:00-05:00");
        }
        restTimeValidate(customShift.getDayRestTime(), "请按正确的格式填写早班休息时间，例：09:00-09:10,17:00-17:30");
        restTimeValidate(customShift.getNightRestTime(), "请按正确的格式填写晚班休息时间，例：09:00-09:10,17:00-17:30");
    }

    public void restTimeValidate(String restTime, String msg) {
        try {
            String[] split = restTime.split(",");
            for (String s : split) {
                String[] time = s.split("-");
                LocalTime start = LocalTime.parse(time[0], DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime end = LocalTime.parse(time[1], DateTimeFormatter.ofPattern("HH:mm"));
                log.info("休息时间转换：{}-{}", start, end);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException(msg);
        }
    }

    @Override
    public void update(CustomShift customShift) {
        validateShift(customShift);
        if (customShift.getId() == null) {
            throw new ServiceException("更新时，id不能为空");
        }
        customShiftRepository.update(customShift);
    }

    @Override
    public void delete(Long id) {
        customShiftRepository.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        customShiftRepository.deleteBatch(ids);
    }
}
