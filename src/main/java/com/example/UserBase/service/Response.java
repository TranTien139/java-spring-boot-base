package com.example.UserBase.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Response {
    public Map<String, Object> responseSuccess(Object data){
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", Boolean.TRUE);
        result.put("message", "Success");
        result.put("data", data);
        return result;
    }

    public Map<String, Object> responseError(Object data){
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", Boolean.FALSE);
        result.put("message", "Error");
        result.put("data", data);
        return result;
    }
}
