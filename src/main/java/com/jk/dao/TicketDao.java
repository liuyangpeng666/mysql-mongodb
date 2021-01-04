package com.jk.dao;

import com.jk.pojo.TicketBean;
import com.jk.pojo.TreeBean;
import com.jk.pojo.UserBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * @program: mysql-mongodb
 * @description:
 * @author: 刘洋朋
 * @create: 2021-01-03 20:20
 */
@MapperScan
public interface TicketDao {
    @Select("select * from t_trees where pid=#{pid}")
    List<TreeBean> findTreeByPid(int pid);

    int findTotal();

    List<TicketBean> findTicket(@Param("start") int start, @Param("rows") Integer rows);

    void updataPiao(@Param("num")Integer num,@Param("id")Integer id);

    void updateMoney(@Param("value") double value,@Param("username") String username);

    TicketBean findTicketById(Integer id);

    UserBean findUser(String username);

}
