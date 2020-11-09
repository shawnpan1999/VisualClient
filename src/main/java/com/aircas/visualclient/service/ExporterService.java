package com.aircas.visualclient.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExporterService {
    HashMap<String, Double> resultMap = new HashMap<>();

    public void putInfoMapKeys(HashMap<String, Double> map) {
        //CPU
        //    CPU使用率：100 - (avg by (instance) (irate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)
        map.put("node_cpu_seconds_total", 0.0);

        //内存
        //    内存使用率：(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes ))* 100
        //    可用内存 M：node_memory_MemAvailable_bytes / 1024 / 1024
        map.put("node_memory_MemTotal_bytes", 0.0);
        map.put("node_memory_MemAvailable_bytes", 0.0);
        map.put("node_memory_MemFree_bytes", 0.0);
        map.put("node_memory_Buffers_bytes", 0.0);
        map.put("node_memory_Cached_bytes", 0.0);
        map.put("node_memory_SReclaimable_bytes", 0.0);

        //磁盘
        //    磁盘总大小 G：node_filesystem_size_bytes {fstype=~"ext4|xfs"} / 1024 / 1024 / 1024
        //    磁盘剩余大小 G：node_filesystem_avail_bytes {fstype=~"ext4|xfs"}  / 1024 / 1024 / 1024
        //    磁盘使用率 G：(1-(node_filesystem_free_bytes{fstype=~"ext4|xfs"} / node_filesystem_size_bytes{fstype=~"ext4|xfs"})) * 100
        map.put("node_filesystem_size_bytes", 0.0);
        map.put("node_filesystem_avail_bytes", 0.0);
        map.put("node_filesystem_free_bytes", 0.0);
    }

    public HashMap<String, Double> analysis(HashMap<String, Double> infoMap) {
        //CPU使用率(%)
        double nowTime = (double)new Date().getTime();
        Double new_CPU_utilization = 100 - ((infoMap.get("node_cpu_seconds_total") - resultMap.get("node_cpu_seconds_total")) / ((nowTime - resultMap.get("last_time")) / 100000));
        if (new_CPU_utilization >=0) resultMap.put("CPU_utilization", new_CPU_utilization);
        //CPU时间(s)
        resultMap.put("node_cpu_seconds_total", infoMap.get("node_cpu_seconds_total"));
        //系统时间(ms)
        resultMap.put("last_time", nowTime);
        //内存使用率(%)
        resultMap.put("memory_utilization", 100 * (1 -
                (infoMap.get("node_memory_MemAvailable_bytes") / infoMap.get("node_memory_MemTotal_bytes"))));
        //可用内存(M)
        resultMap.put("memory_available", infoMap.get("node_memory_MemAvailable_bytes") / 1024 / 1024);
        //磁盘总大小(G)
        resultMap.put("filesystem_total_size", infoMap.get("node_filesystem_size_bytes") / 1024 / 1024 / 1024);
        //磁盘剩余大小(G)
        resultMap.put("filesystem_avail_size", infoMap.get("node_filesystem_avail_bytes") / 1024 / 1024 / 1024);
        //磁盘使用率(%)
        resultMap.put("filesystem_utilization", (1 - (infoMap.get("node_filesystem_free_bytes") / infoMap.get("node_filesystem_size_bytes"))) * 100);
        return resultMap;
    }

    public HashMap<String, Double> getExporterInfo(String targetUrl) throws IOException {
        HashMap<String, Double> infoMap = new HashMap<>();
        putInfoMapKeys(infoMap);
        InputStreamReader isr;
        BufferedReader br;

        URL url = new URL(targetUrl);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setAllowUserInteraction(false);
        isr = new InputStreamReader(url.openStream());
        br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null)
        {
            if (line.charAt(0) == '#') continue;    //忽略 # 注释
            for (Map.Entry<String, Double> entry : infoMap.entrySet()) {
                if (line.startsWith(entry.getKey())) {
                    if (entry.getKey().startsWith("node_filesystem")
                            && !line.contains("fstype=\"ext4\"")
                            && !line.contains("fstype=\"xfs\"")) {
                        continue;
                    }
                    if (entry.getKey().startsWith("node_cpu_seconds_total")
                            && !line.contains("mode=\"idle\"")) {
                        continue;
                    }
                    infoMap.put(entry.getKey(), new BigDecimal(line.split(" ")[1]).doubleValue());
                    break;
                }
            }
        }
        return analysis(infoMap);
    }

    public ExporterService() {
        resultMap.put("last_time", (double)new Date().getTime());
        resultMap.put("node_cpu_seconds_total", 0.0);
    }
}
