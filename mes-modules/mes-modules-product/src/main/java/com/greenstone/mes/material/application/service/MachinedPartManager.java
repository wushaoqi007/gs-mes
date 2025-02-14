package com.greenstone.mes.material.application.service;

import com.greenstone.mes.material.dto.MachinedPartImportDto;

import java.util.List;

public interface MachinedPartManager {

    void importData(List<MachinedPartImportDto> importList);

}
