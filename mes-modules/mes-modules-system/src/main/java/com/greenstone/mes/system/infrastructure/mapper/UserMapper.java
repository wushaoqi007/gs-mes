package com.greenstone.mes.system.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.infrastructure.po.UserPo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends EasyBaseMapper<UserPo> {

    @Results(id = "UserDeptResultMap", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "nickName", column = "nick_name"),
            @Result(property = "deptId", column = "dept_id"),
            @Result(property = "roleId", column = "role_id"),
            @Result(property = "employeeNo", column = "employee_no"),
            @Result(property = "wxUserId", column = "wx_user_id"),
            @Result(property = "wxCpId", column = "main_wxcp_id"),
            @Result(property = "email", column = "email"),
            @Result(property = "phonenumber", column = "phonenumber"),
            @Result(property = "avatar", column = "avatar"),
            @Result(property = "userType", column = "user_type"),
            @Result(property = "dept.deptId", column = "dept_id"),
            @Result(property = "dept.deptName", column = "dept_name"),
            @Result(property = "roleName", column = "role_name"),
    })
    @Select("""
select u.user_id, u.user_name, u.dept_id, u.role_id, u.nick_name, u.main_wxcp_id, u.wx_user_id, u.employee_no,
    u.email, u.avatar, u.phonenumber, u.user_type, d.dept_name, r.role_name 
    from sys_user u left join sys_dept d on u.dept_id = d.dept_id 
    left join sys_role_new r ON u.role_id = r.role_id 
    WHERE u.user_id = #{userId} and deleted = 0
""")
    User getById(Long userId);

    @Results(id = "UserResultMap", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "nickName", column = "nick_name"),
            @Result(property = "employeeNo", column = "employee_no"),
            @Result(property = "email", column = "email"),
            @Result(property = "wxUserId", column = "wx_user_id"),
            @Result(property = "wxCpId", column = "main_wxcp_id"),
    })
    @Select("""
            select u.user_id, u.user_name, u.nick_name, u.employee_no, u.email, u.wx_user_id, u.main_wxcp_id
            from sys_user u WHERE u.email = #{mail} and deleted = 0
            """)
    User getByMail(String mail);

    @ResultMap("UserResultMap")
    @Select("""
            select u.user_id, u.user_name, u.nick_name, u.employee_no, u.email, u.wx_user_id, u.main_wxcp_id
            from sys_user u WHERE deleted = 0 and (u.employee_no is null or u.employee_no = '') and u.user_type = 'user'
            """)
    List<User> getUserHaveNoEmployeeNo();

    @Select("""
select u.user_id, u.user_name, u.dept_id, u.role_id, u.nick_name, u.main_wxcp_id, u.wx_user_id, u.employee_no,
    u.email, u.avatar, u.phonenumber, u.user_type, d.dept_name, r.role_name 
    from sys_user u left join sys_dept d on u.dept_id = d.dept_id 
    left join sys_role_new r ON u.role_id = r.role_id 
    WHERE u.user_name = #{username} and deleted = 0
""")
    @ResultMap("UserDeptResultMap")
    User getByUsername(String username);

    @ResultMap("UserDeptResultMap")
    @Select("""
select u.user_id, u.user_name, u.dept_id, u.role_id, u.nick_name, u.main_wxcp_id, u.wx_user_id, u.employee_no,
    u.email, u.avatar, u.phonenumber, u.user_type, d.dept_name, r.role_name 
    from sys_user u left join sys_dept d on u.dept_id = d.dept_id 
    left join sys_role_new r ON u.role_id = r.role_id 
    WHERE u.main_wxcp_id = #{wxCpId} and u.wx_user_id = #{wxUserId} and deleted = 0
""")
    User getByWx(@Param("wxCpId") String wxCpId, @Param("wxUserId") String wxUserId);

    @ResultMap("UserDeptResultMap")
    @Select("""
<script>
select u.user_id, u.dept_id, u.role_id, u.nick_name, u.main_wxcp_id, u.wx_user_id, u.employee_no,
    u.email, u.avatar, u.phonenumber, u.user_type, d.dept_name, r.role_name ,u.create_time 
    from sys_user u left join sys_dept d on u.dept_id = d.dept_id 
    left join sys_role_new r ON u.role_id = r.role_id 
    where u.deleted = '0'
    <if test="userName != null and userName != ''">
    	AND u.user_name = #{userName}
    </if>
    <if test="nickName != null and nickName != ''">
    	AND u.nick_name like concat('%', #{nickName}, '%')
    </if>
    <if test="employeeNo != null and employeeNo != ''">
    	AND u.employee_no like concat('%', #{employeeNo}, '%')
    </if>
    <if test="email != null and email != ''">
    	AND u.email like concat('%', #{email}, '%')
    </if>
    <if test="phonenumber != null and phonenumber != ''">
    	AND u.phonenumber = #{phonenumber}
    </if>
    <if test="deptId != null">
    	AND u.dept_id = #{deptId}
    </if>
</script>
""")
    List<User> getUsers(User user);

    @ResultMap("UserDeptResultMap")
    @Select("""
select u.user_id, u.dept_id, u.nick_name, u.main_wxcp_id, u.wx_user_id, u.employee_no,
    u.email, u.avatar, u.phonenumber, u.user_type, d.dept_name 
    from sys_user u left join sys_dept d on u.dept_id = d.dept_id 
    WHERE u.main_wxcp_id = #{wxCpId} and deleted = 0
""")
    List<User> getByWxCp(String wxCpId);

}
