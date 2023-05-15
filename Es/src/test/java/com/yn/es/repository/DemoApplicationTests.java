package com.yn.es.esPostsRepository;

import com.yn.es.domain.Posts;
import com.yn.es.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class DemoApplicationTests {


    @Autowired
    ApiService apiService;

    @Test
    public void contextLoads() {
        Posts posts = apiService.findOnePosts(302191900788527104L);
        log.info("Posts查询:{}", posts);
    }
}
