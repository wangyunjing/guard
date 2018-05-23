var vm = new Vue({
    el: ".index",
    data: {
        // 是否为集群模式
        cloud: true,
        // Guard实例
        guardInstanceId: "127.0.0.1:8080",
        // Master实例
        masterId: "127.0.0.1:8082",
        // guard集群实例
        guardCloudInstances: [
            {
                "applicationId": 1,
                "instanceId": "127.0.0.1:8080",
                "ip": "127.0.0.1",
                "port": 8080,
                "healthUrl": "/health",
                "weight": null,
                "startCommand": "java -jar guard-1.0.0.jar >>/dev/null &",
                "username": "root",
                "password": "root",
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            }, {
                "applicationId": 1,
                "instanceId": "127.0.0.1:8081",
                "ip": "127.0.0.1",
                "port": 8081,
                "healthUrl": "/health",
                "weight": null,
                "startCommand": "java -jar guard-1.0.0.jar >>/dev/null &",
                "username": "root",
                "password": "root",
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            }, {
                "applicationId": 1,
                "instanceId": "127.0.0.1:8082",
                "ip": "127.0.0.1",
                "port": 8082,
                "healthUrl": "/health",
                "weight": null,
                "startCommand": "java -jar guard-1.0.0.jar >>/dev/null &",
                "username": "root",
                "password": "root",
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            }
        ],
        // 所有应用
        applications: [
            {
                "applicationId": 1,
                "applicationName": "test1",
                "port": 8080,
                "healthUrl": null,
                "startCommand": null,
                "startInstanceNum": 2,
                "username": "root",
                "password": "root",
                "defendInstanceDuration": 30000,
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            },
            {
                "applicationId": 2,
                "applicationName": "test2",
                "port": 8080,
                "healthUrl": null,
                "startCommand": null,
                "startInstanceNum": 2,
                "username": "root",
                "password": "root",
                "defendInstanceDuration": 30000,
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            },
            {
                "applicationId": 3,
                "applicationName": "test3",
                "port": 8080,
                "healthUrl": null,
                "startCommand": null,
                "startInstanceNum": 2,
                "username": "root",
                "password": "root",
                "defendInstanceDuration": 30000,
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            },
            {
                "applicationId": 4,
                "applicationName": "test4",
                "port": 8080,
                "healthUrl": null,
                "startCommand": null,
                "startInstanceNum": 2,
                "username": "root",
                "password": "root",
                "defendInstanceDuration": 30000,
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            },
            {
                "applicationId": 5,
                "applicationName": "test5",
                "port": 8080,
                "healthUrl": null,
                "startCommand": null,
                "startInstanceNum": 2,
                "username": "root",
                "password": "root",
                "defendInstanceDuration": 30000,
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            }
        ],
        // 某一应用的所有实例
        instances: [
            {
                "applicationId": 1,
                "applicationName":"test1",
                "instanceId": "127.0.0.1:8080",
                "ip": "127.0.0.1",
                "port": 8080,
                "healthUrl": "/health",
                "weight": null,
                "startCommand": "java -jar guard-1.0.0.jar >>/dev/null &",
                "username": "root",
                "password": "root",
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            }, {
                "applicationId": 1,
                "applicationName":"test1",
                "instanceId": "127.0.0.1:8081",
                "ip": "127.0.0.1",
                "port": 8081,
                "healthUrl": "/health",
                "weight": null,
                "startCommand": "java -jar guard-1.0.0.jar >>/dev/null &",
                "username": "root",
                "password": "root",
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            }, {
                "applicationId": 1,
                "applicationName":"test1",
                "instanceId": "127.0.0.1:8082",
                "ip": "127.0.0.1",
                "port": 8082,
                "healthUrl": "/health",
                "weight": null,
                "startCommand": "java -jar guard-1.0.0.jar >>/dev/null &",
                "username": "root",
                "password": "root",
                "heartbeatRate": 30000,
                "initializeInstanceDuration": 40000,
                "selfProtectedDuration": 60000,
                "status": "是",
                "launchStatus": "是"
            }
        ],
        paxos: {
            "ip": null,
            "port": null,
            "id": "127.0.0.1:8080",
            "name": "wangyunjing",
            "person": [{"name": "www", "sex": "1"}, {"name": "www", "sex": "1"}]
        },
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
        // 初始化
        initPage: function () {
            // 判断是否为集群
        },

        // 刷新master
        refreshMaster: function () {

        },

        // 显示Paxos信息
        showPaxosInfo: function (ip, port) {
            this.paxos.ip = ip;
            this.paxos.port = port;
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


        // 刷新应用列表
        refreshApplications: function () {

        },

        // 显示应用信息
        showApplicationInfo: function (applicationId) {
            if (applicationId == this.showApplicationId) {
                this.showApplicationId = null;
                return;
            }

            this.showApplicationId = applicationId;
        },

        // 添加应用
        addApplication: function () {
            this.application={};
        },
        // 添加应用
        addApplicationAction: function () {

        },

        // 编辑应用信息
        editApplication: function (applicationId) {
            this.application={};
            for (var i = 0; i < this.applications.length; i++) {
                if (this.applications[i].applicationId == applicationId) {
                    for (var tmp in this.applications[i]) {
                        this.application[tmp] = this.applications[i][tmp];
                    }
                    return;
                }
            }
        },
        // 编辑应用信息
        editApplicationAction: function () {

        },

        // 关闭应用
        closeApplication: function (applicationId) {
            this.application={};
            for (var i = 0; i < this.applications.length; i++) {
                if (this.applications[i].applicationId == applicationId) {
                    for (var tmp in this.applications[i]) {
                        this.application[tmp] = this.applications[i][tmp];
                    }
                    return;
                }
            }
        },
        // 关闭应用
        closeApplicationAction: function () {

        },

        // 删除应用
        deleteApplication: function (applicationId) {
            this.application={};
            for (var i = 0; i < this.applications.length; i++) {
                if (this.applications[i].applicationId == applicationId) {
                    for (var tmp in this.applications[i]) {
                        this.application[tmp] = this.applications[i][tmp];
                    }
                    return;
                }
            }
        },
        // 删除应用
        deleteApplicationAction: function () {

        },


        // 刷新实例列表
        refreshInstances: function (applicationId) {
        },

        // 添加实例
        addInstance: function (applicationId) {
            this.instance={};
            for (var i = 0; i < this.applications.length; i++) {
                if (this.applications[i].applicationId == applicationId) {
                    for (var tmp in this.applications[i]) {
                        this.instance[tmp] = this.applications[i][tmp];
                    }
                    return;
                }
            }
        },
        // 添加实例
        addInstanceAction: function () {

        },

        // 编辑实例信息
        editInstance: function (instanceId) {
            this.instance={};
            for (var i = 0; i < this.instances.length; i++) {
                if (this.instances[i].instanceId == instanceId) {
                    for (var tmp in this.instances[i]) {
                        this.instance[tmp] = this.instances[i][tmp];
                    }
                    return;
                }
            }
        },
        // 编辑实例信息
        editInstanceAction: function () {

        },

        // 关闭实例
        closeInstance: function (instanceId) {
            this.instance={};
            for (var i = 0; i < this.instances.length; i++) {
                if (this.instances[i].instanceId == instanceId) {
                    for (var tmp in this.instances[i]) {
                        this.instance[tmp] = this.instances[i][tmp];
                    }
                    return;
                }
            }
        },
        // 关闭实例
        closeInstanceAction: function () {

        },

        // 删除实例
        deleteInstance: function (instanceId) {
            this.instance={};
            for (var i = 0; i < this.instances.length; i++) {
                if (this.instances[i].instanceId == instanceId) {
                    for (var tmp in this.instances[i]) {
                        this.instance[tmp] = this.instances[i][tmp];
                    }
                    return;
                }
            }
        },
        // 删除实例
        deleteInstanceAction: function () {

        },
    }
});