package com.yn.es.document;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
@Slf4j
@Service
public class BulkTests {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 批量操作
     *
     * @throws IOException
     */
    @Test
    public void bulkAdd() throws IOException{
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest("posts").id("7")
                .source(XContentType.JSON,"field", "foo"));
        request.add(new IndexRequest("posts").id("8")
                .source(XContentType.JSON,"field", "bar"));
        request.add(new IndexRequest("posts").id("9")
                .source(XContentType.JSON,"field", "baz"));
        //将策略刷新为 WriteRequest.RefreshPolicy 实例
        request.timeout(TimeValue.timeValueMinutes(2));
        restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
    }

}
