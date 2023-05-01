package com.yn.es.document;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
@Slf4j
@Service
public class DeleteByQueryTests {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 条件删除
     *
     * @throws IOException
     */
    @Test
    public void updateByQuery() throws IOException {
        DeleteByQueryRequest request =
                new DeleteByQueryRequest("posts2");
        //防止版本冲突
        request.setConflicts("proceed");
        //条件查询限制
        request.setQuery(new TermQueryBuilder("user.keyword", "bob1"));
        //来限制处理文档的数量
        request.setMaxDocs(10);
        //使用 setBatchSize 更改批次大小
        request.setMaxDocs(100);
        //设置要使用的切片数
        request.setSlices(2);
        //等待查询请求更新作为 TimeValue 执行的超时
        request.setTimeout(TimeValue.timeValueMinutes(2));
        //通过查询调用更新后刷新索引
        request.setRefresh(true);
        //设置索引选项
        request.setIndicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
        restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        //client.deleteByQueryAsync(request, RequestOptions.DEFAULT, listener);
    }

//    listener = new ActionListener<BulkByScrollResponse>() {
//        @Override
//        public void onResponse(BulkByScrollResponse bulkResponse) {
//
//        }
//
//        @Override
//        public void onFailure(Exception e) {
//
//        }
//    };

}
