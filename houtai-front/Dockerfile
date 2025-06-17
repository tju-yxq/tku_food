# yxq/houtai-front/Dockerfile

# --- 第一阶段: 构建 ---
# 使用官方的Node.js镜像作为构建环境，并命名为 'builder'
FROM node:16-alpine as builder

# 设置工作目录
WORKDIR /app

# 复制 package.json 和 package-lock.json
COPY package*.json ./

# 安装项目依赖
RUN npm install

# 复制所有项目文件到工作目录
COPY . .

# 执行生产构建
# 注意：构建命令会使用 vue.config.js 中为生产环境配置的 CDN 地址
RUN npm run build

# --- 第二阶段: 运行 ---
# 使用轻量级的 Nginx 镜像作为最终的运行环境
FROM nginx:stable-alpine

# 将第一阶段构建好的静态文件复制到 Nginx 的默认网站根目录
COPY --from=builder /app/dist /usr/share/nginx/html

# （可选）如果需要自定义Nginx配置（例如解决history模式下的404问题），
# 可以取消下面两行的注释，并创建一个 nginx.conf 文件
# COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露 80 端口
EXPOSE 80

# 启动 Nginx 服务
CMD ["nginx", "-g", "daemon off;"]