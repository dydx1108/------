CREATE DATABASE IF NOT EXISTS campus_lost_found
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE campus_lost_found;

DROP TABLE IF EXISTS claims;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(120) NOT NULL,
  real_name VARCHAR(50) NOT NULL,
  role ENUM('admin', 'student') NOT NULL DEFAULT 'student',
  phone VARCHAR(20),
  college VARCHAR(80),
  status ENUM('active', 'disabled') NOT NULL DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL UNIQUE,
  color VARCHAR(20) NOT NULL DEFAULT '#1f9d88',
  description VARCHAR(200)
);

CREATE TABLE items (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  type ENUM('lost', 'found') NOT NULL,
  category_id INT NOT NULL,
  location VARCHAR(120) NOT NULL,
  event_date DATE NOT NULL,
  description TEXT NOT NULL,
  image_url VARCHAR(255),
  contact_name VARCHAR(50) NOT NULL,
  contact_phone VARCHAR(20) NOT NULL,
  status ENUM('pending', 'published', 'claimed', 'closed', 'rejected') NOT NULL DEFAULT 'pending',
  publisher_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_items_category FOREIGN KEY (category_id) REFERENCES categories(id),
  CONSTRAINT fk_items_user FOREIGN KEY (publisher_id) REFERENCES users(id)
);

CREATE TABLE claims (
  id INT PRIMARY KEY AUTO_INCREMENT,
  item_id INT NOT NULL,
  user_id INT NOT NULL,
  evidence VARCHAR(300) NOT NULL,
  status ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',
  reply VARCHAR(300),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_claims_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
  CONSTRAINT fk_claims_user FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO users (id, username, password, real_name, role, phone, college, status) VALUES
(1, 'admin', '$2b$10$5SvK3rleo2da4cVM30UJoe/di/jtTFuk2Yyt43aHsV80hLht46N6m', '平台管理员', 'admin', '13800000000', '信息中心', 'active'),
(2, 'student', '$2b$10$5SvK3rleo2da4cVM30UJoe/di/jtTFuk2Yyt43aHsV80hLht46N6m', '李同学', 'student', '13900000000', '软件学院', 'active'),
(3, 'chenxi', '$2b$10$5SvK3rleo2da4cVM30UJoe/di/jtTFuk2Yyt43aHsV80hLht46N6m', '陈曦', 'student', '13910010001', '计算机学院', 'active'),
(4, 'wangyue', '$2b$10$5SvK3rleo2da4cVM30UJoe/di/jtTFuk2Yyt43aHsV80hLht46N6m', '王悦', 'student', '13910010002', '外国语学院', 'active'),
(5, 'zhaolei', '$2b$10$5SvK3rleo2da4cVM30UJoe/di/jtTFuk2Yyt43aHsV80hLht46N6m', '赵磊', 'student', '13910010003', '机电工程学院', 'active'),
(6, 'suning', '$2b$10$5SvK3rleo2da4cVM30UJoe/di/jtTFuk2Yyt43aHsV80hLht46N6m', '苏宁', 'student', '13910010004', '新闻传播学院', 'active'),
(7, 'hejia', '$2b$10$5SvK3rleo2da4cVM30UJoe/di/jtTFuk2Yyt43aHsV80hLht46N6m', '何嘉', 'student', '13910010005', '艺术设计学院', 'active'),
(8, 'linran', '$2b$10$5SvK3rleo2da4cVM30UJoe/di/jtTFuk2Yyt43aHsV80hLht46N6m', '林冉', 'student', '13910010006', '经济管理学院', 'active');

INSERT INTO categories (id, name, color, description) VALUES
(1, '证件卡包', '#1f9d88', '身份证、学生证、校园卡等'),
(2, '电子设备', '#246bfe', '耳机、手机、平板等'),
(3, '包裹快递', '#f59f00', '快递盒、包装、外卖袋等'),
(4, '生活用品', '#e8590c', '雨伞、帽子、水杯等'),
(5, '其他物品', '#7950f2', '无法归类的其他物品');

INSERT INTO items
(id, title, type, category_id, location, event_date, description, image_url, contact_name, contact_phone, status, publisher_id)
VALUES
(1, '校园卡', 'lost', 1, '图书馆一楼自助借还区', '2026-06-10', '蓝色卡套，卡背贴有课程表小贴纸，如有拾到请联系。', '', '陈曦', '13910010001', 'pending', 3),
(2, '黑色双肩包', 'lost', 5, '第二教学楼 402 教室', '2026-06-11', '包里有高数笔记和一把折叠伞，背带上挂着一个小熊挂件。', '', '赵磊', '13910010003', 'published', 5),
(3, '银色保温杯', 'lost', 4, '教学楼 B 座 508 门口', '2026-06-11', '杯身贴有“多喝热水”贴纸，杯盖完好无漏水。', '', '苏宁', '13910010004', 'published', 6),
(4, '快递文件袋', 'lost', 3, '菜鸟驿站取件口旁长椅', '2026-06-11', '白色文件袋，里面装着课程报名材料和打印件。', '', '李同学', '13900000000', 'published', 2),
(5, '黑色 U 盘', 'lost', 2, '实验楼 302 机房', '2026-06-12', '金属外壳 64G U 盘，里面有课程作业和比赛资料。', '', '何嘉', '13910010005', 'pending', 7),
(6, '学生证', 'lost', 1, '北门公交站候车棚', '2026-06-12', '学生证原本夹在一本四六级词汇书里，现在两样都找不到了。', '', '林冉', '13910010006', 'published', 8),
(7, '白色充电宝', 'lost', 2, '操场主席台旁看台', '2026-06-13', '10000 毫安充电宝，侧面有绿色贴纸，附一根短数据线。', '', '王悦', '13910010002', 'pending', 4),
(8, '黄色雨伞', 'found', 4, '三号教学楼 203', '2026-06-14', '在教室后排座位旁拾到一把黄色折叠雨伞，伞面有卡通图案。', '/uploads/items/1781363856173..jpeg', '李同学', '13900000000', 'published', 2),
(9, '快递包裹', 'found', 3, '图书馆二楼自习区', '2026-06-14', '自习区角落发现一个棕色快递包裹，外箱略有破损。', '/uploads/items/1781364357409..jpeg', '陈曦', '13910010001', 'published', 3),
(10, '金属框眼镜', 'found', 2, '食堂一楼靠窗餐桌', '2026-06-14', '拾到一副金属框眼镜，旁边还有一个黑色眼镜盒。', '/uploads/items/1781364363210..jpeg', '王悦', '13910010002', 'published', 4),
(11, '钥匙串', 'found', 5, '明德楼 A 区储物柜旁', '2026-06-15', '一串钥匙和小挂件一起拾到，挂件有豹纹蝴蝶结。', '/uploads/items/1781364375595..jpeg', '赵磊', '13910010003', 'published', 5),
(12, '白色针织帽', 'found', 4, '图书馆自助打印区', '2026-06-15', '打印机旁边拾到一顶白色针织帽，比较干净。', '/uploads/items/6ca9dec511cd00acc2e563681405094a_750.png', '苏宁', '13910010004', 'published', 6),
(13, '图书借阅证和笔袋', 'found', 1, '四号教学楼 104 教室', '2026-06-15', '桌面上拾到借阅证和一个旧笔袋，证件信息可现场核对。', '/uploads/items/acd95e05b77a34b5c2584433bed40109_750.png', '何嘉', '13910010005', 'claimed', 7),
(14, '黑色头戴式耳机', 'found', 2, '宿舍 6 栋楼下', '2026-06-15', '在宿舍楼下长椅附近拾到黑色头戴式耳机，耳罩保存完好。', '/uploads/items/d0eaeffbe99756059cc7f3baa3c927c9_750.png', '林冉', '13910010006', 'published', 8);

INSERT INTO claims (id, item_id, user_id, evidence, status, reply) VALUES
(1, 13, 2, '借阅证姓名和学号都能核对上，笔袋里还有我平时用的自动铅笔。', 'pending', NULL);
