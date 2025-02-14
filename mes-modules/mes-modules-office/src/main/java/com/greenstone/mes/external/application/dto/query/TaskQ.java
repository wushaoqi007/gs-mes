package com.greenstone.mes.external.application.dto.query;

import lombok.Data;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/3/3 14:31
 */
@Data
public class TaskQ {

    private List<Integer> taskState;

    private Long currUser;

}
