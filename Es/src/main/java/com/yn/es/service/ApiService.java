package com.yn.es.service;

import com.yn.es.domain.Posts;
import com.yn.es.repository.EsPostsesPostsRepository;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiService {
    @Autowired
    private EsPostsesPostsRepository esPostsesPostsRepository;

    /**
     * 删除文档
     * @param id
     * @return
     */
    public boolean deletePosts(Long id) {
        try {
            esPostsesPostsRepository.deleteById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 插入文档
     * @param aLog
     * @return
     */
    public Posts savePosts(Posts aLog) {
        Posts Posts = new Posts();
        try {
            Posts = esPostsesPostsRepository.save(aLog);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return Posts;
    }

    /**
     * 按id查询
     * @param id
     * @return
     */
    public Posts findOnePosts(long id) {
        Posts Posts = new Posts();
        try {
            Posts = esPostsesPostsRepository.findById(id).get();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return Posts;
    }

    /**
     * 查询全部文档
     * @return
     */
    public List<Posts> findAllPosts() {
        List<Posts> list = new ArrayList<>();
        try {
            Iterable<Posts> aIterable = esPostsesPostsRepository.findAll();
            for (Posts Posts : aIterable) {
                list.add(Posts);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return list;
    }

    /**
     * 按条件过滤查询
     * @param b_time
     * @param e_time
     * @return
     */
    public List<Posts> findPostsByDate(String b_time,String e_time) {
        List<Posts> list = new ArrayList<>();
        try {
            // 单个字符串
//       QueryBuilder qb0 = QueryBuilders.termQuery("id", "0");
            // 闭区间
            QueryBuilder qb1 = QueryBuilders.rangeQuery("c_time").from(b_time).to(e_time);
            // 大于
            QueryBuilder qb2 = QueryBuilders.rangeQuery("uid").gt(0);
            // 过滤多条件
            QueryBuilder qb = QueryBuilders.boolQuery().must(qb1).must(qb2);

            Iterable<Posts> aIterable = esPostsesPostsRepository.search(qb);
            for (Posts Posts : aIterable) {
                list.add(Posts);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return list;
    }
}
