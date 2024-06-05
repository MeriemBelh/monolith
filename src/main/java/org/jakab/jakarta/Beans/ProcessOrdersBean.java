package org.jakab.jakarta.Beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakab.jakarta.domain.Order;
import org.jakab.jakarta.service.OrderService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("processOrders")
@RequestScoped
public class ProcessOrdersBean {

    private List<Order> orders;

    private List<String> statuses;

    public Map<Long, String> getSelectedOrders() {
        return selectedOrders;
    }

    public void setSelectedOrders(Map<Long, String> selectedOrders) {
        this.selectedOrders = selectedOrders;
    }

    private Map<Long, String> selectedOrders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;

    private String status;

    @Inject
    private OrderService service;

    @PostConstruct
    public void init(){

        orders = service.getAllOrders();
        statuses = new ArrayList<>();
        statuses.add("order accepted");
        statuses.add("payment received");
        statuses.add("being prepared");
        statuses.add("ready for collection");
        selectedOrders = new HashMap<>();



    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void updateOrderStatus(){
        if (selectedOrders != null && !selectedOrders.isEmpty()) {
            for (Map.Entry<Long, String> entry : selectedOrders.entrySet()) {
                Long orderId = entry.getKey();
                String status = entry.getValue();
                service.updateOrderStatus(orderId, status);
            }
        }



    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getStatus() {
        return status;
    }
}