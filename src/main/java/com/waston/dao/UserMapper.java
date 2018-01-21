package com.waston.dao;

import com.waston.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 持久层用户接口
 */
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 检查用户名是否存在
     *
     * @param username
     * @return
     */
    int checkUsername(String username);

    /**
     * 根据用户名和密码查询用户
     *
     * @param username
     * @param password
     * @return
     */
    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 检查邮箱是否存在
     *
     * @param email
     * @return
     */
    int checkEmail(String email);

    /**
     * 根据用户名获取问题信息，用来重置密码
     *
     * @param username
     * @return
     */
    String selectQuestionByUsername(String username);

    /**
     * 检查答案是否正确
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    /**
     * 修改密码
     *
     * @param username
     * @param passwordNew
     * @return
     */
    int updatePassword(@Param("username") String username, @Param("passwordNew") String passwordNew);

    /**
     * 校验旧密码
     *
     * @param passwordOld
     * @param username
     * @return
     */
    int checkPassword(@Param("username") String username, @Param("passwordOld") String passwordOld);

    /**
     * 检查邮箱是否存在(把id也就是当前用户除开)
     *
     * @param id
     * @param email
     * @return
     */
    int checkEmailById(@Param("id") Integer id, @Param("email") String email);

    /**
     * 分页查询
     * @return
     */
    List<User> selectByPage();
}