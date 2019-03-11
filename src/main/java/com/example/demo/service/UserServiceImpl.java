package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserDeptDTO;
import com.example.demo.entity.QDepartment;
import com.example.demo.entity.QUser;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        QUser user = QUser.user;
        return jpaQueryFactory
                .selectFrom(user)
                .where(
                        user.username.eq(username),
                        user.password.eq(password)
                )
                .fetchOne();
    }

    @Override
    public List<UserDTO> findAllUserDTO(Pageable pageable) {
        QUser user = QUser.user;
        return jpaQueryFactory
                .select(
                        user.username,
                        user.userId,
                        user.nickName,
                        user.birthday
                )
                .from(user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> UserDTO.builder()
                        .username(tuple.get(user.username))
                        .nickname(tuple.get(user.nickName))
                        .userId(tuple.get(user.userId).toString())
                        .birthday(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tuple.get(user.birthday)))
                        .build()
                )
                .collect(Collectors.toList());

    }

    @Override
    public Page<User> findByUserProperties(Pageable pageable, String username, String password, String nickName, Date birthday, BigDecimal uIndex) {
        QUser user = QUser.user;
        //初始化组装条件(类似where 1=1)
        Predicate predicate = user.isNotNull().or(user.isNull());

        //执行动态条件拼装
        predicate = username == null ? predicate : ExpressionUtils.and(predicate,user.username.eq(username));
        predicate = password == null ? predicate : ExpressionUtils.and(predicate,user.password.eq(password));
        predicate = nickName == null ? predicate : ExpressionUtils.and(predicate,user.nickName.eq(username));
        predicate = birthday == null ? predicate : ExpressionUtils.and(predicate,user.birthday.eq(birthday));
        predicate = uIndex == null ? predicate : ExpressionUtils.and(predicate,user.uIndex.eq(uIndex));

        Page<User> page = userRepository.findAll(predicate, pageable);
        return page;
    }

    @Override
    public List<User> findByUserPropertiesGroupByUIndex(String username, String password, String nickName, Date birthday, BigDecimal uIndex) {
        QUser user = QUser.user;
        //初始化组装条件(类似where 1=1)
        Predicate predicate = user.isNotNull().or(user.isNull());
        //执行动态条件拼装
        predicate = username == null ? predicate : ExpressionUtils.and(predicate, user.username.eq(username));
        predicate = password == null ? predicate : ExpressionUtils.and(predicate, user.password.eq(password));
        predicate = nickName == null ? predicate : ExpressionUtils.and(predicate, user.nickName.eq(username));
        predicate = birthday == null ? predicate : ExpressionUtils.and(predicate, user.birthday.eq(birthday));
        predicate = uIndex == null ? predicate : ExpressionUtils.and(predicate, user.uIndex.eq(uIndex));
        //执行拼装好的条件并根据userId排序，根据uIndex分组
        List<User> list = jpaQueryFactory
                .selectFrom(user)
                .where(predicate)               //执行条件
                .orderBy(user.userId.asc())     //执行排序
                .groupBy(user.uIndex)           //执行分组
                .having(user.uIndex.longValue().max().gt(7))//uIndex最大值小于7
                .fetch();

        //封装成Page返回
        return list;
    }

    @Override
    public List<String> listUsernames() {
        QUser user = QUser.user;

        return jpaQueryFactory
                .select(user.username)
                .from(user)
                .fetch();
    }

    @Override
    public List<UserDeptDTO> findByDepatmentIdDTO(int departmentId) {
        QUser user = QUser.user;
        QDepartment department = QDepartment.department;
        //直接返回
        return jpaQueryFactory
                //投影只去部分字段
                .select(
                        user.username,
                        user.nickName,
                        user.birthday,
                        department.deptName,
                        department.createDate

                )
                .from(user, department)
                //联合查询
                .where(
                        user.departmentId.eq(department.deptId).and(department.deptId.eq(departmentId))
                )
                .fetch()
                //lambda开始
                .stream()
                .map(tuple ->
                        //需要做类型转换，所以使用map函数非常适合
                        UserDeptDTO.builder()
                                .username(tuple.get(user.username))
                                .nickname(tuple.get(user.nickName))
                                .birthday(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tuple.get(user.birthday)))
                                .deptName(tuple.get(department.deptName))
                                .deptBirth(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tuple.get(department.createDate)))
                                .build()
                )
                .collect(Collectors.toList());
    }
}
