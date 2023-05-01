package com.yn.es.service;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;

public interface DeleteService extends ActionListener<DeleteResponse> {
}
