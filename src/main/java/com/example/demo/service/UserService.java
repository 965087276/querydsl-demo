package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserDeptDTO;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface UserService {
    /**
     * 根据用户名和密码查找（假定只能找出一条）
     * @param username
     * @param password
     * @return
     */
    User findByUsernameAndPassword(String username, String password);

    /**
     * 部分字段映射查询 避免返回List<Object[]>
     * 投影为UserRes,lambda方式(灵活，类型可以在lambda中修改)
     * @return
     */
     List<UserDTO> findAllUserDTO(Pageable pageable);

    /**
     * 所有条件动态分页查询
     *
     * @param username
     * @param password
     * @param nickName
     * @param birthday
     * @param uIndex
     * @return
     */
    Page<User> findByUserProperties(Pageable pageable, String username, String password, String nickName, Date birthday, BigDecimal uIndex);

    /**
     * 动态条件排序、分组查询
     * @param username
     * @param password
     * @param nickName
     * @param birthday
     * @param uIndex
     * @return
     */
    List<User> findByUserPropertiesGroupByUIndex(String username, String password, String nickName, Date birthday, BigDecimal uIndex);

    /**
     * 查询单个字段（避免返回List<Object>
     */
    List<String> listUsernames();

    /**
     * 根据部门的id查询用户的基本信息+用户所属部门信息，并且使用UserDeptDTO进行封装返回给前端展示
     * @param departmentId
     * @return
     */
    List<UserDeptDTO> findByDepatmentIdDTO(int departmentId);


}
