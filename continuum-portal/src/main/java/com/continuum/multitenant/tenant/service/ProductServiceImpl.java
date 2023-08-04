package com.continuum.multitenant.tenant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.tenant.repos.entity.Product;
import com.continuum.tenant.repos.repository.ProductRepository;

import java.util.List;

/**
 * @author RK
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }
}
