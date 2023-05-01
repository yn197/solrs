# solrs
搜索引擎学习，例如es，clickhouse等等

1.索引同步常用操作

1.1简单的reindex

```
POST _reindex
{
  "source": {
    "index": "pigg_test"
  },
  "dest": {
    "index": "pigg_test2"
  }
}
```

1.2只创建目标索引中缺少的文档

```
POST _reindex
{
  "source": {
    "index": "pigg_test"
  },
  "dest": {
    "index": "pigg_test2",
    "op_type": "create"
  }
}
```

1.3设置批次大小

```
POST _reindex
{
  "source": {
    "index": "pigg_test",
    "size": 5000
  },
  "dest": {
    "index": "pigg_test2"
  }
}

```

1.4遇到冲突继续

```
POST _reindex
{
  "conflicts": "proceed", 
  "source": {
    "index": "pigg_test"
  },
  "dest": 
    "index": "pigg_test2",
    "op_type": "create"
  }
}

```

1.5只reindex符合条件的数据

```
POST _reindex
{
  "source": {
    "index": "pigg_test",
    "query": {
      "term": {
        "name.keyword": {
          "value": "冬哥"
        }
      }
    }
  },
  "dest": {
    "index": "pigg_test2"
  }
}
```

1.6只同步源index里部分字段

```
POST _reindex
{
  "source": {
    "index": "pigg_test",
    "_source": ["name", "age"]
  },
  "dest": {
    "index": "pigg_test2"
  }
}

```

1.7屏蔽掉不想同步的字段

```
POST _reindex
{
  "source": {
    "index": "pigg_test",
    "_source": {
      "excludes": ["name"]
    }
  },
  "dest": {
    "index": "pigg_test2"
  }
}

```

1.8用script脚本在同步时做数据处理

es支持的script非常强大，这个不详细讲，仅仅举个简单的例子 

```
POST _reindex
{
  "source": {
    "index": "pigg_test"
  },
  "dest": {
    "index": "pigg_test2"
  },
  "script": {
    "source": "ctx._source.age += 2",
    "lang": "painless"
  }
}

```

1.9字段重新命名

同样是用script，讲name属性重命名为newName 

```
POST _reindex
{
  "source": {
    "index": "pigg_test"
  },
  "dest": {
    "index": "pigg_test2"
  },
  "script": {
    "source": "ctx._source.newName = ctx._source.remove(\"name\")",
    "lang": "painless"
  }
}

```







出自:[Elasticsearch教程(18) reindex 重建索引，非常实用的功能_es reindex 屏蔽字段_国服亚瑟的博客-CSDN博客](https://blog.csdn.net/winterking3/article/details/108242124) 