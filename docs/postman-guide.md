# API Test Rehberi – Postman

Bu belge, projedeki servislerin API uç noktalarını Postman ile test etmek için adım adım yönergeler içerir.

---

## 1. Postman Ortamı (Environment) Kurulumu
Postman’de **Environments** → **Add** tıklayarak yeni bir ortam oluşturun ve aşağıdaki değişkenleri ekleyin.

| Key                    | Initial / Current Value            | Açıklama |
|------------------------|------------------------------------|----------|
| `USER_SERVER_PORT`     | `8081`                             | `user-service` portu |
| `DEPARTMENT_SERVER_PORT` | `8082`                             | `department-service` portu |
| `TICKET_SERVER_PORT`   | `8083`                             | `ticket-service` portu |
| `user_base_url`        | `http://localhost:{{USER_SERVER_PORT}}` | `user-service` temel adresi |
| `department_base_url`  | `http://localhost:{{DEPARTMENT_SERVER_PORT}}` | `department-service` temel adresi |
| `ticket_base_url`     | `http://localhost:{{TICKET_SERVER_PORT}}` | `ticket-service` temel adresi |
| `token`                | *(boş bırakın)*                     | Login sonrası otomatik dolacak |

---

## 2. User-Service API

### 2.1. Register – Kullanıcı Kaydı
- **Method:** `POST`
- **URL:** `{{user_base_url}}/api/auth/register`

**Gövde (Body → raw → JSON):**
```json
{
  "name": "Ayşe",
  "surname": "Kara",
  "email": "ayse@example.com",
  "userName": "ayse",
  "password": "Secret123!",
  "phone": "+90 555 111 2233",
  "departmentId": 2
}
```

### 2.2. Login – Kullanıcı Girişi
- **Method:** `POST`
- **URL:** `{{user_base_url}}/api/auth/login`

**Gövde:**
```json
{
  "userName": "ayse",
  "password": "Secret123!"
}
```

**Test Script (Register ve Login için):**
Bu script, dönen token'ı otomatik olarak ortam değişkenine kaydeder.
```js
const json = pm.response.json();
if (json && json.token) {
    pm.environment.set("token", json.token);
    console.log("Token saved to environment.");
}
```

---

## 3. Department-Service API
> **Önemli:** Bu servise atılacak tüm istekler için **Authorization** sekmesinde `Type` olarak `Bearer Token` ve `Token` olarak `{{token}}` seçilmelidir.

### 3.1. Departman Yönetimi

- **`GET /api/departments/tree`**: Aktif departman ağacını listeler. (Yetki: `ROLE_USER` / `ROLE_ADMIN`)
- **`POST /api/departments`**: Yeni departman oluşturur. (Yetki: `ROLE_ADMIN`)
  - **Gövde:** `{ "name": "AR-GE", "parentDepartmentId": 1 }`
- **`PUT /api/departments/{id}`**: Departman günceller. (Yetki: `ROLE_ADMIN`)
  - **Gövde:** `{ "name": "Araştırma ve Geliştirme", "parentDepartmentId": 1 }`
- **`DELETE /api/departments/{id}`**: Departmanı pasif hale getirir. (Yetki: `ROLE_ADMIN`)

### 3.2. Personel (Çalışan) Yönetimi

- **`GET /api/persons`**: Aktif personelleri listeler. (Yetki: `ROLE_USER` / `ROLE_ADMIN`)
- **`GET /api/persons/{id}`**: ID'si verilen aktif personeli getirir. (Yetki: `ROLE_USER` / `ROLE_ADMIN`)
- **`POST /api/persons`**: Yeni personel oluşturur. (Yetki: `ROLE_ADMIN`)
  - **Gövde:** `{ "name": "Mehmet", "surname": "Can", "email": "mehmet@example.com", "phone": "+90 544 888 7766", "departmentId": 3 }`
- **`PUT /api/persons/{id}`**: Personel bilgilerini günceller. (Yetki: `ROLE_USER` / `ROLE_ADMIN`)
  - **Gövde:** `{ "name": "Mehmet", "surname": "Canlı", "email": "mehmet.canli@example.com", "phone": "+90 544 888 7766", "departmentId": 4 }`
- **`DELETE /api/persons/{id}`**: Personeli pasif hale getirir. (Yetki: `ROLE_USER` / `ROLE_ADMIN`)

### 3.3. Personel Yönetimi (Admin'e Özel)

- **`GET /api/persons/all`**: Aktif ve pasif TÜM personelleri listeler. (Yetki: `ROLE_ADMIN`)
- **`GET /api/persons/inactive`**: Sadece pasif (silinmiş) personelleri listeler. (Yetki: `ROLE_ADMIN`)
- **`PUT /api/persons/{id}/reactivate`**: Pasif bir personeli tekrar aktif hale getirir. (Yetki: `ROLE_ADMIN`)

---

## 4. Ticket-Service API
> **Yetkilendirme Notu:** `public` ile işaretlenmiş endpoint'ler hariç tüm istekler için **Authorization** sekmesinde `Type` olarak `Bearer Token` ve `Token` olarak `{{token}}` seçilmelidir.

### 4.1. Halka Açık Endpoint'ler (Yetkilendirme Gerekmez)

#### 4.1.1. Aktif Kategorileri Listele
Ticket oluşturma formlarındaki kategori seçim alanını doldurmak için kullanılır.

- **Method:** `GET`
- **URL:** `{{ticket_base_url}}/api/public/categories`

#### 4.1.2. Müşteri Ticketi Oluştur
Halka açık portal üzerinden kimlik doğrulaması olmadan ticket oluşturur.

- **Method:** `POST`
- **URL:** `{{ticket_base_url}}/api/tickets/public`

**Gövde (Body → raw → JSON):**
```json
{
  "name": "Ali",
  "surname": "Veli",
  "email": "ali.veli@customerexample.com",
  "phone": "+90 533 444 5566",
  "issue": "Yazıcım çalışmıyor, acil destek rica ediyorum.",
  "priority": "HIGH",
  "category": "TECHNICAL_SUPPORT"
}
```

### 4.2. Çalışan Endpoint'leri (Yetki: `ROLE_USER`)

#### 4.2.1. Çalışan Ticketi Oluştur
Sisteme giriş yapmış bir çalışan adına ticket oluşturur.

- **Method:** `POST`
- **URL:** `{{ticket_base_url}}/api/tickets`

**Gövde (Body → raw → JSON):**
```json
{
  "issue": "Müşteri yönetim panelinde yavaşlık var.",
  "priority": "MEDIUM",
  "category": "SOFTWARE_ISSUE"
}
```

#### 4.2.2. Ticket Detayını Görüntüle
ID'si verilen bir ticket'ın detaylarını getirir.

- **Method:** `GET`
- **URL:** `{{ticket_base_url}}/api/tickets/{id}`

### 4.3. Kategori Yönetimi (Yetki: `ROLE_ADMIN`)
Bu bölümdeki tüm endpoint'ler `ROLE_ADMIN` yetkisi gerektirir.

#### 4.3.1. Tüm Kategorileri Listele
Sistemdeki aktif ve pasif tüm ticket kategorilerini listeler.

- **Method:** `GET`
- **URL:** `{{ticket_base_url}}/api/management/categories`

#### 4.3.2. Yeni Kategori Oluştur
Ticketların yönlendirileceği yeni bir kategori tanımlar.

- **Method:** `POST`
- **URL:** `{{ticket_base_url}}/api/management/categories`

**Gövde (Body → raw → JSON):**
```json
{
  "categoryKey": "HARDWARE_ISSUE",
  "displayName": "Donanım Sorunları",
  "targetDepartmentId": 14
}
```

#### 4.3.3. Kategori Durumunu Güncelle
Bir kategoriyi aktif veya pasif hale getirir.

- **Method:** `PATCH`
- **URL:** `{{ticket_base_url}}/api/management/categories/{id}/status`

**Gövde (Body → raw → JSON):**
```json
{
  "isActive": false
}
```

---

## 5. Hata Giderme
- **401 Unauthorized:** JWT eksik, geçersiz veya süresi dolmuş. Tekrar **Login** yapın.
- **403 Forbidden:** Bu işlemi yapmaya yetkiniz yok. İsteğin `ROLE_ADMIN` yetkisi gerektirip gerektirmediğini kontrol edin ve admin kullanıcısıyla login yaptığınızdan emin olun.
- **500 Internal Server Error (Register sırasında):** `department-service` çalışmıyor veya gönderdiğiniz `departmentId` geçersiz/pasif olabilir. Servis loglarını kontrol edin.
- **"Connection refused":** İlgili servisin konteyneri çalışmıyor veya `.env` dosyasındaki port ayarları yanlış.
