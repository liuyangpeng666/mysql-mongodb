package com.jk.controller;

import com.jk.pojo.OrderBean;
import com.jk.pojo.TreeBean;
import com.jk.pojo.UserBean;
import com.jk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

/**
 * @program: mysql-mongodb
 * @descri=ption:
 * @author: 刘洋朋
 * @create: 2021-01-03 20:20
 */
@Controller
@RequestMapping("ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @RequestMapping("toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("show")
    public String show(){
        return "show";
    }

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("order")
    public String order(){
        return "order";
    }

    @RequestMapping("login")
    @ResponseBody
    private String login(UserBean userBean, HttpSession session){
        UserBean user= ticketService.findUser(userBean.getUsername());
        if (user == null) {
            return "用户名不存在";
        }
        if (!userBean.getPwd().equals(user.getPwd())) {
            return "密码不正确";
        }
        session.setAttribute("user", user);
        return "登录成功！！";
    }

    @RequestMapping("findTree")
    @ResponseBody
    public List<TreeBean> findTree(){
        return ticketService.findTree();
    }

    @RequestMapping("findTicket")
    @ResponseBody
    public HashMap<String,Object> findTicket(Integer page, Integer rows) {
        return ticketService.findTicket(page,rows);
    }
    @RequestMapping("goumai")
    @ResponseBody
    public String goumai(Double sum, Integer id, Integer num, HttpSession session) {
        UserBean userBean = (UserBean) session.getAttribute("user");
        if (userBean.getMoney()<sum){
            return "余额不足,请充值！！！";
        }else {
            userBean.setMoney(userBean.getMoney()-sum);
            session.removeAttribute("user");
            session.setAttribute("user",userBean);
            ticketService.goumai(id,num,userBean.getUsername());
            return "购买成功！！";
        }
    }

    @RequestMapping("myorder")
    @ResponseBody
    public HashMap<String,Object> myorder(Integer page, Integer rows, OrderBean orderBean) {
        return ticketService.myorder(page,rows,orderBean);
    }

    @RequestMapping("daochu")
    @ResponseBody
    public void daochu(HttpServletResponse response){
        ticketService.daochu(response);
    }

}
