package com.jk.pojo;

import lombok.Data;

/**
 * @program: mysql-mongodb
 * @description:
 * @author: 刘洋朋
 * @create: 2021-01-03 21:00
 */
@Data
public class UserBean {
    private Integer id;
    private String username;
    private Double money;
    private String pwd;
}
