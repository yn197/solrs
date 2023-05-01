package com.yn.es.search;

import com.yn.es.model.EsField;
import com.yn.es.model.EsPage;
import com.yn.es.utils.ElasticsearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
        //searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        //模糊匹配
        QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("user", "Lucy")
                //对匹配查询启用模糊匹配
                .fuzziness(Fuzziness.AUTO)
                //在匹配查询上设置前缀长度选项
                .prefixLength(3)
                //设置最大扩展选项来控制查询的模糊过程
                .maxExpansions(10);
        searchSourceBuilder.query(matchQueryBuilder);

        //按 _score 降序排序（默认）
        //searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        //也按 _id 字段升序排序
        //searchSourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.ASC));

        //通配符
        //String[] includeFields = new String[] {"title", "innerObject.*"};
        //String[] excludeFields = new String[] {"user"};
        //searchSourceBuilder.fetchSource(includeFields, excludeFields);

        //高亮显示
        //SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //HighlightBuilder highlightBuilder = new HighlightBuilder();
        //HighlightBuilder.Field highlightTitle =new HighlightBuilder.Field("title");
        //highlightTitle.highlighterType("unified");
        //highlightBuilder.field(highlightTitle);
        //HighlightBuilder.Field highlightUser = new HighlightBuilder.Field("user");
        //highlightBuilder.field(highlightUser);
        //searchSourceBuilder.highlighter(highlightBuilder);

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
        EsPage<Map> dataPage = elasticsearchUtils.selectDataPage("posts", 1, 10, newArrayList, Map.class);
        log.info("查询结果{}", dataPage);
    }

    /**
     * 聚合查询
     *
     * @throws IOException
     */
    @Test
    public void aggregation() throws Exception {
        SearchRequest searchRequest = new SearchRequest("posts");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("age").field("age");
        AvgAggregationBuilder aggregationBuilder = AggregationBuilders.avg("average_age").field("age");

        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //异步
        //client.searchAsync(searchRequest, RequestOptions.DEFAULT, listener);
        Terms terms = (Terms) response.getAggregations().get("age");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        HashMap<String, Long> map = new HashMap<>();
        for (Terms.Bucket bucket : buckets) {
            long docCount = bucket.getDocCount();
            String keyAsString = bucket.getKeyAsString();
            map.put(keyAsString, docCount);
        }

        log.info("分组聚合的bulk数据:{}", map);
        Aggregation averageAge = response.getAggregations().get("average_age");
        double value = ((ParsedAvg) averageAge).getValue();
        log.info("求平均值:{}", value);
    }

    //todo   时间条件搜索

    ActionListener<SearchResponse> listener = new ActionListener<SearchResponse>() {
        @Override
        public void onResponse(SearchResponse searchResponse) {

        }

        @Override
        public void onFailure(Exception e) {

        }
    };
}
