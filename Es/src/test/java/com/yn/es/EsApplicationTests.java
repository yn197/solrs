package com.yn.es;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class EsApplicationTests {

    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Test
    void contextLoads() {

    }

}
