package com.nhnacademy.gateway2.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = RestTemplateConfig.class)
class RestTemplateConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testRestTemplateBeanExists() {
        // Check that the RestTemplate bean is present in the application context
        assertThat(applicationContext.getBean(RestTemplate.class)).isNotNull();
    }

    @Test
    void testRestTemplateBean() {
        // Get the RestTemplate bean from the application context
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

        // Check that the RestTemplate bean is correctly configured
        assertThat(restTemplate).isInstanceOf(RestTemplate.class);
    }
}
