package com.aircas.visualclient.model;

import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.messages.Container;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ContainerModel {
    private String id;
    private String image;
    private String command;
    private String created;
    private String state;
    private String status;
    private String ports;
    private String names;

    private String realId;
    public ContainerModel() {
    }



    public String toPortString(Container.PortMapping portMapping) {
        StringBuilder res = new StringBuilder();
        res.append(portMapping.ip()).append(":").append(portMapping.privatePort())
                .append("->").append(portMapping.publicPort()).append("/").append(portMapping.type());
        return res.toString();
    }

    public String toImageString(String image) {
        String[] splits = image.split(":");
        if (splits[0].equals("sha256")) {
            return splits[1].substring(0, 12);
        }
        splits = image.split("@");
        if (splits.length > 1) {
            return splits[0];
        }
        return image;
    }

    public ContainerModel(Container container) {
        this.setId(container.id().substring(0, 12));
        this.setImage(toImageString(container.image()));
        this.setCommand(container.command());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.setCreated(formatter.format(new Date(container.created() * 1000)));
        this.setState(container.state());
        this.setStatus(container.status());

        ImmutableList<Container.PortMapping> portMappings = container.ports();
        try {
            if (portMappings.size() == 0) {
                this.setPorts("");
            } else {
                StringBuilder str = new StringBuilder();
                for (Container.PortMapping portMapping : portMappings) {
                    str.append(", ").append(toPortString(portMapping));
                }
                this.setPorts(str.substring(2));
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            this.setPorts("");
        }

        ImmutableList<String> names = container.names();
        try {
            if (names.size() == 0) {
                this.setNames("");
            } else {
                StringBuilder str = new StringBuilder();
                for (String name : names) {
                    str.append(", ").append(name.substring(1));
                }
                this.setNames(str.substring(2));
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            this.setNames("");
        }
        this.setRealId(container.id());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports = ports;
    }

    public String getRealId() {
        return realId;
    }

    public void setRealId(String realId) {
        this.realId = realId;
    }
}
