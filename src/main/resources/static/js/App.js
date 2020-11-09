function containerRestart(cid, bid) {
    disableContainerButtons(bid);
    bs4pop.notice("正在重启容器..", {type: "primary"});
    $.ajax({
        type : "post",
        url : "/container/restart",
        dataType: "json",
        data : {
            cid: cid,
        },
        success : function(result){
            if (result.code == 0) {
                setContainerLineByBid(document.getElementById("containerLine"+bid), result.data.container, bid);
                bs4pop.notice("重启成功", {type: "success"});
            } else {
                bs4pop.notice(result.msg, {type: "danger"});
            }
        },
        error : function(result){
            bs4pop.notice("未知错误", {type: "danger"});
        }
    });
}
function containerStop(cid, bid) {
    disableContainerButtons(bid);
    bs4pop.notice("正在停止容器..", {type: "primary"});
    $.ajax({
        type : "post",
        url : "/container/stop",
        dataType: "json",
        data : {
            cid: cid,
        },
        success : function(result){
            if (result.code == 0) {
                setContainerLineByBid(document.getElementById("containerLine"+bid), result.data.container, bid);
                bs4pop.notice("停止成功", {type: "success"});
            } else {
                bs4pop.notice(result.msg, {type: "danger"});
            }
        },
        error : function(result){
            bs4pop.notice("未知错误", {type: "danger"});
        }
    });
}

function containerStart(cid, bid) {
    disableContainerButtons(bid);
    bs4pop.notice("正在启动容器..", {type: "primary"});
    $.ajax({
        type : "post",
        url : "/container/start",
        dataType: "json",
        data : {
            cid: cid,
        },
        success : function(result){
            if (result.code == 0) {
                setContainerLineByBid(document.getElementById("containerLine"+bid), result.data.container, bid);
                bs4pop.notice("启动成功", {type: "success"});
            } else {
                bs4pop.notice(result.msg, {type: "danger"});
            }
        },
        error : function(result){
            bs4pop.notice("未知错误", {type: "danger"});
        }
    });
}

function containerDelete(cid, bid) {
    disableContainerButtons(bid);
    bs4pop.notice("正在删除容器.", {type: "primary"});
    $.ajax({
        type : "post",
        url : "/container/delete",
        dataType: "json",
        data : {
            cid: cid,
        },
        success : function(result){
            if (result.code == 0) {
                var t = document.getElementById("containerLine"+bid);
                t.remove();
                bs4pop.notice("删除成功", {type: "success"});
            } else {
                bs4pop.notice(result.msg, {type: "danger"});
            }
        },
        error : function(result){
            bs4pop.notice("未知错误", {type: "danger"});
        }
    });
}

function imageDelete(iid, bid) {
    disableImageButtons(bid);
    bs4pop.notice("正在删除镜像.", {type: "primary"});
    $.ajax({
        type : "post",
        url : "/image/delete",
        dataType: "json",
        data : {
            iid: iid,
        },
        success : function(result){
            if (result.code == 0) {
                var t = document.getElementById("imageLine"+bid);
                t.remove();
                bs4pop.notice("删除成功", {type: "success"});
            } else {
                bs4pop.notice(result.msg, {type: "danger"});
            }
        },
        error : function(result){
            bs4pop.notice("未知错误", {type: "danger"});
        }
    });
}


function containerCreate() {
    var repoTag = document.getElementById("containerCreateRepoTag").value;
    var name = document.getElementById("containerCreateName").value;
    bs4pop.notice("正在创建容器..", {type: "primary"});
    $.ajax({
        type : "post",
        url : "/container/create",
        dataType: "json",
        data : {
            repoTag: repoTag,
            name: name,
        },
        success : function(result){
            if (result.code == 0) {
                bs4pop.notice("创建成功, ID: " + result.data.id, {type: "success"});
                refreshContainers(Boolean(false));
                for (var x in result.data.warnings) {
                    bs4pop.notice("WARNRING: " + result.data.id, {type: "warning"});
                }
            } else {
                bs4pop.notice(result.msg, {type: "danger"});
            }
        },
        error : function(result){
            bs4pop.notice("未知错误", {type: "danger"});
        }
    });
}

function refreshContainers(needTips) {
    $.ajax({
        type : "get",
        url : "/container/getList",
        dataType: "json",
        success : function(result){
            if (result.code == 0) {
                if (needTips) {
                    bs4pop.notice("拉取容器列表成功.", {type: "success"});
                }
                var containers = result.data.containerList;
                document.getElementById("containerTableBody").innerHTML="";
                var tbody = document.getElementById("containerTableBody");
                for (var x in containers) {
                    var tr = document.createElement("tr");
                    tbody.appendChild(tr);
                    tr.id = "containerLine" + x;
                    setContainerLineByBid(tr, containers[x], x);
                }
            } else {
                bs4pop.notice("拉取容器列表失败", {type: "danger"});
            }
        },
        error : function(result){
            bs4pop.notice("未知错误", {type: "danger"});
        }
    });
}

function setContainerLineByBid(containerLine, container, bid) {
    containerLine.innerHTML =
        "<td><div>"                        + container.id      + "</div> </td>\n" +
        "<td><div class=\"longContent\"> " + container.image   + "</div> </td>\n" +
        "<td><div class=\"longContent\"> " + container.command + "</div> </td>\n" +
        "<td><div class=\"longContent\"> " + container.created + "</div> </td>\n" +
        "<td><div>"                        + container.state   + "</div> </td>\n" +
        "<td><div class=\"longContent\"> " + container.status  + "</div> </td>\n" +
        "<td><div>"                        + container.ports   + "</div> </td>\n" +
        "<td><div class=\"longContent\"> " + container.names   + "</div> </td>\n" +
        "<td id=\"buttonset" + bid + "\"" + "</td>\n";
    changeButtonsByState(document.getElementById("buttonset" + bid), container.realId, bid, container.state);
}

function changeButtonsByState(buttonSet, cid, bid, state) {
    if (state == "running") {
        buttonSet.innerHTML = "<div>\n" +
            "<button type=\"button\" class=\"btn btn-outline-info btn-sm\" id=\"restart"+bid+"\"" +
            "    onclick=\"containerRestart('"+cid+"','"+bid+"')\">重启</button>\n" +
            "<button type=\"button\" class=\"btn btn-outline-warning btn-sm\" id=\"stop"+bid+"\"" +
            "    onclick=\"containerStop('"+cid+"','"+bid+"')\">停止</button>\n" +
            "<button type=\"button\" class=\"btn btn-outline-danger btn-sm\" id=\"delete"+bid+"\"" +
            "    onclick=\"containerDelete('"+cid+"','"+bid+"')\">删除</button>\n" +
            "</div>"
    }
    if (state == "exited") {
        buttonSet.innerHTML = "<div>\n" +
            "<button type=\"button\" class=\"btn btn-outline-info btn-sm\" id=\"restart"+bid+"\"" +
            "    onclick=\"containerRestart('"+cid+"','"+bid+"')\">重启</button>\n" +
            "<button type=\"button\" class=\"btn btn-outline-warning btn-sm\" id=\"stop"+bid+"\" disabled=\"disabled\"\n" +
            "    onclick=\"containerStop('"+cid+"','"+bid+"')\">停止</button>\n" +
            "<button type=\"button\" class=\"btn btn-outline-danger btn-sm\" id=\"delete"+bid+"\"" +
            "    onclick=\"containerDelete('"+cid+"','"+bid+"')\">删除</button>\n" +
            "</div>"
    }
    if (state == "created") {
        buttonSet.innerHTML = "<div>\n" +
            "<button type=\"button\" class=\"btn btn-outline-success btn-sm\" id=\"start"+bid+"\"" +
            "    onclick=\"containerStart('"+cid+"','"+bid+"')\">启动</button>\n" +
            "<button type=\"button\" class=\"btn btn-outline-danger btn-sm\" id=\"delete"+bid+"\"" +
            "    onclick=\"containerDelete('"+cid+"','"+bid+"')\">删除</button>\n" +
            "</div>"
    }
}

function modelFill(repo, tag) {
    document.getElementById("containerCreateRepoTag").setAttribute("value", repo + ":" + tag);
}

function disableImageButtons(bid) {
    let createButton = document.getElementById("create" + bid);
    let deleteImageButton = document.getElementById("deleteImage" + bid);
    if (createButton != null) {
        createButton.disabled=true;
    }
    if (deleteImageButton != null) {
        deleteImageButton.disabled=true;
    }

}

function disableContainerButtons(bid) {
    let startButton = document.getElementById("start" + bid);
    let restartButton = document.getElementById("restart" + bid);
    let stopButton = document.getElementById("stop" + bid);
    let deleteButton = document.getElementById("delete" + bid);
    if (startButton != null) {
        startButton.disabled=true;
    }
    if (restartButton != null) {
        restartButton.disabled=true;
    }
    if (stopButton != null) {
        stopButton.disabled=true;
    }
    if (deleteButton != null) {
        deleteButton.disabled=true;
    }
}

function loadImage() {
    var input = $("input[name='file']");
    if (input.val() === "") {
        alert("文件为空! ");
        return false;
    }
    var size = input[0].files[0].size;
    if (size > 209715200) {
        alert("上传文件不能超过200MB!");
        return false;
    }
    bs4pop.notice("正在上传..", {type: "primary"});
    var button = document.getElementById("loadImage");
    button.disabled = true;
    button.innerText = "上传中...";
    var formData = new FormData();//这里需要实例化一个FormData来进行文件上传
    formData.append("file", input[0].files[0]);
    var strArray = input[0].files[0].name.split("\.");
    var fileName = "";
    for (var x in strArray) {
        if (x < strArray.length - 1) {
            fileName = fileName + strArray[x];
        }
    }
    formData.append("fileName", fileName);
    $.ajax({
        type : "post",
        url : "/image/load",
        data : formData,
        processData : false,
        contentType : false,
        dataType: "json",
        success : function(result){
            if (result.code == 0) {
                bs4pop.notice(result.msg, {type: "success"});
            } else {
                bs4pop.notice(result.msg, {type: "danger"});
            }
            button.disabled = false;
            button.innerText = "上传";
            location.reload();
        },
        error : function(result){
            bs4pop.notice("未知错误", {type: "danger"});
            button.disabled = false;
            button.innerText = "上传";
        }
    });
}