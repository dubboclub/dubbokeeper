package com.dubboclub.dk.storage.mongodb;


import com.dubboclub.dk.storage.model.Annotation;
import com.dubboclub.dk.storage.model.Application;
import com.dubboclub.dk.storage.model.Service;
import com.dubboclub.dk.storage.model.Span;
import com.dubboclub.dk.storage.model.Trace;
import com.dubboclub.dk.storage.mongodb.dao.TracingAnnotationDao;
import com.dubboclub.dk.storage.mongodb.dao.TracingApplicationDao;
import com.dubboclub.dk.storage.mongodb.dao.TracingServiceDao;
import com.dubboclub.dk.storage.mongodb.dao.TracingSpanDao;
import com.dubboclub.dk.storage.mongodb.dao.TracingTraceDao;
import com.dubboclub.dk.storage.mongodb.dto.TracingAnnotationDto;
import com.dubboclub.dk.storage.mongodb.dto.TracingApplicationDto;
import com.dubboclub.dk.storage.mongodb.dto.TracingServiceDto;
import com.dubboclub.dk.storage.mongodb.dto.TracingSpanDto;
import com.dubboclub.dk.storage.mongodb.dto.TracingTraceDto;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zetas on 2016/7/14.
 */
public class TraceDataQuery {//implements com.dubboclub.dk.storage.TraceDataQuery {
/*
    private TracingAnnotationDao annotationDao;
    private TracingApplicationDao applicationDao;
    private TracingServiceDao serviceDao;
    private TracingSpanDao spanDao;
    private TracingTraceDao traceDao;
    private ContextHolder contextHolder;

    public void setTracingAnnotationDao(TracingAnnotationDao annotationDao) {
        this.annotationDao = annotationDao;
    }

    public void setTracingApplicationDao(TracingApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
    }

    public void setTracingServiceDao(TracingServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    public void setTracingSpanDao(TracingSpanDao spanDao) {
        this.spanDao = spanDao;
    }

    public void setTracingTraceDao(TracingTraceDao traceDao) {
        this.traceDao = traceDao;
    }

    public void setContextHolder(ContextHolder contextHolder) {
        this.contextHolder = contextHolder;
    }

    @Override
    public List<Application> findAllApplication() {
        List<Application> applicationList = new LinkedList<Application>();
        List<TracingApplicationDto> applicationDtoList = applicationDao.findAll();
        if (applicationDtoList != null) {
            for (TracingApplicationDto dto : applicationDtoList) {
                Application application = new Application();
                application.setId(dto.getApplicationId());
                application.setName(dto.getApplicationName());

                applicationList.add(application);
            }
        }
        return applicationList;
    }

    @Override
    public List<Service> findAllService() {
        List<Service> serviceList = new LinkedList<Service>();
        List<TracingServiceDto> serviceDtoList = serviceDao.findAll();
        if (serviceDtoList != null) {
            for (TracingServiceDto dto : serviceDtoList) {
                Service service = new Service();
                service.setId(dto.getServiceId());
                service.setName(dto.getServiceName());

                serviceList.add(service);
            }
        }
        return serviceList;
    }

    @Override
    public List<Trace> findTrace(Integer serviceId, Long startTime, Long endTime, Integer minDuration, Integer maxDuration) {
        List<TracingTraceDto> traceDtoList = traceDao.findByServiceIdAndDurationBetweenAndTimeBetween(serviceId, startTime, endTime, minDuration, maxDuration);
        List<Trace> traceList = new LinkedList<Trace>();
        for (TracingTraceDto dto : traceDtoList) {
            Trace trace = new Trace();
            trace.setTraceId(dto.getTraceId());
            trace.setDuration(dto.getDuration());
            trace.setTime(dto.getTime());
            trace.setServiceName(findServiceName(dto));

            traceList.add(trace);
        }
        return traceList;
    }

    @Override
    public Span findSpanBySpanId(String spanId) {
        TracingSpanDto dto = spanDao.findBySpanId(spanId);
        Span span = null;
        if (dto != null) {
            span = create(dto);
        }
        return span;
    }

    @Override
    public List<Span> findSpanByTraceId(String traceId) {
        List<TracingSpanDto> spanDtoList = spanDao.findByTraceId(traceId);
        List<Span> spanList = new LinkedList<Span>();
        for (TracingSpanDto dto : spanDtoList) {
            spanList.add(create(dto));
        }
        return spanList;
    }

    private Span create(TracingSpanDto dto) {
        Span span = new Span();
        span.setSpanId(dto.getSpanId());
        span.setParentId(dto.getParentId());
        span.setTraceId(dto.getTraceId());
        span.setName(dto.getName());
        span.setServiceName(findServiceName(dto));
        return span;
    }

    @Override
    public List<Annotation> findAnnotationBySpanId(String spanId) {
        List<Annotation> annotationList = new LinkedList<Annotation>();
        List<TracingAnnotationDto> annotationDtoList = annotationDao.findBySpanId(spanId);
        for (TracingAnnotationDto dto : annotationDtoList) {
            annotationList.add(create(dto));
        }
        return annotationList;
    }

    @Override
    public List<Annotation> findAnnotationByTraceId(String traceId) {
        List<Annotation> annotationList = new LinkedList<Annotation>();
        List<TracingAnnotationDto> annotationDtoList = annotationDao.findByTraceId(traceId);
        for (TracingAnnotationDto dto : annotationDtoList) {
            annotationList.add(create(dto));
        }
        return annotationList;
    }

    private Annotation create(TracingAnnotationDto dto) {
        Annotation annotation = new Annotation();
        annotation.setKey(dto.getKey());
        annotation.setValue(dto.getValue());
        annotation.setIp(dto.getIp());
        annotation.setPort(dto.getPort());
        annotation.setTime(dto.getTime());
        annotation.setDuration(dto.getDuration());
        annotation.setSpanId(dto.getSpanId());
        annotation.setTraceId(dto.getTraceId());
        annotation.setServiceName(findServiceName(dto));
        return annotation;
    }


    private String findServiceName(TracingTraceDto dto) {
        return contextHolder.findTracingServiceDtoById(dto.getServiceId()).getServiceName();
    }

    private String findServiceName(TracingSpanDto dto) {
        return contextHolder.findTracingServiceDtoById(dto.getServiceId()).getServiceName();
    }

    private String findServiceName(TracingAnnotationDto dto) {
        return contextHolder.findTracingServiceDtoById(dto.getServiceId()).getServiceName();
    }*/
}
