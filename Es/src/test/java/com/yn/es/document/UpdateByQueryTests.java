package com.yn.es.document;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
@Slf4j
@Service
public class UpdateByQueryTests {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 条件修改
     *
     * @throws IOException
     */
    @Test
    public void updateByQuery() throws IOException {
        UpdateByQueryRequest request = new UpdateByQueryRequest("posts2");
        //防止版本冲突
        request.setConflicts("proceed");
        //条件查询限制
        request.setQuery(new TermQueryBuilder("user.keyword", "bob"));
        //来限制处理文档的数量
        request.setMaxDocs(10);
        //使用 setBatchSize 更改批次大小
        request.setMaxDocs(100);
        //pipeline 使用摄取功能
//        request.setPipeline("my_pipeline");
        //脚本修改
//        request.setScript(
//                new Script(
//                        ScriptType.INLINE, "painless",
//                        "if (ctx._source.user == 'Lucy') {ctx._source.likes++;}",
//                        Collections.emptyMap()));
        request.setScript(new Script("ctx._source['user']='bob1';"));
        //设置要使用的切片数
        request.setSlices(2);
        //等待查询请求更新作为 TimeValue 执行的超时
        request.setTimeout(TimeValue.timeValueMinutes(2));
        //通过查询调用更新后刷新索引
        request.setRefresh(true);
        //设置索引选项
        request.setIndicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
        restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
        //client.updateByQueryAsync(request, RequestOptions.DEFAULT, listener);
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
