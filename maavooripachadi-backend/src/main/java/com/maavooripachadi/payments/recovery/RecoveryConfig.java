package com.maavooripachadi.payments.recovery;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@EnableConfigurationProperties(RecoveryProperties.class)
public class RecoveryConfig { }