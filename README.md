# 校园失物招领平台

这是一个完整可运行的校园失物招领平台，项目按你的工具要求拆分为：

- `backend`：IDEA 后端项目，Spring Boot + MySQL。
- `frontend`：VS Code 前端项目，Vue3 + Vite。
- `database`：Navicat 可直接导入的 MySQL 数据库脚本。

## 技术栈

- 前端：Vue3、Vue Router、Pinia、Axios、Vite
- 后端：Java 8、Spring Boot 2.7、Spring JDBC、JWT、BCrypt
- 数据库：MySQL，可用 Navicat 导入 `database/campus_lost_found.sql`
- 图片：已从 `D:\jietu` 复制到 `frontend/public/uploads/items`

## 运行步骤

1. 在 Navicat 中新建连接，执行 `database/campus_lost_found.sql`。
2. 用 IDEA 打开 `backend` 文件夹，修改 `backend/src/main/resources/application.yml` 中的 MySQL 账号密码。
3. 在 IDEA 中运行 `CampusLostFoundApplication`，后端地址为 `http://localhost:8080`。
4. 用 VS Code 打开 `frontend` 文件夹，执行 `npm install`。
5. 在 VS Code 终端执行 `npm run dev`，浏览器访问 `http://localhost:5173`。

## 默认账号

- 管理员：`admin` / `123456`
- 学生：`student` / `123456`

## 功能说明

- 双角色登录：管理员和学生登录后进入不同页面。
- 10+ 视图页面：首屏、登录、学生首页、我的发布、发布失物、发布拾物、认领记录、消息中心、管理员首页、物品管理、用户管理、分类管理、审核中心、数据统计。
- 数据展示：表格、卡片列表、统计面板。
- CRUD：物品、用户、分类、认领记录支持查询、新增、修改、删除或审核。
- 权限控制：路由守卫 + 后端 JWT + 角色校验。
- 表单验证与提示：前后端都包含必要验证和消息反馈。
- 动效：首屏、登录页和内屏均包含入场、悬浮、点击、常驻、鼠标跟随、流光、粒子、视差等效果，并支持 `prefers-reduced-motion`。
