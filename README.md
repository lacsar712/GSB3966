# 交易策略管理系统

一个基于 Vue 3 + Spring Boot 的交易策略管理平台，支持多角色权限管理、策略全生命周期管理。

## 技术栈

### 前端
- Vue 3 + Vue Router + Pinia
- Element Plus UI 组件库
- Axios HTTP 客户端

### 后端
- Spring Boot 3.x
- Spring Security + JWT
- MyBatis-Plus ORM
- MySQL 8.0

## 功能特性

### 用户认证与权限
- JWT Token 认证
- RBAC 权限控制
- 四种角色：系统管理员、策略管理员、策略交易员、访客

### 策略管理
- 策略创建、编辑、删除
- 策略审核流程（提交审核 → 审核通过/拒绝）
- 策略状态管理（草稿、待审核、已发布、已暂停、已归档）
- 策略参数和风险控制配置（JSON 格式）

### 用户管理
- 用户 CRUD 操作
- 角色分配
- 账号启用/禁用

## 项目结构

```
trading-strategy-system/
├── backend/                     # 后端项目
│   ├── src/main/java/com/trading/
│   │   ├── config/             # 配置类
│   │   ├── controller/         # 控制层
│   │   ├── dto/                # 数据传输对象
│   │   ├── entity/             # 实体类
│   │   ├── exception/          # 异常处理
│   │   ├── mapper/             # 数据访问层
│   │   ├── security/           # 安全配置
│   │   ├── service/            # 业务层
│   │   └── vo/                 # 视图对象
│   └── src/main/resources/
│       └── db/init.sql         # 数据库初始化脚本
├── frontend/                    # 前端项目
│   ├── src/
│   │   ├── api/                # API 接口
│   │   ├── router/             # 路由配置
│   │   ├── store/              # 状态管理
│   │   ├── utils/              # 工具函数
│   │   └── views/              # 页面组件
│   └── nginx.conf              # Nginx 配置
├── docker-compose.yml           # Docker 编排
└── README.md                    # 项目说明
```

## How to Run

### 环境要求
- Docker 20.10+
- Docker Compose 2.0+

### 启动命令

```bash
docker compose up
```

首次启动会自动构建镜像并初始化数据库。

## Services

- **前端**: http://localhost:3000
- **后端 API**: http://localhost:8080
- **MySQL**: localhost:3306 (root/123456)

## Verification

### 登录验证
1. 访问 http://localhost:3000
2. 使用以下账号登录：
   - 管理员：`admin` / `admin123`
   - 策略管理员：`manager` / `manager123`
   - 交易员：`trader` / `trader123`

### 功能验证

#### 1. 策略管理（交易员角色）
1. 使用 `trader` 账号登录
2. 进入"策略管理"页面
3. 点击"创建策略"，填写策略信息并保存
4. 策略列表会显示新创建的策略（草稿状态）
5. 点击"提交审核"，策略状态变为"待审核"

#### 2. 策略审核（管理员角色）
1. 使用 `manager` 或 `admin` 账号登录
2. 进入"策略管理"页面
3. 找到待审核的策略，点击"审核"
4. 选择"通过"或"拒绝"
5. 审核通过后可进行"启动"/"暂停"操作

#### 3. 用户管理（管理员角色）
1. 使用 `admin` 账号登录
2. 进入"用户管理"页面
3. 可以创建、编辑、删除用户
4. 可以启用/禁用用户账号

## 数据库表结构

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_role | 角色表 |
| sys_user_role | 用户角色关联表 |
| trading_strategy | 交易策略表 |
| sys_operation_log | 操作日志表 |

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/info` - 获取当前用户信息

### 策略接口
- `GET /api/strategy` - 策略列表
- `POST /api/strategy` - 创建策略
- `PUT /api/strategy/{id}` - 更新策略
- `POST /api/strategy/{id}/submit` - 提交审核
- `POST /api/strategy/{id}/review` - 审核策略
- `POST /api/strategy/{id}/start` - 启动策略
- `POST /api/strategy/{id}/pause` - 暂停策略

## 角色权限说明

| 角色 | 权限 |
|------|------|
| ADMIN | 所有权限，包括用户管理 |
| MANAGER | 策略审核、启停管理 |
| TRADER | 创建策略、编辑自己的策略 |
| VISITOR | 只读访问 |

## 系统截图

系统主要界面包括：
- 登录页面：简洁的登录界面，支持多种角色
- 首页 Dashboard：展示策略统计、最近策略、系统公告
- 策略管理页：策略列表、搜索筛选、操作按钮
- 策略编辑页：表单验证、JSON 参数配置
- 用户管理页：用户 CRUD、角色分配
