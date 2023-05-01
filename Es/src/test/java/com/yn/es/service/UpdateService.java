package com.yn.es.service;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.update.UpdateResponse;

public interface UpdateService extends ActionListener<UpdateResponse> {
}
