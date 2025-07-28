# API Test Adımları: Postman ile Kullanıcı Servisi (user-service) Testi

Bu doküman, `user-service`'in temel kimlik doğrulama (authentication) ve kayıt (registration) endpoint'lerini [Postman](https://www.postman.com/downloads/) kullanarak test etmek için gereken adımları içerir.

## Adım 0: Sistemi Başlatma

Tüm testlerden önce, Docker servislerinin en güncel kod ile çalıştığından emin olun. Proje ana dizininde aşağıdaki komutu çalıştırın:

```bash
docker-compose down && docker-compose up --build -d
```

Bu komut, mevcut container'ları durdurur, `user-service` için yeni bir imaj derler ve servisleri arka planda yeniden başlatır. Servislerin tam olarak başlaması için 30-60 saniye bekleyin. `docker-compose logs -f user-service` komutu ile servis loglarını takip edebilirsiniz.

---

## Adım 1: Postman Ayarları

1.  Postman'i açın ve "Collections" sekmesinde yeni bir koleksiyon oluşturun (örn: `CRM API`).
2.  Bu koleksiyon içinde her test senaryosu için yeni bir "Request" ekleyeceksiniz.

---

## Test Senaryoları

### Test 1: Başarılı Müşteri Kaydı

**Amaç:** Sisteme yeni bir müşteri kaydetmek ve karşılığında bir JWT almak.

1.  Koleksiyonunuza `Register Customer` adında yeni bir request ekleyin.
2.  **Method:** `POST` olarak ayarlayın.
3.  **URL:** `http://localhost:8080/api/auth/register/customer` girin.
4.  **Body** sekmesine gidin:
    *   `raw` seçeneğini işaretleyin ve format olarak `JSON` seçin.
    *   Aşağıdaki JSON verisini body alanına yapıştırın:
      ```json
      {
          "name": "Test Customer",
          "email": "test.customer@example.com",
          "password": "strongPassword123",
          "phone": "5551234567"
      }
      ```
5.  **Send** butonuna tıklayın.

**Beklenen Sonuç:**
*   **Status:** `200 OK`
*   **Body:** Aşağıdakine benzer bir JSON cevabı:
  ```json
  {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LmN1c3RvbWVyQGV4YW1wbGUuY29tIiwiaWF0IjoxNj... (uzun bir token dizisi)"
  }
  ```

---

### Test 2: Mevcut E-posta ile Tekrar Kayıt Denemesi

**Amaç:** Sistemin aynı e-posta adresiyle mükerrer kaydı engellediğini doğrulamak.

1.  Test 1'de oluşturduğunuz `Register Customer` request'ini tekrar **Send** butonuna basarak gönderin.

**Beklenen Sonuç:**
*   **Status:** `500 Internal Server Error`
*   **Body:** "Email already in use" mesajını içeren bir hata yanıtı.

---

### Test 3: Başarılı Giriş (Login)

**Amaç:** Daha önce kaydedilen kullanıcının bilgileriyle sisteme giriş yapmak ve yeni bir JWT almak.

1.  Koleksiyonunuza `Login` adında yeni bir request ekleyin.
2.  **Method:** `POST` olarak ayarlayın.
3.  **URL:** `http://localhost:8080/api/auth/login` girin.
4.  **Body** sekmesine gidin (`raw`, `JSON`):
    ```json
    {
        "email": "test.customer@example.com",
        "password": "strongPassword123"
    }
    ```
5.  **Send** butonuna tıklayın.

**Beklenen Sonuç:**
*   **Status:** `200 OK`
*   **Body:** Yeni bir token içeren JSON cevabı:
  ```json
  {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LmN1c3RvbWVyQGV4YW1wbGUuY29tIiwiaWF0IjoxNj... (yeni bir token dizisi)"
  }
  ```

---

### Test 4: Yanlış Şifre ile Giriş Denemesi

**Amaç:** Sistemin yanlış parola girildiğinde girişi engellediğini doğrulamak.

1.  `Login` request'indeki `password` değerini yanlış bir değerle değiştirin (örn: `WRONGpassword`).
2.  **Send** butonuna tıklayın.

**Beklenen Sonuç:**
*   **Status:** `500 Internal Server Error`
*   **Body:** "Invalid credentials" mesajını içeren bir hata yanıtı.

---

### Test 5: Kayıtlı Olmayan Kullanıcı ile Giriş Denemesi

**Amaç:** Veritabanında bulunmayan bir e-posta ile giriş denendiğinde sistemin bunu reddettiğini doğrulamak.

1.  `Login` request'indeki `email` değerini sistemde kayıtlı olmayan bir e-posta ile değiştirin (örn: `nobody@example.com`).
2.  **Send** butonuna tıklayın.

**Beklenen Sonuç:**
*   **Status:** `500 Internal Server Error`
*   **Body:** "Invalid credentials" mesajını içeren bir hata yanıtı. 

---

# Ticket-Service API Testleri

Bu bölüm, daha önce **user-service** üzerinden JWT alındıktan sonra **ticket-service** uç noktalarını test etmek için adımları içerir. Aşağıdaki senaryolarda `${TOKEN}` ifadesi, **Test 3** (Başarılı Giriş) adımında elde edilen JWT değerini temsil eder.

> Postman ipucu: Koleksiyon düzeyinde `{{jwt}}` adında bir variable tanımlayıp, login isteğinin **Tests** sekmesinde
> ```javascript
> pm.collectionVariables.set("jwt", pm.response.json().token);
> ```
> kodunu ekleyerek token’ı otomatik saklayabilirsiniz. Diğer isteklerde *Authorization* sekmesinde **Bearer Token** → `{{jwt}}` yazmanız yeterlidir.

### Test 6: Ticket Oluşturma

**Amaç:** Bir müşteri adına yeni destek talebi (ticket) oluşturmak.

1. Koleksiyona `Create Ticket` adlı yeni bir request ekleyin.
2. **Method:** `POST`
3. **URL:** `http://localhost:8081/api/tickets`
4. **Headers:**
   * `Authorization: Bearer {{jwt}}`
5. **Body** → `raw` / `JSON`:
   ```json
   {
     "customerId": 1,
     "issue": "Mobil uygulama açılmıyor.",
     "category": "Yazılım Hatası",
     "priority": "Yüksek"
   }
   ```
6. **Send** butonuna tıklayın.

**Beklenen Sonuç:**
* **Status:** `201 Created`
* **Body:** Oluşturulan ticket bilgilerini içeren JSON.

---

### Test 7: Ticket Atama (Assign)

**Amaç:** Destek uzmanının ticket’ı kendisine ataması.

1. `Assign Ticket` adlı yeni request.
2. **Method:** `PUT`
3. **URL:** `http://localhost:8081/api/tickets/assign`
4. **Headers:**
   * `Authorization: Bearer {{jwt}}`  *(SUPPORT_AGENT veya ADMIN rolü olan kullanıcıya ait token olmalı)*
5. **Body** (`raw` / `JSON`):
   ```json
   {
     "ticketId": 10,
     "newAssignedTo": 5,
     "reason": "Çağrıyı ben üstleniyorum."
   }
   ```
6. **Send**

**Beklenen Sonuç:** `200 OK` ve `assignedTo` alanı güncellenmiş ticket JSON’u.

---

### Test 8: Durum Güncelleme

**Amaç:** Ticket durumunu `İşlemde` olarak güncellemek.

1. `Update Ticket Status` request.
2. **Method:** `PUT`
3. **URL:** `http://localhost:8081/api/tickets/status`
4. **Headers:** `Authorization: Bearer {{jwt}}`
5. **Body** (`JSON`):
   ```json
   {
     "ticketId": 10,
     "newStatus": "İşlemde",
     "reason": "Çalışmalara başlandı."
   }
   ```
6. **Beklenen:** `200 OK`, `status` alanı `İşlemde` döner.

---

### Test 9: Müşteriye Ait Ticketları Listeleme

**Amaç:** Belirli müşterinin tüm ticketlarını çekmek.

1. `List Tickets by Customer` request.
2. **Method:** `GET`
3. **URL:** `http://localhost:8081/api/tickets?customerId=1`
4. **Headers:** `Authorization: Bearer {{jwt}}`

**Beklenen Sonuç:** `200 OK`, JSON dizisi şeklinde ticket listesi. 