package com.yn.es.model;

import com.yn.es.enums.FieldTypeEnum;
import lombok.Data;

@Data
public class EsField {
    //字段对应操作功能
    private FieldTypeEnum fieldTypeEnum;
    //字段名称
    private String field;
    //字段对应的值
    private String value;
    public EsField(){

    }
    public EsField(String field ,String value , FieldTypeEnum fieldTypeEnum){
        this.value = value;
        this.field = field;
        this.fieldTypeEnum  = fieldTypeEnum;
    }
}