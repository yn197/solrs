package com.yn.es.search;

import com.yn.es.model.EsField;
import com.yn.es.model.EsPage;
import com.yn.es.utils.ElasticsearchUtils;
import com.yn.es.utils.EsUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class SearchTests {
    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private ElasticsearchUtils elasticsearchUtils;

    /**
     * 查询
     *
     * @throws IOException
     */
    @Test
    public void search() throws IOException {
        /**

         POST /posts/_search
         {
         "query": {
         "match_all": {}
         }
         }
         */
        List<Map<String, Object>> mapList = new ArrayList<>();
        //如果没有参数，这将针对所有索引运行
        SearchRequest searchRequest = new SearchRequest("posts");
        //大多数搜索参数都添加到 SearchSourceBuilder 中。它为进入搜索请求正文的所有内容提供设置器。
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //将 match_all 查询添加到 SearchSourceBuilder 。
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //将 SearchSourceBuilder 添加到 SearchRequest
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        for (SearchHit searchHit : searchHits) {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            mapList.add(sourceAsMap);
        }
        log.info("查询结果{}", mapList);
    }
    /**
     * 分页查询
     *
     * @throws IOException
     */
    @Test
    public void selectDataPage() throws IOException {
        ArrayList<EsField> newArrayList = Lists.newArrayList();
        EsPage<Map> dataPage = elasticsearchUtils.selectDataPage("posts", 1, 2, newArrayList, Map.class);
        log.info("查询结果{}", dataPage);
    }
}
