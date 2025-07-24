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