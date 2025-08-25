# --- Stage 1: Build Aşaması ---
# Node.js ortamını kullanarak React/Vite projemizi build ediyoruz.
FROM node:18-alpine AS builder

WORKDIR /app

# Önce package.json'ı kopyalayıp bağımlılıkları yüklüyoruz.
# Bu katman sadece bağımlılıklar değiştiğinde tekrar çalışır, build'leri hızlandırır.
COPY package*.json ./
RUN npm install

# Tüm proje dosyalarını kopyalıyoruz.
COPY . .

# Projeyi build edip statik dosyaları oluşturuyoruz.
RUN npm run build

# --- Stage 2: Sunucu Aşaması ---
# Statik dosyaları sunmak için çok hafif bir NGINX imajı kullanıyoruz.
FROM nginx:stable-alpine

# Build aşamasında oluşturulan statik dosyaları NGINX'in sunacağı klasöre kopyalıyoruz.
COPY --from=builder /app/dist /usr/share/nginx/html

# NGINX'in SPA'lar ile uyumlu çalışması için kendi yapılandırma dosyamızı kopyalıyoruz.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# NGINX'in çalışacağı 80 portunu açıyoruz.
EXPOSE 80

# Container başladığında NGINX'i başlatıyoruz.
CMD ["nginx", "-g", "daemon off;"]