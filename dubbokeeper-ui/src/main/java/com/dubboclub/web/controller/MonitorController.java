package com.dubboclub.web.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dubboclub.monitor.model.Statistics;
import com.dubboclub.monitor.service.MonitorService;

@Controller
@RequestMapping("/monitor")
public class MonitorController {

	MonitorService monitorService = new MonitorService();

	@RequestMapping("/{service}/monitors.htm")
	public @ResponseBody List<Statistics> listElapsedByService(@PathVariable String service,
			@RequestParam(value = "lastTimestamp", required = false, defaultValue = "0") long lastTimestamp) {
		return monitorService.listElapsedByService(service, lastTimestamp);
	}
}
