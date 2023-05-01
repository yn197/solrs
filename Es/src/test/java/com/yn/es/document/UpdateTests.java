package com.yn.es.document;

import com.yn.es.service.UpdateService;
import com.yn.es.utils.EsUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

@SpringBootTest
@Slf4j
@Service
public class UpdateTests implements UpdateService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private EsUtils esUtils;

    /**
     * 更新操作
     *
     * @throws IOException
     */
    @Test
    public void updateById() throws IOException {
        Map<String, Object> parameters = singletonMap("age", 18);
        esUtils.updateDataById(parameters, "posts", "5");
    }


    /**
     * 部分文档源作为 Object 密钥对提供，将转换为 JSON 格式
     *
     * @throws IOException
     */
    @Test
    public void Object() throws IOException {
        UpdateRequest request = new UpdateRequest("posts", "5")
                .doc("updated", new Date(), "reason", "daily update");
        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 部分文档源作为 Map 提供，自动转换为 JSON 格式
     *
     * @throws IOException
     */
    @Test
    public void Map() throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("updated", new Date());
        jsonMap.put("reason", "daily update");
        UpdateRequest request = new UpdateRequest("posts", "3")
                .doc(jsonMap);
        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    /**
     * XContentBuilder
     *
     * @throws IOException
     */
    @Test
    public void XContentBuilder() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.timeField("updated", new Date());
            builder.field("reason", "daily update");
        }
        builder.endObject();
        UpdateRequest request = new UpdateRequest("posts", "2").doc(builder);
        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 如果该文档尚不存在，则可以使用 upsert 方法定义一些将作为新文档插入的内容：
     * todo 存在问题
     *
     * @throws IOException
     */
    @Test
    public void upsert() throws IOException {
        UpdateRequest request = new UpdateRequest(
                "posts",
                "doc",
                "5");
        String jsonString = "{" +
                "\"updated\":\"2017-01-01\"," +
                "\"reason\":\"daily update\"" +
                "}";
//update数据源
        request.doc(jsonString, XContentType.JSON);
//开户upsert
        request.docAsUpsert(true);
//insert数据源
        request.upsert(jsonString, XContentType.JSON);


        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 更新操作
     * todo 存在问题
     *
     * @throws IOException
     */
    @Test
    public void updateScriptById() throws IOException {
        UpdateRequest request = new UpdateRequest("posts", "3");
        Map<String, Object> parameters = singletonMap("count", 4);

        Script inline = new Script(ScriptType.INLINE, "painless",
                "ctx._source.field += params.count", parameters);
        request.script(inline);

        Script stored = new Script(
                ScriptType.STORED, null, "increment-field", parameters);
        request.script(stored);
        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 异步修改
     */
    @Test
    public void updateAsync() throws IOException, InterruptedException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("updated", new Date());
        jsonMap.put("reason", "daily update");
        UpdateRequest request = new UpdateRequest("posts", "3")
                .doc(jsonMap);
        restHighLevelClient.updateAsync(request, RequestOptions.DEFAULT,this);
        Thread.sleep(500);
    }

    @Override
    public void onResponse(UpdateResponse updateResponse) {
        updateResponse.getGetResult().getDocumentFields();
    }

    @Override
    public void onFailure(Exception e) {

    }
}
