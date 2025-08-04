# API Test Rehberi – Postman

Bu belge, projedeki servislerin API uç noktalarını Postman ile test etmek için adım adım yönergeler içerir.

---

## 1. Postman Ortamı (Environment) Kurulumu
Postman’de **Environments** → **Add** tıklayarak yeni bir ortam oluşturun ve aşağıdaki değişkenleri ekleyin.

| Key                    | Initial / Current Value            | Açıklama |
|------------------------|------------------------------------|----------|
| `USER_SERVER_PORT`     | `8081`                             | `user-service` portu |
| `DEPARTMENT_SERVER_PORT` | `8082`                             | `department-service` portu |
| `user_base_url`        | `http://localhost:{{USER_SERVER_PORT}}` | `user-service` temel adresi |
| `department_base_url`  | `http://localhost:{{DEPARTMENT_SERVER_PORT}}` | `department-service` temel adresi |
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

## 4. Hata Giderme
- **401 Unauthorized:** JWT eksik, geçersiz veya süresi dolmuş. Tekrar **Login** yapın.
- **403 Forbidden:** Bu işlemi yapmaya yetkiniz yok. İsteğin `ROLE_ADMIN` yetkisi gerektirip gerektirmediğini kontrol edin ve admin kullanıcısıyla login yaptığınızdan emin olun.
- **500 Internal Server Error (Register sırasında):** `department-service` çalışmıyor veya gönderdiğiniz `departmentId` geçersiz/pasif olabilir. Servis loglarını kontrol edin.
- **"Connection refused":** İlgili servisin konteyneri çalışmıyor veya `.env` dosyasındaki port ayarları yanlış.
