# NTOJ

NTOJ 是基于 Spring Boot 和 React.js 的在线评测（OJ）系统。

**注意：NTOJ 仍在快速迭代开发中，所有修改不做兼容性保证（含数据库结构）。请勿用于生产环境！**

## 项目结构

- `buildSrc`：Gradle 自定义构建插件
- `shared`：公共模块
- `server`：服务端
- `judger`：评测机
- `web`：Web 端

## 使用技术

服务端主要使用了 [Kotlin](https://kotlinlang.org/)、[Spring Boot](https://spring.io/projects/spring-boot)、
[Spring Data JPA](https://spring.io/projects/spring-data-jpa)、[Sa-Token](http://sa-token.dev33.cn/)、
[Flyway](https://flywaydb.org/)、[PostgreSQL](https://www.postgresql.org/) 等技术。

Web 端主要使用了 [TypeScript](https://www.typescriptlang.org/)、[React](https://reactjs.org/)、
[Ant Design](https://ant.design/)、[Vite](https://vitejs.dev/)、[React Router](https://reactrouter.com/) 等技术。

## 开发指南（环境搭建）

硬性要求是使用 Java 17+ 和 [Node.js 18+](https://nodejs.org/)。

服务端开发指南见 [server/README.md](server/README.md)。

Web 端开发指南见 [web/README.md](web/README.md)。

评测机开发指南见 [judger/README.md](judger/README.md)。

## 部署指南（WIP）

[Caddy 配置文件](deploy/caddy/Caddyfile)
