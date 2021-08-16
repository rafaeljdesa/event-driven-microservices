package com.appsdeveloperblog.store.ProductsService.query;

import com.appsdeveloperblog.store.ProductsService.core.data.ProductEntity;
import com.appsdeveloperblog.store.ProductsService.core.data.ProductRepository;
import com.appsdeveloperblog.store.ProductsService.query.rest.ProductRestModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductsQueryHandler {

    private final ProductRepository productRepository;

    public ProductsQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductsQuery query) {
        List<ProductEntity> products = productRepository.findAll();
        if (CollectionUtils.isEmpty(products)) {
            return new ArrayList<>();
        }
        return products.stream().map(entity -> {
            ProductRestModel model = new ProductRestModel();
            BeanUtils.copyProperties(entity, model);
            return model;
        }).collect(Collectors.toList());
    }

}
