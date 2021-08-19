package com.appsdeveloperblog.store.ProductsService;

import com.appsdeveloperblog.store.ProductsService.command.interceptors.CreateProductCommandInterceptor;
import com.appsdeveloperblog.store.ProductsService.core.errorhandling.ProductsServiceEventsErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

@EnableEurekaClient
@SpringBootApplication
public class ProductsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductsServiceApplication.class, args);
	}

	@Autowired
	public void registerCreateProductCommandInterceptor(ApplicationContext context, CommandBus commandBus) {
		commandBus.registerDispatchInterceptor(
			context.getBean(CreateProductCommandInterceptor.class)
		);
	}

	@Autowired
	public void configure(EventProcessingConfigurer config) {
		config.registerListenerInvocationErrorHandler("product-group",
			configuration -> new ProductsServiceEventsErrorHandler());

		/*config.registerListenerInvocationErrorHandler("product-group",
				configuration -> PropagatingErrorHandler.instance());*/
	}

}
