toastr.options = {
    closeButton: true,// 是否显示关闭按钮（提示框右上角关闭按钮）
    debug: false,
    progressBar: true, // 是否显示进度条（设置关闭的超时时间进度条）
    positionClass: "toast-top-center", // 消息框在页面显示的位置
    onclick: null,
    showDuration: "300",
    hideDuration: "200",
    timeOut: "1500",
    extendedTimeOut: "1000",
    showEasing: "swing",
    hideEasing: "linear",
    showMethod: "fadeIn",
    hideMethod: "fadeOut"
}
var vm = new Vue({
    el: ".index",
    data: {
        // 是否为集群模式
        cloud: true,
        // Guard实例
        guardInstanceId: "",
        // Master实例
        masterId: "-1",
        // guard集群实例
        guardCloudInstances: [],
        // 所有应用
        applications: [],
        // 某一应用的所有实例
        instances: [],
        paxos: {},
        showApplicationId: null,

        application: {},
        instance: {},
    },
    mounted: function () {
        this.$nextTick(function () {
            this.initPage();
        })
    },
    methods: {
        // 初始化 OK
        initPage: function () {
            this.getCloud();
            if (this.cloud == true) {
                this.refreshMaster();
            }
            this.refreshApplications();
        },

        // 获取是否为集群
        getCloud: function () {
            var vm = this;
            $.ajax({
                async: false,
                url: "/guard/is_cloud",
                success: function (data) {
                    vm.cloud = data;
                    toastr.success("获取是否为集群成功!");
                },
                error: function () {
                    toastr.error("获取是否为集群出错!");
                }
            });
        },

        // 刷新master OK
        refreshMaster: function () {
            // 刷新Master
            var vm = this;
            vm.guardInstanceId = "";
            vm.masterId = "-1";
            $.ajax({
                async: false,
                url: "/paxos/is_master",
                success: function (data) {
                    vm.guardInstanceId = data["instanceId"];
                    vm.masterId = data["master"];
                    if (vm.masterId == null) {
                        vm.masterId = "-1";
                    }
                    toastr.success("获取Master信息成功!");
                },
                error: function () {
                    toastr.error("获取Master信息出错!");
                }
            });
            vm.guardCloudInstances = [];
            // 获取所有集群实例 /cloud/instances/list
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                return;
            }
            $.ajax({
                url: "http://" + this.masterId + "/cloud/instances/list",
                success: function (data) {
                    vm.guardCloudInstances = data;
                    toastr.success("获取Guard集群实例列表成功!");
                },
                error: function () {
                    toastr.error("获取Guard集群实例列表出错!");
                }
            });
        },

        // 显示Paxos信息 OK
        showPaxosInfo: function (ip, port) {
            this.paxos = {};
            var vm = this;
            $.ajax({
                async: false,
                url: "http://" + ip + ":" + port + "/paxos/is_master",
                success: function (data) {
                    vm.paxos = data;
                    toastr.success("获取Paxos信息成功!");
                },
                error: function () {
                    toastr.error("获取Paxos信息出错!");
                }
            });
            $('#paxosInfo').html(this.syntaxHighLight(this.paxos));
        },
        syntaxHighLight: function (json) {
            if (typeof json != 'string') {
                json = JSON.stringify(json, undefined, 4);
            }
            json = json.replace(/&/g, '&').replace(/</g, '<').replace(/>/g, '>');
            return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
                var cls = 'number';
                if (/^"/.test(match)) {
                    if (/:$/.test(match)) {
                        cls = 'key';
                    } else {
                        cls = 'string';
                    }
                } else if (/true|false/.test(match)) {
                    cls = 'boolean';
                } else if (/null/.test(match)) {
                    cls = 'null';
                }
                return '<span class="' + cls + '">' + match + '</span>';
            });
        },

        // 关闭所有应用
        closeAllApplications: function () {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                return;
            }
            var vm = this;
            $.ajax({
                async: false,
                type: "PUT",
                url: vm.getUrlPrefix() + "/guard/closure",
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("关闭所有应用成功!");
                },
                error: function () {
                    toastr.error("关闭所有应用出错!");
                }
            });
            this.refreshApplications();
        },

        // 启动所有应用
        startAllApplications: function () {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                return;
            }
            var vm = this;
            $.ajax({
                async: false,
                type: "PUT",
                url: vm.getUrlPrefix() + "/guard/open",
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("启动所有应用成功!");
                },
                error: function () {
                    toastr.error("启动所有应用出错!");
                }
            });
            this.refreshApplications();
        },

        // 刷新配置
        refreshConfig: function (applicationId, instanceId) {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                return;
            }
            var vm = this;
            $.ajax({
                type: "POST",
                url: vm.getUrlPrefix() + "/guard/refresh",
                data: {
                    "applicationId": applicationId,
                    "instanceId": instanceId
                },
                success: function () {
                    toastr.success("刷新配置成功!");
                },
                error: function () {
                    toastr.error("刷新配置出错!");
                }
            });
        },
        
        // 刷新应用列表 OK
        refreshApplications: function () {
            this.applications = [];
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                return;
            }
            var vm = this;
            $.ajax({
                // url: "http://" + this.masterId + "/applications/list",
                url: vm.getUrlPrefix() + "/applications/list",
                success: function (data) {
                    vm.applications = data;
                    toastr.success("获取应用列表成功!");
                },
                error: function () {
                    toastr.error("获取应用列表出错!");
                }
            });
        },

        // 显示应用信息 OK
        showApplicationInfo: function (applicationId) {
            if (applicationId == this.showApplicationId) {
                this.showApplicationId = null;
                return;
            }
            this.showApplicationId = applicationId;
            this.refreshInstances(applicationId);
        },

        // 添加应用 OK
        addApplication: function () {
            this.application = {};
        },
        // 添加应用 OK
        addApplicationAction: function () {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                this.application = {};
                return;
            }
            var vm = this;
            $.ajax({
                async: false,
                type: "POST",
                url: vm.getUrlPrefix() + "/applications",
                data: JSON.stringify(vm.application),
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("添加应用成功!");
                },
                error: function () {
                    toastr.error("添加应用出错!");
                },
                complete: function () {
                    vm.application = {};
                }
            });
            this.refreshApplications();
        },

        // 编辑应用信息 OK
        editApplication: function (applicationId) {
            this.application = {};
            for (var i = 0; i < this.applications.length; i++) {
                if (this.applications[i].applicationId == applicationId) {
                    for (var tmp in this.applications[i]) {
                        this.application[tmp] = this.applications[i][tmp];
                    }
                    return;
                }
            }
        },
        // 编辑应用信息 OK
        editApplicationAction: function (applicationId) {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                this.application = {};
                return;
            }
            var vm = this;
            $.ajax({
                async: false,
                type: "PUT",
                // url: "http://" + vm.masterId + "/applications/" + applicationId,
                url: vm.getUrlPrefix() + "/applications/" + applicationId,
                data: JSON.stringify(vm.application),
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("编辑应用成功!");
                },
                error: function () {
                    toastr.error("编辑应用出错!");
                },
                complete: function () {
                    vm.application = {};
                }
            });
            this.refreshApplications();
        },

        // 关闭应用 OK
        closeApplication: function (applicationId) {
            this.application = {};
            for (var i = 0; i < this.applications.length; i++) {
                if (this.applications[i].applicationId == applicationId) {
                    for (var tmp in this.applications[i]) {
                        this.application[tmp] = this.applications[i][tmp];
                    }
                    return;
                }
            }
        },
        // 关闭应用 OK
        closeApplicationAction: function (applicationId) {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                this.application = {};
                return;
            }
            var vm = this;
            $.ajax({
                async: false,
                type: "PUT",
                // url: "http://" + vm.masterId + "/applications/" + applicationId + "/closure",
                url: vm.getUrlPrefix() + "/applications/" + applicationId + "/closure",
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("关闭应用成功!");
                },
                error: function () {
                    toastr.error("关闭应用出错!");
                },
                complete: function () {
                    vm.application = {};
                }
            });
            this.refreshApplications();
        },

        // 启动应用 OK
        startApplication: function (applicationId) {
            this.application = {};
            for (var i = 0; i < this.applications.length; i++) {
                if (this.applications[i].applicationId == applicationId) {
                    for (var tmp in this.applications[i]) {
                        this.application[tmp] = this.applications[i][tmp];
                    }
                    return;
                }
            }
        },
        // 启动应用 OK
        startApplicationAction: function (applicationId) {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                this.application = {};
                return;
            }
            var vm = this;
            $.ajax({
                async: false,
                type: "PUT",
                // url: "http://" + vm.masterId + "/applications/" + applicationId + "/open",
                url: vm.getUrlPrefix() + "/applications/" + applicationId + "/open",
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("启动应用成功!");
                },
                error: function () {
                    toastr.error("启动应用出错!");
                },
                complete: function () {
                    vm.application = {};
                }
            });
            this.refreshApplications();
        },

        // 删除应用 OK
        deleteApplication: function (applicationId) {
            this.application = {};
            for (var i = 0; i < this.applications.length; i++) {
                if (this.applications[i].applicationId == applicationId) {
                    for (var tmp in this.applications[i]) {
                        this.application[tmp] = this.applications[i][tmp];
                    }
                    return;
                }
            }
        },
        // 删除应用 OK
        deleteApplicationAction: function (applicationId) {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                this.application = {};
                return;
            }
            var vm = this;
            $.ajax({
                async: false,
                type: "DELETE",
                // url: "http://" + vm.masterId + "/applications/" + applicationId,
                url: vm.getUrlPrefix() + "/applications/" + applicationId,
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("删除应用成功!");
                },
                error: function () {
                    toastr.error("删除应用出错!");
                },
                complete: function () {
                    vm.application = {};
                }
            });
            this.refreshApplications();
        },


        // 刷新实例列表 OK
        refreshInstances: function (applicationId) {
            this.instances = [];
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                return;
            }
            var vm = this;
            $.ajax({
                // url: "http://" + this.masterId + "/instances/list",
                url: vm.getUrlPrefix() + "/instances/list",
                data: {
                    "applicationId": applicationId
                },
                success: function (data) {
                    vm.instances = data;
                    toastr.success("刷新实例列表成功!");
                },
                error: function () {
                    toastr.error("刷新实例列表出错!");
                }
            });
        },

        // 添加实例 OK
        addInstance: function (applicationId) {
            this.instance = {};
            for (var i = 0; i < this.applications.length; i++) {
                if (this.applications[i].applicationId == applicationId) {
                    for (var tmp in this.applications[i]) {
                        this.instance[tmp] = this.applications[i][tmp];
                    }
                    return;
                }
            }
        },
        // 添加实例 OK
        addInstanceAction: function (applicationId) {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                this.instance = {};
                return;
            }
            var vm = this;
            $.ajax({
                async: false,
                type: "POST",
                // url: "http://" + vm.masterId + "/applications/" + applicationId + "/instances",
                url: vm.getUrlPrefix() + "/applications/" + applicationId + "/instances",
                data: JSON.stringify(vm.instance),
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("添加实例成功!");
                },
                error: function () {
                    toastr.error("添加实例出错!");
                },
                complete: function () {
                    vm.instance = {};
                }
            });
            this.refreshInstances(applicationId);
        },

        // 编辑实例信息 OK
        editInstance: function (instanceId) {
            this.instance = {};
            for (var i = 0; i < this.instances.length; i++) {
                if (this.instances[i].instanceId == instanceId) {
                    for (var tmp in this.instances[i]) {
                        this.instance[tmp] = this.instances[i][tmp];
                    }
                    return;
                }
            }
        },
        // 编辑实例信息 OK
        editInstanceAction: function (instanceId) {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                this.instance = {};
                return;
            }
            var vm = this;
            var applicationId = vm.instance.applicationId;
            $.ajax({
                async: false,
                type: "PUT",
                // url: "http://" + vm.masterId + "/applications/" + applicationId + "/instances?instanceId=" + instanceId,
                url: vm.getUrlPrefix() + "/applications/" + applicationId + "/instances?instanceId=" + instanceId,
                data: JSON.stringify(vm.instance),
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("编辑实例成功!");
                },
                error: function () {
                    toastr.error("编辑实例出错!");
                },
                complete: function () {
                    vm.instance = {};
                }
            });
            this.refreshInstances(applicationId);
        },

        // 关闭实例 OK
        closeInstance: function (instanceId) {
            this.instance = {};
            for (var i = 0; i < this.instances.length; i++) {
                if (this.instances[i].instanceId == instanceId) {
                    for (var tmp in this.instances[i]) {
                        this.instance[tmp] = this.instances[i][tmp];
                    }
                    return;
                }
            }
        },
        // 关闭实例 OK
        closeInstanceAction: function (instanceId) {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                this.instance = {};
                return;
            }
            var vm = this;
            var applicationId = vm.instance.applicationId;
            $.ajax({
                async: false,
                type: "PUT",
                // url: "http://" + vm.masterId + "/applications/" + applicationId + "/instances/closure?instanceId=" + instanceId,
                url: vm.getUrlPrefix() + "/applications/" + applicationId + "/instances/closure?instanceId=" + instanceId,
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("关闭实例成功!");
                },
                error: function () {
                    toastr.error("关闭实例出错!");
                },
                complete: function () {
                    vm.instance = {};
                }
            });
            this.refreshInstances(applicationId);
        },

        // 启动实例 OK
        startInstance: function (instanceId) {
            this.instance = {};
            for (var i = 0; i < this.instances.length; i++) {
                if (this.instances[i].instanceId == instanceId) {
                    for (var tmp in this.instances[i]) {
                        this.instance[tmp] = this.instances[i][tmp];
                    }
                    return;
                }
            }
        },
        // 启动实例 OK
        startInstanceAction: function (instanceId) {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                this.instance = {};
                return;
            }
            var vm = this;
            var applicationId = vm.instance.applicationId;
            $.ajax({
                async: false,
                type: "PUT",
                // url: "http://" + vm.masterId + "/applications/" + applicationId + "/instances/open?instanceId=" + instanceId,
                url: vm.getUrlPrefix() + "/applications/" + applicationId + "/instances/open?instanceId=" + instanceId,
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("启动实例成功!");
                },
                error: function () {
                    toastr.error("启动实例出错!");
                },
                complete: function () {
                    vm.instance = {};
                }
            });
            this.refreshInstances(applicationId);
        },

        // 删除实例 OK
        deleteInstance: function (instanceId) {
            this.instance = {};
            for (var i = 0; i < this.instances.length; i++) {
                if (this.instances[i].instanceId == instanceId) {
                    for (var tmp in this.instances[i]) {
                        this.instance[tmp] = this.instances[i][tmp];
                    }
                    return;
                }
            }
        },
        // 删除实例 OK
        deleteInstanceAction: function (instanceId) {
            if (this.cloud == true && this.masterId == "-1") {
                toastr.warning("Master节点不存在!");
                this.instance = {};
                return;
            }
            var vm = this;
            var applicationId = vm.instance.applicationId;
            $.ajax({
                async: false,
                type: "DELETE",
                // url: "http://" + vm.masterId + "/applications/" + applicationId + "/instances?instanceId=" + instanceId,
                url: vm.getUrlPrefix() + "/applications/" + applicationId + "/instances?instanceId=" + instanceId,
                contentType: "application/json;charset=UTF-8",
                success: function () {
                    toastr.success("启动实例成功!");
                },
                error: function () {
                    toastr.error("启动实例出错!");
                },
                complete: function () {
                    vm.instance = {};
                }
            });
            this.refreshInstances(applicationId);
        },

        getUrlPrefix: function () {
            if (this.masterId == "-1") {
                return "";
            }
            return "http://" + this.masterId;
        }
    }
});