package com.dubboclub.web.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import  com.dubboclub.web.model.BasicResponse;

/**
 * Created by bieber on 2015/6/15.
 */
@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(DataHadChangedException.class)
    public @ResponseBody BasicResponse dataHadChanged(){
        BasicResponse response = new BasicResponse();
        response.setResult(BasicResponse.FAILED);
        response.setMemo("数据已经发生变更！");
        return response;
    }
}
