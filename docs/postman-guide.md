# User-Service API – Postman Test Rehberi

Bu belge, **user-service** API uç noktalarını Postman ile test etmek için adım adım yönergeler içerir.

> Tüm istek URL’lerinde `localhost:${USER_SERVER_PORT}` kullanın. `.env` dosyasında varsayılan `USER_SERVER_PORT` değeri `8081` dir.

---

## 1. Postman Ortamı Oluşturun
1. Postman’de **Environments** → **Add** tıklayın.  
2. Aşağıdaki anahtar–değer çiftlerini ekleyin:
   | Key | Initial / Current Value |
   |-----|------------------------|
   | `base_url` | `http://localhost:{{USER_SERVER_PORT}}` |
   | `token`    | *(boş bırakın – login sonrası set edilecek)* |
   | `USER_SERVER_PORT` | `8081` *(veya .env’deki değer)* |
3. **Save** edin ve ortamı seçin.

---

## 2. Register – Kullanıcı Kaydı
| Özellik | Değer |
|---------|-------|
| Method  | `POST` |
| URL     | `{{base_url}}/api/auth/register` |
| Headers | `Content-Type: application/json` |

### Gövde (raw → JSON)
```json
{
  "name": "Ahmet",
  "surname": "Yılmaz",
  "email": "ahmet@example.com",
  "userName": "ahmet",
  "password": "Secret123!",
  "phone": "+90 555 000 0000",
  "department": "IT"
}
```

### Başarılı Yanıt
```json
{
  "token": "<JWT>"
}
```

> **Test Script** (Tests sekmesine yapıştırın) – token’ı ortama kaydeder:
```js
const json = pm.response.json();
pm.environment.set("token", json.token);
```

---

## 3. Login – Kullanıcı Girişi
| Özellik | Değer |
| Method  | `POST` |
| URL     | `{{base_url}}/api/auth/login` |
| Headers | `Content-Type: application/json` |

### Gövde
```json
{
  "userName": "ahmet",
  "password": "Secret123!"
}
```

### Test Script
```js
const json = pm.response.json();
pm.environment.set("token", json.token);
```

---

## 4. Yetkili İstekler
Login/Register sonrası alınan JWT’yi `token` ortam değişkenine kaydettiğimiz için, diğer isteklerde sadece şu Header’ı ekleyin:

```
Authorization: Bearer {{token}}
```

> Postman’de **Headers** → `Authorization` anahtarı, **VALUE** kısmına `Bearer {{token}}` yazın.

---

## 5. Örnek Korunan İstek (sağlık kontrolü gibi)
`GET {{base_url}}/api/secure/ping`  *(varsayımsal endpoint)*

Headers: `Authorization: Bearer {{token}}`

---

## 6. Docker Compose ile Çalıştırma
```bash
# Proje kök dizininde
docker-compose up --build
```
Hizmetler başladıktan sonra Postman istekleri gönderebilirsiniz.

---

## 7. Hata Giderme
1. **401 Unauthorized**  
   – JWT eksik / süresi dolmuş. Login tekrar yapın.
2. **409 Conflict** (register)  
   – `userName` zaten mevcut.
3. **Could not connect**  
   – user-service konteyneri çalışmıyor veya farklı port.

---

> Gereksinimler arttığında (refresh token, ticket-service vb.) bu belgeyi güncelleyiniz.
