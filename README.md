# 校园失物招领平台

基于 Vue 3 + Spring Boot 的校园失物招领平台，面向学生与管理员两类角色，覆盖失物发布、拾物登记、认领申请、信息审核、分类管理和数据统计等核心流程。

## 项目简介

这是一个前后端分离的校园失物招领项目，适合用于课程设计、项目展示和前后端综合练习。前端负责页面展示与交互，后端提供接口、权限控制和数据处理，数据库用于存储用户、物品、分类和认领记录。

## 技术栈

### 前端

- Vue 3
- Vite
- Vue Router
- Pinia
- Axios
- Vitest

### 后端

- Java 17
- Spring Boot 2.7
- Spring Web
- Spring JDBC
- Spring Validation
- Spring Security Crypto
- JWT

### 数据库与部署

- MySQL
- Docker

## 核心功能

### 学生端

- 用户注册与登录
- 浏览失物大厅与拾物大厅
- 发布失物信息和拾物信息
- 上传物品图片
- 查看物品详情
- 管理个人发布记录
- 提交认领申请
- 查看认领记录与处理状态

### 管理员端

- 后台首页概览
- 物品列表管理
- 物品审核
- 认领审核
- 用户管理
- 分类管理
- 平台数据统计

## 项目结构

```text
校园失物平台
├─ frontend        # Vue 3 前端项目
├─ backend         # Spring Boot 后端项目
├─ database        # 数据库初始化脚本
├─ Dockerfile      # Docker 构建文件
└─ README.md
```

## 运行环境

- Node.js 18 及以上
- JDK 17
- Maven 3.8 及以上
- MySQL 8.x

## 本地运行

### 1. 初始化数据库

执行 [database/campus_lost_found.sql](/D:/校园失物平台/database/campus_lost_found.sql) 创建数据库和基础数据。

默认数据库名：

```sql
campus_lost_found
```

### 2. 启动后端

后端配置文件位于 [backend/src/main/resources/application.yml](/D:/校园失物平台/backend/src/main/resources/application.yml)。

默认配置如下：

- 服务端口：`8080`
- 数据库名：`campus_lost_found`
- 数据库用户名：`root`
- 数据库密码：`123456`

启动命令：

```bash
cd backend
mvn spring-boot:run
```

### 3. 启动前端

前端项目配置位于 [frontend/package.json](/D:/校园失物平台/frontend/package.json)。

启动命令：

```bash
cd frontend
npm install
npm run dev
```

前端访问地址：

```text
http://localhost:5173
```

开发环境下，前端会将 `/api` 请求代理到：

```text
http://localhost:8080
```

## Docker 部署

项目根目录已提供 [Dockerfile](/D:/校园失物平台/Dockerfile)，可直接构建并运行：

```bash
docker build -t campus-lost-found .
docker run -p 8080:8080 campus-lost-found
```

## 默认演示账号

数据库初始化脚本中包含示例账号：

- 管理员：`admin`
- 学生：`student`

系统使用 BCrypt 进行密码加密，使用 JWT 进行登录鉴权。实际演示时请以你本地数据库中的账号数据为准。

## 接口模块

主要接口模块如下：

- `/api/auth`：登录、注册、登录态检查
- `/api/items`：物品发布、查询、编辑、删除、审核、图片上传
- `/api/claims`：认领申请与审核
- `/api/categories`：分类管理
- `/api/stats`：统计数据

## 测试与构建

### 前端

```bash
cd frontend
npm run test
npm run build
```

### 后端

```bash
cd backend
mvn test
mvn package
```
