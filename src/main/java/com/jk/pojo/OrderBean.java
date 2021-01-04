package com.jk.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @program: mysql-mongodb
 * @description:
 * @author: 刘洋朋
 * @create: 2021-01-03 21:00
 */
@Data
@Document(collection = "y_orders")
public class OrderBean {
    @Id
    private String id;
    private String start_site;
    private String stop_site;
    private Integer seat;
    private String typename;
    private Integer ticket_num;
    private Double price;
    private Double ticket_sum;
    private String username;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField
    private Date time;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField
    private Date mindate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField
    private Date maxdate;
    private String yewu;
    private Integer order;

}
