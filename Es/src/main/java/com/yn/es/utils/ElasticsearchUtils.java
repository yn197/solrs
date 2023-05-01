package com.yn.es.utils;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yn.es.exception.BusinessException;
import com.yn.es.model.EsBaseData;
import com.yn.es.model.EsField;
import com.yn.es.model.EsPage;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * es工具类型
 *
 * @author lsj
 * @date 2021/11/3 15:17
 */
@Component
@Slf4j
public class ElasticsearchUtils {
    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * 添加索引
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean addIndex(String indexName) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        CreateIndexResponse createIndexResponse = null;
        try {
            //1.使用client获取操作索引对象
            IndicesClient indices = restHighLevelClient.indices();
            //2.具体操作获取返回值
            //2.1 设置索引名称
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            createIndexResponse = indices.create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("elasticsearch addindex error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch addindex error , meassage=" + e.getMessage());
        }

        //3.根据返回值判断结果
        return createIndexResponse.isAcknowledged();
    }

    /**
     * 删除索引
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean deleteIndex(String indexName) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        AcknowledgedResponse deleteRespone = null;
        try {
            //1.使用client获取操作索引对象
            IndicesClient indices = restHighLevelClient.indices();
            //2.具体操作获取返回值
            //2.1 设置索引名称
            DeleteIndexRequest request = new DeleteIndexRequest("twitter_two");//指定要删除的索引名称
            deleteRespone = indices.delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("elasticsearch deleteIndex error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch deleteIndex error , meassage=" + e.getMessage());
        }
        //3.根据返回值判断结果
        return deleteRespone.isAcknowledged();
    }

    /**
     * 创建数据
     *
     * @param indexName
     * @param id
     * @param data
     * @throws IOException
     */
    public boolean addData(String indexName, String id, Object data) {
        Assert.notNull(data, "Elasticsearch exception data null");
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        IndexResponse indexResponse = null;
        try {
            //准备文档
            String jsonString = JSONObject.toJSONString(data);
            Map jsonMap = JSONObject.parseObject(jsonString, Map.class);
            //创建请求
            IndexRequest indexRequest = new IndexRequest(indexName).id(id);
            //指定文档内容
            indexRequest.source(jsonMap);
            //true 当存在相同的_id时，插入会出现异常； false 当存在相同_id时，插入会进行覆盖；
            indexRequest.create(true);
            //通过client进行http请求
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("elasticsearch addOrUpdateDoc error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch addOrUpdateDoc error , meassage=" + e.getMessage());
        }
        return indexResponse.getResult().equals(DocWriteResponse.Result.CREATED);
    }

    /**
     * 创建文档id存在则更新文档
     *
     * @param indexName
     * @param id
     * @param data
     * @throws IOException
     */
    public boolean addOrUpdateData(String indexName, String id, Object data) {
        Assert.notNull(data, "Elasticsearch exception data null");
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        IndexResponse indexResponse = null;
        try {
            //准备文档
            String jsonString = JSONObject.toJSONString(data);
            Map jsonMap = JSONObject.parseObject(jsonString, Map.class);
            //创建请求
            IndexRequest indexRequest = new IndexRequest(indexName).id(id);
            //指定文档内容
            indexRequest.source(jsonMap);
            //通过client进行http请求
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("elasticsearch addOrUpdateDoc error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch addOrUpdateDoc error , meassage=" + e.getMessage());
        }
        return indexResponse.getResult().equals(DocWriteResponse.Result.CREATED);
    }

    /**
     * 单条更新
     *
     * @param indexName
     * @param id
     * @param data
     * @return
     * @throws IOException
     */
    public boolean updateData(String indexName, String id, Object data) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(indexName, id);
        //准备文档
        String jsonString = JSONObject.toJSONString(data);
        Map jsonMap = JSONObject.parseObject(jsonString, Map.class);
        updateRequest.doc(jsonMap);
        updateRequest.timeout(TimeValue.timeValueSeconds(1));
        updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        //数据为存储而不是更新
        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        return update.getGetResult().equals(DocWriteResponse.Result.UPDATED);
    }


    /**
     * 批量新增数据
     *
     * @param index
     * @param datas
     * @return
     */
    public boolean addBatchData(String index, List<? extends EsBaseData> datas) {
        Assert.hasLength(index, "Elasticsearch exception indexName null");
        Assert.notEmpty(datas, "addBatchData elastaicsearch exception datas is null");
        if (datas.size() > 100000) {
            log.error("es add batch data too large{}", datas.size());
            throw new BusinessException("es add batch data too large" + datas.size());
        }
        BulkResponse bulk = null;
        try {
            BulkRequest request = new BulkRequest();
            datas.forEach(data -> {
                String source = JSON.toJSONString(data);
                request.add(new IndexRequest(data.getIndexName()).id(data.esId).source(source, XContentType.JSON));
            });
            bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        } catch (Exception e) {
            log.error("elasticsearch addBatchData error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch addBatchData error , meassage=" + e.getMessage());
        }
        return !bulk.hasFailures();
    }

    /**
     * 通过id删除数据
     *
     * @param indexName
     * @param id
     * @return
     */
    public boolean deleteDataById(String indexName, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
        DeleteResponse response = null;
        try {
            response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("elasticsearch deleteDocById error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch deleteDataById error , meassage=" + e.getMessage());
        }
        return response.getResult().equals(DocWriteResponse.Result.DELETED);
    }


    /**
     * 通过条件删除数据
     *
     * @param indexName
     * @param conditionFileds
     * @return
     */
    public boolean deleteDataByCondition(String indexName, List<EsField> conditionFileds) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        Assert.notEmpty(conditionFileds, "Elasticsearch exception conditionFileds null");
        BulkByScrollResponse resp = null;
        try {
            DeleteByQueryRequest request = new DeleteByQueryRequest(indexName);
//            SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(conditionFileds);
//            request.getSearchRequest().source(searchSourceBuilder);
            // 更新时版本冲突
            request.setConflicts("proceed");
            //构建条件
            setDeletCondition(conditionFileds, request);
            // 刷新索引
            request.setRefresh(true);

            resp = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("elasticsearch deleteDataByCondition error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch deleteDataByCondition error , meassage=" + e.getMessage());
        }
        return resp.getStatus().getDeleted() > 0;
    }


    /**
     * 通过条件更新数据
     *
     * @param indexName
     * @param conditionFileds
     * @return
     */
    public boolean updateDataByCondition(String indexName, List<EsField> conditionFileds, Object data) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        Assert.notEmpty(conditionFileds, "Elasticsearch exception conditionFileds null");
        Assert.notNull(data, "elasticsearch updateDataByCondition data is null ");
        BulkByScrollResponse resp = null;
        try {
            UpdateByQueryRequest request = new UpdateByQueryRequest(indexName);
            //设置分片并行
            request.setSlices(2);
            //设置版本冲突时继续执行
            request.setConflicts("proceed");
            //构建条件
            setUpdateConfition(conditionFileds, request);
            //设置更新完成后刷新索引 ps很重要如果不加可能数据不会实时刷新
            request.setRefresh(true);
            StringBuffer scriptContext = buildScriptContext(data);
            //设置要修改的内容可以多个值多个用；隔开
            request.setScript(new Script(scriptContext.toString()));
            resp = restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("elasticsearch updateDataByCondition error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch updateDataByCondition error , meassage=" + e.getMessage());
        }
        return resp.getStatus().getUpdated() > 0;
    }

    /**
     * 根据id查询文档
     */
    public <T> T selectDataById(String indexName, String id, Class<T> c) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        Assert.hasLength(id, "Elasticsearch exception id null");
        GetResponse response = null;
        try {
            //设置查询的索引、文档
            GetRequest indexRequest = new GetRequest(indexName, id);
            response = restHighLevelClient.get(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("elasticsearch selectDataById error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch selectDataById error , meassage=" + e.getMessage());
        }
        String res = response.getSourceAsString();
        return JSONObject.parseObject(res, c);
    }


    /**
     * 条件查询
     *
     * @param indexName
     * @param conditionFileds 条件
     * @param c               返回对象类型
     * @return
     */
    public <T> List<T> selectDataList(String indexName, List<EsField> conditionFileds, Class<T> c) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        Assert.notNull(c, "Class<T>  is null ");
        List<T> res = null;
        try {
            // 创建检索请求
            SearchRequest searchRequest = new SearchRequest();
            // 指定索引
            searchRequest.indices(indexName);
            SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(conditionFileds);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //分析结果
            SearchHit[] hits = searchResponse.getHits().getHits();
            res = new ArrayList<>();
            for (SearchHit hit : hits
            ) {
                String data = hit.getSourceAsString();
                T t = JSONObject.parseObject(data, c);
                log.info("data={}", data);
                res.add(t);
            }
        } catch (Exception e) {
            log.error("elasticsearch selectDataList error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch selectDataList error , meassage=" + e.getMessage());
        }
        return res;
    }

    /**
     * 条件查询
     *
     * @param indexName
     * @param conditionFileds 条件
     * @param c               返回对象类型
     * @return
     */
    public <T> EsPage<T> selectDataPage(String indexName, Integer pageNum, Integer pageSize, List<EsField> conditionFileds, Class<T> c) {
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        Assert.notNull(c, "Class<T>  is null ");
        List<T> res = null;
        //总记录数
        Integer total = 0;
        try {
            // 创建检索请求
            SearchRequest searchRequest = new SearchRequest();
            // 指定索引
            searchRequest.indices(indexName);
            SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(conditionFileds);
            //设置分页

            searchSourceBuilder.from((pageNum - 1) * pageSize).size(pageSize);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //分析结果
            SearchHit[] hits = searchResponse.getHits().getHits();
            total = new Long(searchResponse.getHits().getTotalHits().value).intValue();
            res = new ArrayList<>();
            for (SearchHit hit : hits
            ) {
                String data = hit.getSourceAsString();
                T t = JSONObject.parseObject(data, c);
                log.info("data={}", data);
                res.add(t);
            }
        } catch (Exception e) {
            log.error("elasticsearch selectDataPage error , meassage = {}", e.getMessage());
            //打印轨迹
            log.error(e.getMessage(), e);
            throw new BusinessException("elasticsearch selectDataPage error , meassage=" + e.getMessage());
        }
        return new EsPage<>(pageNum, pageSize, total, res);
    }

    /**
     * 创建搜索条件
     *
     * @param conditionFileds
     * @return
     */
    private static SearchSourceBuilder buildSearchSourceBuilder(List<EsField> conditionFileds) {
        // 指定DSL
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建条件
        if (!CollectionUtils.isEmpty(conditionFileds)) {
            for (EsField condFiled :
                    conditionFileds) {
                switch (condFiled.getFieldTypeEnum()) {
                    case ORDER_ASC:
                        //升序
                        searchSourceBuilder.sort(condFiled.getField(), SortOrder.ASC);
                        break;
                    case ORDER_ESC:
                        //降序
                        searchSourceBuilder.sort(condFiled.getField(), SortOrder.DESC);
                        break;
                    case VAGUE_QUERY:
                        // 模糊查询
                        searchSourceBuilder.query(QueryBuilders.matchQuery(condFiled.getField(), condFiled.getValue()).fuzziness(Fuzziness.AUTO));
                        break;
                    case PRECISE_QUERY:
                        searchSourceBuilder.query(QueryBuilders.matchQuery(condFiled.getField(), condFiled.getValue()));
                        break;
                    default:
                        //默认精准查询
                        searchSourceBuilder.query(QueryBuilders.matchQuery(condFiled.getField(), condFiled.getValue()));
                        break;
                }
            }
        }
        return searchSourceBuilder;
    }


    /**
     * 构建删除条件
     *
     * @param conditionFileds
     * @param request
     */
    private void setDeletCondition(List<EsField> conditionFileds, DeleteByQueryRequest request) {
        if (!CollectionUtils.isEmpty(conditionFileds)) {
            for (EsField condFiled :
                    conditionFileds) {
                switch (condFiled.getFieldTypeEnum()) {
                    case VAGUE_QUERY:
//                           模糊查询
                        request.setQuery(QueryBuilders.matchQuery(condFiled.getField(), condFiled.getValue()).fuzziness(Fuzziness.AUTO));
                        break;
                    case PRECISE_QUERY:
                        request.setQuery(QueryBuilders.matchQuery(condFiled.getField(), condFiled.getValue()));
                        break;
                    default:
                        log.error("field type error ,only supprt VAGUE_QUERY and PRECISE_QUERY");
                        throw new BusinessException(" field type error ,only supprt VAGUE_QUERY and PRECISE_QUERY");
                }
            }
        }
    }

    /**
     * 构建修改条件
     *
     * @param conditionFileds
     * @param request
     */
    private void setUpdateConfition(List<EsField> conditionFileds, UpdateByQueryRequest request) {
        if (!CollectionUtils.isEmpty(conditionFileds)) {
            for (EsField condFiled :
                    conditionFileds) {
                switch (condFiled.getFieldTypeEnum()) {
                    case VAGUE_QUERY:
//                           模糊查询
                        request.setQuery(QueryBuilders.matchQuery(condFiled.getField(), condFiled.getValue()).fuzziness(Fuzziness.AUTO));
                        break;
                    case PRECISE_QUERY:
                        request.setQuery(QueryBuilders.matchQuery(condFiled.getField(), condFiled.getValue()));
                        break;
                    default:
                        log.error("field type error ,only supprt VAGUE_QUERY and PRECISE_QUERY");
                        throw new BusinessException(" field type error ,only supprt VAGUE_QUERY and PRECISE_QUERY");
                }
            }
        }
    }


    /**
     * 创建修改script内容
     *
     * @param data
     * @return
     */
    private StringBuffer buildScriptContext(Object data) {
        //准备文档
        String jsonString = JSONObject.toJSONString(data);
        Map<String, String> jsonMap = JSONObject.parseObject(jsonString, Map.class);
        String tem = "ctx._source['key']='value'";
        StringBuffer scriptContext = new StringBuffer();
        for (String key :
                jsonMap.keySet()) {
            String value = jsonMap.get(key);
            scriptContext.append(tem.replace("key", key).replace("value", value))
                    .append(";");

        }
        return scriptContext;
    }
}

