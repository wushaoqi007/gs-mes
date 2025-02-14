package com.greenstone.mes.system.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.system.infrastructure.po.PermPo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermMapper extends EasyBaseMapper<PermPo> {

    @Select("""
( SELECT
	p.perm_code
	FROM
		sys_user_role ur
		LEFT JOIN sys_role_perm rp ON ur.role_id = rp.role_id
		RIGHT JOIN sys_perm p ON rp.perm_id = p.perm_id
	WHERE
		ur.user_id = #{userId}
		AND p.perm_type = 'P'
	) UNION
	(
	SELECT
		p.perm_code
	FROM
		sys_role_perm rp
		LEFT JOIN sys_role r ON rp.role_id = r.role_id
		LEFT JOIN sys_perm p ON rp.perm_id = p.perm_id
	WHERE
	r.universal_role = 1
	AND p.perm_type = 'P')
""")
    List<String> selectRolePermsByUserId(@Param("userId") Long userId);

}
