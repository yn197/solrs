package com.yn.es.document;

import com.yn.es.service.GetService;
import com.yn.es.utils.EsUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
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
public class GetTests implements GetService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private EsUtils esUtils;

    /**
     * 根据id查询
     *
     * @throws IOException
     */
    @Test
    public void getById() throws IOException {
        GetRequest getRequest = new GetRequest("posts", "1");
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        log.info("查询数据:{}", response.getSourceAsMap());
    }

    /**
     * 根据id查询查询某些特定字段
     *
     * @throws IOException
     */
    @Test
    public void fetchSourceContext() throws IOException {
        GetRequest getRequest = new GetRequest("posts", "1");
        //指定字段
//        String[] includes = new String[]{"message", "*Date"};
//        String[] excludes = Strings.EMPTY_ARRAY;
//        FetchSourceContext fetchSourceContext =
//                new FetchSourceContext(true, includes, excludes);
        //排除message的其他字段
        String[] includes = Strings.EMPTY_ARRAY;
        String[] excludes = new String[]{"message"};
        FetchSourceContext fetchSourceContext =
                new FetchSourceContext(true, includes, excludes);
        getRequest.fetchSourceContext(fetchSourceContext);
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        log.info("查询数据:{}", response.getSourceAsMap());
    }

    /**
     * 根据id查询异步查询某些特定字段
     *
     * @throws IOException
     */
    @Test
    public void storedFields() throws IOException, InterruptedException {
        GetRequest getRequest = new GetRequest("posts", "1");
        restHighLevelClient.getAsync(getRequest, RequestOptions.DEFAULT, this);
        Thread.sleep(1000);
    }
    /**
     * 根据id查询
     *
     * @throws IOException
     */
    @Test
    public void getEsUtilsById() throws IOException {
        Map<String, Object> objectMap = esUtils.searchDataById("posts", "2", null);
        log.info("查询数据:{}", objectMap);
    }


    @Override
    public void onResponse(GetResponse getResponse) {
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        log.info("onResponse查询到数据:{}",sourceAsMap);
    }

    @Override
    public void onFailure(Exception e) {
        log.info("onFailure我创建失败了");
    }
}
