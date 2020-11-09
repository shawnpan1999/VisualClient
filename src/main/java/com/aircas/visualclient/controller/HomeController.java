package com.aircas.visualclient.controller;

import com.aircas.visualclient.model.ContainerModel;
import com.aircas.visualclient.model.ImageModel;
import com.aircas.visualclient.service.DockerService;
import com.spotify.docker.client.exceptions.DockerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private DockerService dockerService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})
    public String home(Model model) {
        try {
            List<ImageModel> imageModels = dockerService.getImagesAll();
            model.addAttribute("images", imageModels);
            List<ContainerModel> containerModels = dockerService.getContainersAll();
            model.addAttribute("containers", containerModels);
            return "index";
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
            return "index";
        }
    }

    @RequestMapping(path = {"/dashboard"}, method = {RequestMethod.GET})
    public String dashboard() {
        return "dashboard";
    }
}
