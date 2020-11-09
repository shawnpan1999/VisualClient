package com.aircas.visualclient.service;

import com.aircas.visualclient.model.ContainerModel;
import com.aircas.visualclient.model.ImageModel;
import com.google.common.collect.ImmutableSet;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class DockerService {
    @Value("${docker.connect.url}")
    private String dockerConnectUrl;

    @Value("${container.stop.timelimit}")
    private String containerStopTimelimit;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DockerClient connect() {
        return DefaultDockerClient.builder().uri(URI.create(dockerConnectUrl)).build();
    }

    public List<ImageModel> getImagesAll() throws DockerException, InterruptedException {
        DockerClient docker = connect();
        List<ImageModel> imageModels = new ArrayList<>();
        List<Image> images = docker.listImages();
        for (Image image : images) {
            imageModels.add(new ImageModel(image));
        }
        return imageModels;
    }

    public List<ContainerModel> getContainersAll() throws DockerException, InterruptedException {
        DockerClient docker = connect();
        List<ContainerModel> containerModels = new ArrayList<>();
        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());
        for (Container container : containers) {
            containerModels.add(new ContainerModel(container));
        }
        return containerModels;
    }

    public void loadImage(MultipartFile file, String imageName) throws DockerException, InterruptedException, IOException {
        DockerClient docker = connect();
        InputStream imagePayload = new BufferedInputStream(file.getInputStream());
        docker.create(imageName, imagePayload);
    }

    public ContainerCreation createContainer(String repoTag, String name) throws DockerException, InterruptedException {
        DockerClient docker = connect();
        ContainerConfig config = ContainerConfig.builder()
                .image(repoTag)
                .build();
        return docker.createContainer(config, name);
    }

    public ContainerCreation createContainer(String repoTag) throws DockerException, InterruptedException {
        DockerClient docker = connect();
        ContainerConfig config = ContainerConfig.builder()
                .image(repoTag)
                .build();
        ContainerCreation creation = docker.createContainer(config);
        return creation;
    }

    public ContainerModel getContainerById(String containerId) throws DockerException, InterruptedException {
        DockerClient docker = connect();
        ContainerModel containerModel;
        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());
        for (Container container : containers) {
            if (container.id().equals(containerId)) {
                containerModel = new ContainerModel(container);
                return containerModel;
            }
        }
        return null;
    }
    public void removeContainer(String containerId) throws DockerException, InterruptedException {
        DockerClient docker = connect();
        if (docker.inspectContainer(containerId).state().running()) {
            docker.stopContainer(containerId, Integer.parseInt(containerStopTimelimit));
        }
        docker.removeContainer(containerId);
    }

    public void removeImage(String imageId) throws DockerException, InterruptedException {
        DockerClient docker = connect();
        docker.removeImage(imageId);
    }

    public void restartContainer(String containerId) throws DockerException, InterruptedException {
        DockerClient docker = connect();
        docker.restartContainer(containerId);
    }

    public void stopContainer(String containerId) throws DockerException, InterruptedException {
        DockerClient docker = connect();
        docker.stopContainer(containerId, Integer.parseInt(containerStopTimelimit));
    }

    public void startContainer(String containerId) throws DockerException, InterruptedException {
        DockerClient docker = connect();
        docker.startContainer(containerId);
    }

}
