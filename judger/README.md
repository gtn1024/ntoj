# 评测机开发指南

## 评测机工作流程

1. 启动时，程序首先检测评测服务器是否正常工作。如果不正常，则 5 秒后重试。
2. 向服务端请求评测任务。如果无评测任务，则 1 秒后回到第 1 步重试。若无法连接到服务端，则 5 秒后重试。
3. 收到任务后，将该评测任务的状态改为 `COMPILING`，并开始编译代码。
4. 若编译失败，向服务端返回 `COMPILE_ERROR`，1 秒后返回第 1 步。
   若编译成功，则将评测任务状态改为 `JUDGING`，并向服务端获取测试数据，执行测试。
5. 向服务端报告评测结果

## 运行要求

需要确保运行 [沙箱程序](https://github.com/criyle/go-judge)，可使用 Docker 运行，具体见该项目文档。
确保沙箱程序所在系统中装有对应编译器，具体可见
[`deploy/docker/judgeserver.cn.Dockerfile`](../deploy/docker/judgeserver.cn.Dockerfile)。

同时，评测机程序运行需要配置环境变量。具体可见
[`judger/src/main/kotlin/zip/ntoj/judger/Configuration.kt`](src/main/kotlin/zip/ntoj/judger/Configuration.kt)。
