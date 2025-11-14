package com.maavooripachadi.order;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OrderNotificationProperties.class)
public class OrderConfig {
}
