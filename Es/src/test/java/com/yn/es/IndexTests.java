package com.yn.es;

import com.alibaba.fastjson.JSONObject;
import com.yn.es.service.IndexService;
import com.yn.es.utils.EsUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Slf4j
@Service
public class IndexTests implements IndexService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private EsUtils esUtils;

    /**
     * 创建索引添加数据
     *
     * @throws IOException
     */
    @Test
    public void esUtilsAdd() throws IOException {
        String jsonString = "{" +
                "\"user\":\"bob\"," +
                "\"postDate\":\"2014-01-30\"," +
                "\"message\":\"trying out redis\"" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //不加id，则随机生成一个id
        esUtils.addData(jsonObject, "posts");
    }

    /**
     * 创建索引添加数据
     *
     * @throws IOException
     */
    @Test
    public void indexStringJsonObject() throws IOException {
        IndexRequest request = new IndexRequest("posts");
        request.id("1");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString, XContentType.JSON);
        restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    @Test
    public void indexMapJsonObject() throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest indexRequest = new IndexRequest("posts")
                .id("2").source(jsonMap);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Test
    public void indexBuilderJsonObject() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "kimchy");
            builder.timeField("postDate", new Date());
            builder.field("message", "trying out Elasticsearch");
        }
        builder.endObject();
        IndexRequest indexRequest = new IndexRequest("posts")
                .id("3").source(builder);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Test
    public void indexSourceObject() throws IOException {
        IndexRequest indexRequest = new IndexRequest("posts")
                .id("4")
                .source("user", "kimchy",
                        "postDate", new Date(),
                        "message", "trying out Elasticsearch");
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    /**
     * 异步创建索引
     * 为什么成功回调了，但是还是走的失败   处理方式如下，加了sleep
     * @throws IOException
     */
    @Test
    public void indexAsyncSourceObject() throws IOException, InterruptedException {
        IndexRequest indexRequest = new IndexRequest("posts")
                .id("5")
                .source("user", "Lucy",
                        "postDate", new Date(),
                        "message", "trying out java");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        restHighLevelClient.indexAsync(indexRequest, RequestOptions.DEFAULT,this);
        Thread.sleep(1000);
    }


    @Override
    public void onResponse(IndexResponse indexResponse) {
        log.info("onResponse我创建成功了");
    }

    @Override
    public void onFailure(Exception e) {
        log.info("onFailure我创建失败了");
    }
}
