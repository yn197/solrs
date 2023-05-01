package com.yn.es.document;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
@Slf4j
@Service
public class ReindexTests {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 索引同步
     *
     * @throws IOException
     */
    @Test
    public void reindex() throws IOException {
        /**
         POST _reindex
         {
         "source": {
         "index": "pigg_test"
         },
         "dest": {
         "index": "pigg_test2"
         }
         }
         */
        ReindexRequest request = new ReindexRequest();
        //源index
        request.setSourceIndices("posts");
        //目标index
        request.setDestIndex("posts2");
        //只创建文档缺少字段
        request.setDestOpType("index");
        //跳过版本冲突
        request.setConflicts("proceed");
        //通过添加查询来限制文档,需要添加keyword，否则会不成功
        request.setSourceQuery(new TermQueryBuilder("user.keyword", "Lucy"));
        //限制处理文档的数量。
        request.setMaxDocs(10);
        //设置批次大小
        request.setSourceBatchSize(100);
        restHighLevelClient.reindex(request,RequestOptions.DEFAULT);
    }

}
