package com.aircas.visualclient.controller;

import com.aircas.visualclient.model.ContainerModel;
import com.aircas.visualclient.service.DockerService;
import com.aircas.visualclient.util.JsonUtil;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerCreation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/container")
public class ContainerController {
    @Autowired
    private DockerService dockerService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(path = "/start", method = {RequestMethod.POST})
    public String start(@RequestParam("cid") String containerId) {
        try {
            dockerService.startContainer(containerId);
            return getContainerJsonString(containerId);
        } catch (DockerException | InterruptedException e) {
            logger.error("start container error.");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "start container error.");
        }
    }

    @RequestMapping(path = "/restart", method = {RequestMethod.POST})
    public String restart(@RequestParam("cid") String containerId) {
        try {
            dockerService.restartContainer(containerId);
            return getContainerJsonString(containerId);
        } catch (DockerException | InterruptedException e) {
            logger.error("restart container error.");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "restart container error.");
        }
    }

    @RequestMapping(path = "/stop", method = {RequestMethod.POST})
    public String stop(@RequestParam("cid") String containerId) {
        try {
            dockerService.stopContainer(containerId);
            return getContainerJsonString(containerId);
        } catch (DockerException | InterruptedException e) {
            logger.error("stop container error.");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "stop container error.");
        }
    }

    @RequestMapping(path = "/delete", method = {RequestMethod.POST})
    public String delete(@RequestParam("cid") String containerId) {
        try {
            dockerService.removeContainer(containerId);
            return JsonUtil.getJSONString(0, containerId);
        } catch (DockerException | InterruptedException e) {
            logger.error("delete container error.");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "delete container error.");
        }
    }

    @RequestMapping(path = "/create", method = {RequestMethod.POST})
    public String create(@RequestParam("repoTag") String repoTag, @RequestParam(value = "name", required = false) String name) {
        try {
            ContainerCreation creation;
            if (name != null && name.trim().length() > 0) {
                creation = dockerService.createContainer(repoTag, name);
            } else {
                creation = dockerService.createContainer(repoTag);
            }
            HashMap<String, Object> viewMap = new HashMap<>();
            viewMap.put("id", creation.id());
            viewMap.put("warnings", creation.warnings());
            return JsonUtil.getJSONString(0, "ok", viewMap);
        } catch (Exception e) {
            logger.error("create container error.");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "create container error.");
        }
    }

    public String getContainerJsonString(String containerId) {
        try {
            ContainerModel containerModel;
            containerModel = dockerService.getContainerById(containerId);
            if (containerModel == null) {
                return JsonUtil.getJSONString(1, "can't get container info " + containerId + ".");
            }
            HashMap<String, Object> viewMap = new HashMap<>();
            viewMap.put("container", containerModel);
            return JsonUtil.getJSONString(0, "ok", viewMap);
        } catch (DockerException | InterruptedException e) {
            logger.error("can't get container info " + containerId + ".");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "can't container info " + containerId + ".");
        }
    }

    @RequestMapping(path = "/getList", method = {RequestMethod.POST, RequestMethod.GET})
    public String getList() {
        try {
            List<ContainerModel> containerModels = dockerService.getContainersAll();
            HashMap<String, Object> viewMap = new HashMap<>();
            viewMap.put("containerList", containerModels);
            return JsonUtil.getJSONString(0, "ok", viewMap);
        } catch (DockerException | InterruptedException e) {
            logger.error("get container list error.");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "get container list error.");
        }
    }
}
