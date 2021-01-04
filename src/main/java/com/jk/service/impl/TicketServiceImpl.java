package com.jk.service.impl;

import com.jk.dao.TicketDao;
import com.jk.pojo.OrderBean;
import com.jk.pojo.TicketBean;
import com.jk.pojo.TreeBean;
import com.jk.pojo.UserBean;
import com.jk.service.TicketService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @program: mysql-mongodb
 * @description:
 * @author: 刘洋朋
 * @create: 2021-01-03 20:20
 */
@Service
public class TicketServiceImpl implements TicketService {

    @Resource
    private TicketDao ticketDao;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<TreeBean> findTree() {
        int pid=0;
        List<TreeBean> list = findNodes(pid);
        return list;
    }
    private List<TreeBean> findNodes(int pid) {
        List<TreeBean> list = ticketDao.findTreeByPid(pid);
        for (TreeBean tree : list) {
            Integer id = tree.getId();
            List<TreeBean> nodelist = findNodes(id);//递归自己调自己
            //判断是否有子节点：有子节点-->打开  false  没有子节点-->不能打开 true
            if(nodelist!=null&&nodelist.size()>0){//有子节点
                tree.setNodes(nodelist);
                tree.setSelectable(false);//有子节点-->打开  false
            }else{//没有子节点
                tree.setSelectable(true);//没有子节点-->不能打开 true
            }
        }
        return list;
    }


    @Override
    public HashMap<String, Object> findTicket(Integer page, Integer rows) {
        int total=ticketDao.findTotal();
        int start=(page-1)*rows;
        List<TicketBean> list = ticketDao.findTicket(start,rows);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("rows", list);
        return map;
    }

    @Override
    public void goumai(Integer id, Integer num, String username) {
        TicketBean ticketBean =ticketDao.findTicketById(id);
        ticketDao.updataPiao(num,id);
        ticketDao.updateMoney(num*ticketBean.getPrice(),username);
        OrderBean orderBean = new OrderBean();
        orderBean.setPrice(ticketBean.getPrice());
        orderBean.setSeat(ticketBean.getSeat());
        orderBean.setStart_site(ticketBean.getStart_site());
        orderBean.setStop_site(ticketBean.getStop_site());
        orderBean.setTypename(ticketBean.getTypename());
        orderBean.setUsername(username);
        Date date = new Date();
        orderBean.setTime(date);
        orderBean.setTicket_num(num);
        orderBean.setTicket_sum(num*ticketBean.getPrice());
        mongoTemplate.save(orderBean);
    }

    @Override
    public UserBean findUser(String username) {
        return ticketDao.findUser(username);
    }

    @Override
    public HashMap<String, Object> myorder(Integer page, Integer rows, OrderBean orderBean) {
        Query query = new Query();
        Criteria where = Criteria.where("time");
        if (orderBean.getMindate()!=null){
            where.gte(orderBean.getMindate());
        }
        if (orderBean.getMaxdate()!=null){
            where.lte(orderBean.getMaxdate());
        }
        if (orderBean.getMindate()!=null || orderBean.getMaxdate()!=null){
            query.addCriteria(where);
        }
        long count = mongoTemplate.count(query,OrderBean.class);
        if (orderBean.getOrder()!=null && orderBean.getOrder()==1){
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC,"order_time"),new Sort.Order(Sort.Direction.DESC,"ticket_sum"));
            query.with(sort);
        }
        if (orderBean.getOrder()!=null && orderBean.getOrder()==2){
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC,"order_time"),new Sort.Order(Sort.Direction.ASC,"ticket_sum"));
            query.with(sort);
        }
        int start =(page-1)*rows;
        query.skip(start).limit(rows);
        List<OrderBean> list= mongoTemplate.find(query,OrderBean.class);
        HashMap<String, Object> map=new HashMap<>();
        map.put("total", count);
        map.put("rows", list);
        return map;
    }

    @Override
    public void daochu(HttpServletResponse response) {
        List<OrderBean> list = mongoTemplate.findAll(OrderBean.class);

        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet("订单信息");

        String[] title = {"id","出发站","到达站","席别","车次类型名称","付款","购买票数","下单时间"};
        XSSFRow row = sheet.createRow(0);

        for (int i = 0; i < title.length; i++) {
            row.createCell(i).setCellValue(title[i]);
        }

        int sumprice = 0;
        int num=0;
        for (int i = 0; i < list.size(); i++) {

            OrderBean order = list.get(i);

            XSSFRow row1 = sheet.createRow(i+1);

            row1.createCell(0).setCellValue(order.getId());
            row1.createCell(1).setCellValue(order.getStart_site());
            row1.createCell(2).setCellValue(order.getStop_site());
            if (order.getSeat()==1){
                order.setYewu("商务座");
            }else if (order.getSeat()==2){
                order.setYewu("一等座");
            }else if (order.getSeat()==3){
                order.setYewu("二等座");
            }else if (order.getSeat()==4){
                order.setYewu("硬卧");
            }else if (order.getSeat()==5){
                order.setYewu("硬座");
            }
            row1.createCell(3).setCellValue(order.getYewu());
            row1.createCell(4).setCellValue(order.getTypename());
            row1.createCell(5).setCellValue(order.getTicket_sum());
            row1.createCell(6).setCellValue(order.getTicket_num());
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            row1.createCell(7).setCellValue( simpleDateFormat.format(order.getTime()));

            num+=order.getTicket_num();
            sumprice+=order.getTicket_sum();
        }


        XSSFRow row2 = sheet.createRow(list.size()+1);
        row2.createCell(0).setCellValue("销量");
        row2.createCell(7).setCellValue(num);
        XSSFRow row3 = sheet.createRow(list.size()+2);
        row3.createCell(0).setCellValue("总价");
        row3.createCell(7).setCellValue(sumprice);

        try {
            response.setHeader("Content-Disposition", "attchment;filename="+ URLEncoder.encode("订单表格.xlsx", "utf-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
