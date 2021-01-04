package com.jk.service;

import com.jk.pojo.OrderBean;
import com.jk.pojo.TreeBean;
import com.jk.pojo.UserBean;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * @program: mysql-mongodb
 * @description:
 * @author: 刘洋朋
 * @create: 2021-01-03 20:20
 */
public interface TicketService {
    List<TreeBean> findTree();

    HashMap<String, Object> findTicket(Integer page, Integer rows);

    void goumai(Integer id, Integer num, String username);

    UserBean findUser(String username);

    HashMap<String, Object> myorder(Integer page, Integer rows, OrderBean orderBean);

    void daochu(HttpServletResponse response);
}
