# API Test Rehberi – Postman

Bu belge, projedeki servislerin API uç noktalarını Postman ile test etmek için adım adım yönergeler içerir.

---

## 1. Postman Ortamı (Environment) Kurulumu
Postman’de **Environments** → **Add** tıklayarak yeni bir ortam oluşturun ve aşağıdaki değişkenleri ekleyin. Bu, servis portlarını tek bir yerden yönetmenizi sağlar.

| Key                    | Initial / Current Value            | Açıklama |
|------------------------|------------------------------------|----------|
| `USER_SERVER_PORT`     | `8081`                             | `user-service` portu |
| `DEPARTMENT_SERVER_PORT` | `8082`                             | `department-service` portu |
| `user_base_url`        | `http://localhost:{{USER_SERVER_PORT}}` | `user-service` temel adresi |
| `department_base_url`  | `http://localhost:{{DEPARTMENT_SERVER_PORT}}` | `department-service` temel adresi |
| `token`                | *(boş bırakın)*                     | Login sonrası otomatik dolacak |

Ortamı kaydedin ve aktif hale getirin.

---

## 2. User-Service API

### 2.1. Register – Kullanıcı Kaydı
| Özellik | Değer |
|---------|-------|
| Method  | `POST` |
| URL     | `{{user_base_url}}/api/auth/register` |
| Headers | `Content-Type: application/json` |

**Gövde (Body → raw → JSON):**
```json
{
  "name": "Ahmet",
  "surname": "Yılmaz",
  "email": "ahmet@example.com",
  "userName": "ahmet",
  "password": "Secret123!",
  "phone": "+90 555 000 0000",
  "departmentId": 1
}
```
> **Not:** `departmentId` olarak gönderilen ID, `departments` tablosunda gerçekten var olmalıdır.

**Başarılı Yanıt (200 OK):**
```json
{
  "token": "<JWT>"
}
```

**Test Script (Tests sekmesine yapıştırın):**
Bu script, dönen token'ı otomatik olarak ortam değişkenine kaydeder.
```js
const json = pm.response.json();
if (json && json.token) {
    pm.environment.set("token", json.token);
    console.log("Token saved to environment.");
}
```

### 2.2. Login – Kullanıcı Girişi
| Özellik | Değer |
|---------|-------|
| Method  | `POST` |
| URL     | `{{user_base_url}}/api/auth/login` |
| Headers | `Content-Type: application/json` |

**Gövde:**
```json
{
  "userName": "ahmet",
  "password": "Secret123!"
}
```
> Test script'i olarak Register bölümündeki script'in aynısını kullanabilirsiniz.

---

## 3. Department-Service API
> **Önemli:** Bu servise atılacak tüm istekler, **Authorization** sekmesinde `Type` olarak `Bearer Token` ve `Token` olarak `{{token}}` seçilerek yapılmalıdır.

### 3.1. Departman Ağacını Listeleme
| Özellik | Değer |
|---------|-------|
| Method  | `GET` |
| URL     | `{{department_base_url}}/api/departments/tree` |
| Yetki   | `ROLE_USER` veya `ROLE_ADMIN` |

### 3.2. Yeni Departman Oluşturma (Admin Gerekli)
| Özellik | Değer |
|---------|-------|
| Method  | `POST` |
| URL     | `{{department_base_url}}/api/departments` |
| Yetki   | `ROLE_ADMIN` |

**Gövde:**
```json
{
  "name": "AR-GE",
  "parentDepartmentId": 1
}
```

### 3.3. Departman Güncelleme (Admin Gerekli)
| Özellik | Değer |
|---------|-------|
| Method  | `PUT` |
| URL     | `{{department_base_url}}/api/departments/{id}` *(Örn: `/api/departments/27`)* |
| Yetki   | `ROLE_ADMIN` |

**Gövde:**
```json
{
  "name": "Araştırma ve Geliştirme",
  "parentDepartmentId": 1
}
```

### 3.4. Departman Silme (Admin Gerekli)
| Özellik | Değer |
|---------|-------|
| Method  | `DELETE` |
| URL     | `{{department_base_url}}/api/departments/{id}` |
| Yetki   | `ROLE_ADMIN` |

---

## 4. Hata Giderme
1.  **401 Unauthorized:** JWT eksik, geçersiz veya süresi dolmuş. Tekrar **Login** yapın.
2.  **403 Forbidden:** Bu işlemi yapmaya yetkiniz yok. İsteğin `ROLE_ADMIN` yetkisi gerektirip gerektirmediğini kontrol edin ve admin kullanıcısıyla login yaptığınızdan emin olun.
3.  **500 Internal Server Error (Register sırasında):** `department-service` çalışmıyor olabilir veya gönderdiğiniz `departmentId` geçersiz olabilir. Servis loglarını kontrol edin.
4.  **"Connection refused":** İlgili servisin konteyneri çalışmıyor veya `.env` dosyasındaki port ayarları yanlış.
