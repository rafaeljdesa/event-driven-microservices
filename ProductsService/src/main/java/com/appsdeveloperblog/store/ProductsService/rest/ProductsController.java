package com.appsdeveloperblog.store.ProductsService.rest;

import com.appsdeveloperblog.store.ProductsService.command.CreateProductCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final Environment env;
    private final CommandGateway commandGateway;

    public ProductsController(Environment env, CommandGateway commandGateway) {
        this.env = env;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@RequestBody CreateProductRestModel createProductRestModel) {

        CreateProductCommand createProductCommand = CreateProductCommand.builder()
            .price(createProductRestModel.getPrice())
            .quantity(createProductRestModel.getQuantity())
            .title(createProductRestModel.getTitle())
            .productId(UUID.randomUUID().toString())
            .build();

        String returnValue;

        try {
            returnValue = commandGateway.sendAndWait(createProductCommand);
        } catch (Exception e) {
            returnValue = e.getLocalizedMessage();
        }

        return returnValue;
    }

    @GetMapping
    public String getProduct() {
        return "Get product";
    }

    @PutMapping
    public String updateProduct() {
        return "";
    }

    @DeleteMapping
    public String deleteProduct() {
        return "";
    }

}
