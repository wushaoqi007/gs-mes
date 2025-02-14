package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.oa.domain.entity.CustomShift;

import java.util.List;

public interface CustomShiftService {
    List<CustomShift> list(CustomShift customShift);

    CustomShift detail(Long id);

    void add(CustomShift customShift);

    void update(CustomShift customShift);

    void delete(Long id);

    void deleteBatch(Long[] ids);
}
