package org.jakab.jakarta.domain;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Order {
    private Long id;

    private Map<MenuItem,Integer> currentOrder = new HashMap<MenuItem,Integer>();
    private String status;

    private Double totalOrder;
    private String customer;

    public void addToOrder(MenuItem menuItem, int quantity) {
        int currentQuantity = 0;
        if (currentOrder.get(menuItem) != null) currentQuantity = currentOrder.get(menuItem);
        currentOrder.put(menuItem, currentQuantity + quantity);
    }
    public Map<MenuItem, Integer> getCurrentOrder() {
        return currentOrder;
    }


    public Double getOrderTotal() {
        double d = 0d;
        for (MenuItem menuItem : currentOrder.keySet()) {
            d += currentOrder.get(menuItem) * menuItem.getPrice();
        }
        return d;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MenuItem menuItem : currentOrder.keySet()) {
            sb.append(menuItem.getName() + " : " + currentOrder.get(menuItem) + "<br/>");
        }
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomer() {
        return customer;
    }

    public void setContents(Map<MenuItem, Integer> contents) {
        this.currentOrder=contents;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        return Objects.equals(id, other.id);
    }





}
