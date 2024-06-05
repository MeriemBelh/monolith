package org.jakab.jakarta.service;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import org.jakab.jakarta.Beans.OrderBean;
import org.jakab.jakarta.data.MenuDao;
import org.jakab.jakarta.data.MenuDaoFactory;
import org.jakab.jakarta.domain.MenuItem;
import org.jakab.jakarta.domain.Order;

import java.util.List;
import java.util.Map;

public class OrderService {

    @Inject
    private MenuDaoFactory menuDaoFactory;

    public OrderService(){}

    public List<MenuItem> getMenuItems(){
        MenuDao menuDao = MenuDaoFactory.getMenuDao();

        List<MenuItem> menuItems = menuDao.getFullMenu();

        return menuItems;
    }

    public String getStatus(Long orderId){
        MenuDao menuDao = MenuDaoFactory.getMenuDao();
        Order order = menuDao.getOrder(orderId);
        return order.getStatus();
    }

    public Double getOrderTotal(Long orderId){
        MenuDao menuDao = MenuDaoFactory.getMenuDao();
        Order order = menuDao.getOrder(orderId);
        return order.getOrderTotal();
    }

    public List<MenuItem> searchMenuByItem(String searchTerm){
        MenuDao menuDao = MenuDaoFactory.getMenuDao();
        List<MenuItem> menuItems = menuDao.find(searchTerm);
        return  menuItems;
    }

    public List<Order> getAllOrders(){
        MenuDao menuDao = MenuDaoFactory.getMenuDao();
        List<Order> orders = menuDao.getAllOrders();
        return orders;
    }

    public void updateOrderStatus(Long orderid, String status ){
        MenuDao menuDao = MenuDaoFactory.getMenuDao();
        List<Order> orders = menuDao.getAllOrders();
        Order order = null;
        for (Order o: orders){
            if (o.getId() == orderid){
                order = o;
            }
        }
        if (order != null) {
            menuDao.updateOrderStatus(orderid, status);
        }
    }

    public Order placeOrder(Map<Integer,String> items){
        MenuDao menuDao = MenuDaoFactory.getMenuDao();
        int maxId = menuDao.getFullMenu().size();
        Order order = menuDao.newOrder("nora");
        for (int i = 0; i <maxId+1; i++) {
            String quantity = items.get(i);
            try
            {
                int q = Integer.parseInt(quantity);
                if (q > 0) {
                    menuDao.addToOrder(order.getId(), menuDao.getItem(i), q);
                    order.addToOrder(menuDao.getItem(i), q);
                }
            }
            catch(NumberFormatException nfe)
            {
                //that's fine it just means there wasn't an order for this item
            }

        }

        return order;

    }
}