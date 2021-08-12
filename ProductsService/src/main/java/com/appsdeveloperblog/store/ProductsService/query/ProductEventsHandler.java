package com.appsdeveloperblog.store.ProductsService.query;

import com.appsdeveloperblog.store.ProductsService.core.data.ProductEntity;
import com.appsdeveloperblog.store.ProductsService.core.data.ProductRepository;
import com.appsdeveloperblog.store.ProductsService.core.events.ProductCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductEventsHandler {

    private final ProductRepository productRepository;

    public ProductEventsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent, productEntity);
        productRepository.save(productEntity);
    }

}
