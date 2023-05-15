package com.yn.es.repository;

import com.yn.es.domain.Posts;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 搜索商品ES操作类
 * Created by macro on 2018/6/19.
 */
public interface EsPostsesPostsRepository extends ElasticsearchRepository<Posts, Long> {
}
