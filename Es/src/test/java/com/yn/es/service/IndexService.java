package com.yn.es.service;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexResponse;

public interface IndexService extends ActionListener<IndexResponse> {
}
