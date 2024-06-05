package org.jakab.jakarta.Beans;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakab.jakarta.domain.MenuItem;
import org.jakab.jakarta.service.OrderService;

import java.util.List;
import java.util.Map;

@Named("order")
@RequestScoped
public class OrderBean {

    private List<MenuItem> items;


    @Inject
    private OrderService service;

    @PostConstruct
    public void init() {
        items = service.getMenuItems();


    }


    public List<MenuItem> getItems() {
        return items;
    }


}
