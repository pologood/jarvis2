const CONST = {
    MSG_CODE: { //消息Code
        SUCCESS: 1000,
        FAILED: 1001
    },
    OPERATE_MODE: { //操作模式
        ADD: 1,
        EDIT: 2,
        DELETE: 3
    },

    JOB_TYPE: { //job类型
        DUMMY: 'dummy',
        MAPREDUCE: 'mapreduce',
        HIVE: 'hive',
        JAVA: 'java',
        SHELL: 'shell',
        SPARK_LAUNCHER: 'sparkLauncher'
    },
    CONTENT_TYPE: { //content类型
        TEXT: 1,
        SCRIPT: 2,
        JAR: 3,
        EMPTY: 0
    },
    JOB_ACTIVE_DATE: {  //任务有效日期
        MIN_DATE: 0,
        MAX_DATE: 253402214400000
    },
    JOB_PARAMS_KEY: {   //任务参数
        JAR_URL: '_jarvis_jar_url'
    },
    SPARK_LAUNCHER_JOB: {        //sparkLauncher任务
        COMMAND: "",
        PARAMS_KEY: {
            taskName: 'taskName',
            taskJar: 'taskJar',
            mainClass: 'mainClass',
            applicationArguments: 'applicationArguments',
            driverCores: 'driverCores',
            driverMemory: 'driverMemory',
            executorCores: 'executorCores',
            executorMemory: 'executorMemory',
            executorNum: 'executorNum',
            sparkSubmitProperties: 'sparkSubmitProperties',
            sparkVersion: 'sparkVersion'
        }
    }
};


