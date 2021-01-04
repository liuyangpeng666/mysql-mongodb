package com.jk.pojo;

import lombok.Data;

/**
 * @program: mysql-mongodb
 * @description:
 * @author: 刘洋朋
 * @create: 2021-01-03 20:49
 */
@Data
public class TicketBean {
    private Integer id;
    private String start_site;
    private String stop_site;
    private Integer seat;
    private String start_date;
    private Integer type_id;
    private Integer ticket_count;
    private Double price;

    private String typename;
}
