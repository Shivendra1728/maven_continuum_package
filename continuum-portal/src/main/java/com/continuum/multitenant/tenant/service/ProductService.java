package com.continuum.multitenant.tenant.service;

import java.util.List;

import com.continuum.tenant.repos.entity.Product;

/**
 * @author RK
 */
public interface ProductService {

    List<Product> getAllProduct();
}
