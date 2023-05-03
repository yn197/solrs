# ES的生命周期创建

## 一：配置生命周期管理

### 1.**配置lifecycle检测时间** 

```
PUT /_cluster/settings
{
  "transient": {
    "indices.lifecycle.poll_interval": "1s" 
  }
}
```

### 2.**创建一个索引策略** 

```
# /_ilm/policy为固定格式，leefs_ilm_policy为创建索引策略名称
PUT /_ilm/policy/leefs_ilm_policy
{
  # policy:配置策略
  "policy": {
    # phases:阶段配置
    "phases": {
      "hot": {
        "actions": {
    	  # rollover:滚动更新
          "rollover": {
    		# max_docs:文档数量最大为5执行操作
            "max_docs": "5"
          }
        }
      },
      "warm": {
        # min_age:该阶段最小停留时长
        "min_age": "10s",
        "actions": {
          # allocate:指定一个索引的副本数
          "allocate": {
          	# number_of_replicas:进行索引副本数量设置
            "number_of_replicas": 0
          }
        }
      },
      "delete": {
        "min_age": "20s",
        "actions": {
          # delete:删除索引，如果没有该方法即使到删除阶段也不执行删除操作
          "delete": {}
        }
      }
    }
  }
}
```

**说明**

在创建索引策略时，不是每个阶段都是必须的，除了hot阶段，其他阶段都可以根据需求进行省略。

包括滚动更新(rollover)在内的所有actions中的方法都可以根据需求进行省略。

### 3.**创建一个索引模版，指定使用的索引策略** 

```
# leefs_ilm_template:索引模版名称
PUT _template/leefs_ilm_template
{
  # 模版匹配的索引名以"leefs_logs-"开头
  "index_patterns": ["leefs_logs-*"],                 
  "settings": {
    # number_of_shard：设置主分片数量
    "number_of_shards": 1,
    # number_of_replicas：设置副本分片数量
    "number_of_replicas": 1,
    # 通过索引策略名称指定模版所匹配的索引策略
    "index.lifecycle.name": "leefs_ilm_policy", 
    # 索引rollover后切换的索引别名为leefs_logs
    "index.lifecycle.rollover_alias": "leefs_logs"
  }
}
```

### 4.**创建一个符合上述索引模版的索引** 

```
# 清空之前索引
DELETE leefs_logs*

# 创建第一个索引
PUT leefs_logs-000001
{
  "aliases": {
    //设置索引别名为leefs_logs的索引
    "leefs_logs": {
      //允许索引被写入数据
      "is_write_index":true
    }
  }
}
```

### 5.**向索引中写入数据，使索引触发滚动更新策略** 

```
# refresh写入后更新
POST leefs_logs/_doc?refresh
{
  "name":"llc"
}
上方命名执行5次，使其触发滚动更新策略
```

### 6.**查看索引所处阶段** 

```
# 查询索引名称以leefs_logs-开头的索引信息
GET leefs_logs-*/_ilm/explain
```

### 7.查询索引别名

```
GET _alias/
```

## 二:更新策略

如果没有index应用这份策略，那么我们可以直接更新该策略。

如果有index应用了这份策略，那么当前正在执行的阶段不会同步修改，当当前阶段结束后，会进入新版本策略的下个阶段。

如果更换了策略，当前正在执行的阶段不会变化，在结束当前阶段后，将会由新的策略管理下一个生命周期。

 三：启动和停止索引生命周期管理

**ILM默认是开启状态。**

**（1）查看ILM的当前运行状态**

```
GET  _ilm/status
```

执行结果 

```
{
  "operation_mode" : "RUNNING"
}
```

**ILM的操作模式**

| 阶段/action | 优先级设置                            |
| ----------- | ------------------------------------- |
| 正在运行    | 正常运行，所有策略均正常执行          |
| 停止        | ILM已收到停止请求，但仍在处理某些策略 |
| 已停止      | 这表示没有执行任何策略的状态          |

**停止ILM**

```
POST  _ilm/stop
复制代码
```

**停止后，所有其他政策措施都将停止。这将反映在状态API中**

```
{
    "operation_mode": "STOPPING"
}

```

**然后，ILM服务将异步地将所有策略运行到可以安全停止的位置。在ILM确认它是安全的之后，它将移至该`STOPPED`模式**

 ```
{
    "operation_mode": "STOPPED"
}
 ```

**启动ILM** 

```
POST  _ilm/start
```

**启动后查询状态** 

```
{
  "operation_mode": "RUNNING"
}
```



链接：[Elasticsearch索引生命周期管理 - 掘金 (juejin.cn)](https://juejin.cn/post/7046943172592664607) 