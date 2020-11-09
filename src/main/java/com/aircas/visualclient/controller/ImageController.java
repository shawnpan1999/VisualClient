package com.aircas.visualclient.controller;

import com.aircas.visualclient.model.ImageModel;
import com.aircas.visualclient.service.DockerService;
import com.aircas.visualclient.util.JsonUtil;
import com.spotify.docker.client.exceptions.DockerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private DockerService dockerService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(path = "/load", method = {RequestMethod.POST})
    public String load(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) {
        try {
            dockerService.loadImage(file, fileName);
            return JsonUtil.getJSONString(0, fileName + " 加载成功");
        } catch (DockerException | InterruptedException | IOException e) {
            logger.error("load image error.");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "load image error.");
        }
    }

    @RequestMapping(path = "/getList", method = {RequestMethod.POST, RequestMethod.GET})
    public String getList() {
        try {
            List<ImageModel> imageModels = dockerService.getImagesAll();
            HashMap<String, Object> viewMap = new HashMap<>();
            viewMap.put("imageList", imageModels);
            return JsonUtil.getJSONString(0, "ok", viewMap);
        } catch (DockerException | InterruptedException e) {
            logger.error("get image list error.");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "get image list error.");
        }
    }

    @RequestMapping(path = "/delete", method = {RequestMethod.POST})
    public String delete(@RequestParam("iid") String imageId) {
        try {
            dockerService.removeImage(imageId);
            return JsonUtil.getJSONString(0, imageId);
        } catch (DockerException | InterruptedException e) {
            logger.error("delete image error.");
            e.printStackTrace();
            return JsonUtil.getJSONString(1, "delete image error.");
        }
    }
}
