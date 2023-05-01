package com.yn.es;

import com.yn.es.service.ExistsService;
import com.yn.es.service.GetService;
import com.yn.es.utils.EsUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@SpringBootTest
@Slf4j
@Service
public class ExistsTests implements ExistsService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private EsUtils esUtils;

    /**
     * 存在查询
     *
     * @throws IOException
     */
    @Test
    public void existById() throws IOException, InterruptedException {
        GetRequest getRequest = new GetRequest(
                "posts",
                "1");
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        log.info("是否存在:{}", exists);
        Cancellable existsAsync = restHighLevelClient.existsAsync(getRequest, RequestOptions.DEFAULT, this);
        Thread.sleep(1000);
        log.info("是否存在:{}", existsAsync);
        boolean existsById = esUtils.existsById("posts", "1");
        log.info("是否存在existsById:{}", existsById);
    }

    @Override
    public void onResponse(Boolean aBoolean) {
        log.info("true");
    }

    @Override
    public void onFailure(Exception e) {
        log.info("false");
    }
}
