package com.yn.es;

import com.yn.es.service.DeleteService;
import com.yn.es.service.ExistsService;
import com.yn.es.utils.EsUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
@Slf4j
@Service
public class DeleteTests implements DeleteService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private EsUtils esUtils;

    /**
     * 通过id进行删除
     *
     * @throws IOException
     */
    @Test
    public void deleteById() throws IOException, InterruptedException {
        DeleteRequest deleteRequest = new DeleteRequest(
                "posts",
                "1");
        restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        esUtils.deleteDataById("posts","1");
    }

    /**
     * 通过id进行删除
     *
     * @throws IOException
     */
    @Test
    public void deleteAsyncById() throws IOException, InterruptedException {
        DeleteRequest deleteRequest = new DeleteRequest(
                "posts",
                "1");
        restHighLevelClient.deleteAsync(deleteRequest, RequestOptions.DEFAULT, this);
        Thread.sleep(10000);
    }

    @Override
    public void onResponse(DeleteResponse deleteResponse) {

    }

    @Override
    public void onFailure(Exception e) {

    }
}
