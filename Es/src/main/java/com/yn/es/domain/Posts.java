package com.yn.es.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 搜索商品的信息
 * Created by macro on 2018/6/19.
 */
@Document(indexName = "posts-000001")
@Data
public class Posts implements Serializable {
    private static final long serialVersionUID = -1L;
    @Id
    private Long id;
    @Field(type = FieldType.Keyword)
    private String user;

    @Field(type = FieldType.Keyword)
    private String postDate;

    @Field(type = FieldType.Keyword)
    private String message;





}
