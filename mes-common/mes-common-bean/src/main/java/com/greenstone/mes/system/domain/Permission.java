package com.greenstone.mes.system.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<String> rights;

    private List<Condition> viewFilter;

    private List<Condition> updateFilter;
}
