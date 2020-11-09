package com.aircas.visualclient.controller;

import com.aircas.visualclient.service.ExporterService;
import com.aircas.visualclient.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ExporterService exporterService;

    @RequestMapping(path = "/getInfoList", method = {RequestMethod.POST})
    public String getInfoList(@RequestParam("targetUrl") String targetUrl) {
        try {
            HashMap<String, Double> resultMap = exporterService.getExporterInfo(targetUrl);
            HashMap<String, String> viewMap = new HashMap<>();
            for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
                viewMap.put(entry.getKey(), String.format("%.2f", entry.getValue()));
            }
            return JsonUtil.getJSONString(0, "ok", viewMap);
        } catch (IOException e) {
            logger.error("get info list error.");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "get info list error.");
        }
    }
}
