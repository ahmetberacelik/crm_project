# CRM Veritabanı Şeması

Bu doküman, CRM uygulamasının veritabanı şemasını detaylı olarak açıklamaktadır. Şema, `docs` klasöründe bulunan PlantUML (`updated_ticket_database_er.puml`) dosyası ile görselleştirilmiş ER diyagramını temel alır ve `user-service` için gereken kimlik doğrulama alanlarını içerecek şekilde genişletilmiştir.

## Şema Değişiklik Notları

`user-service`'in hem müşteri (`Customer`) hem de personel (`User`/`Admin`) kimlik doğrulama (login/register) işlevselliğini desteklemesi için orijinal ER şemasına aşağıdaki alanlar eklenmiştir:

-   **`Customers` Tablosu:**
    -   `password_hash`: Müşteri parolalarının BCrypt gibi algoritmalarla hash'lenerek güvenli bir şekilde saklanması için eklenmiştir.
    -   `role`: Rol bazlı yetkilendirme için eklenmiştir. Bu tablodaki kullanıcılar için varsayılan değer `'CUSTOMER'` olacaktır.

-   **`Users` Tablosu:**
    -   `email`: Personelin sisteme giriş yapabilmesi için benzersiz bir e-posta alanı eklenmiştir.
    -   `password_hash`: Personel parolalarının güvenli bir şekilde saklanması için eklenmiştir.
    -   `role`: Personel rolünü (örn: `'ADMIN'`, `'SUPPORT_AGENT'`) belirtmek için eklenmiştir.

---

## Tablo Detayları

### 1. `Customers`

Müşteri bilgilerini saklar. Müşteriler sisteme kayıt olabilir, giriş yapabilir ve destek talepleri (ticket) oluşturabilirler.

| Kolon Adı       | Veri Tipi      | Kısıtlamalar                        | Açıklama                                                               |
| :-------------- | :------------- | :---------------------------------- | :--------------------------------------------------------------------- |
| `customer_id`   | `BIGINT`       | `PK`, `AUTO_INCREMENT`              | Benzersiz müşteri kimliği.                                             |
| `name`          | `VARCHAR(255)` | `NOT NULL`                          | Müşterinin tam adı.                                                    |
| `email`         | `VARCHAR(255)` | `UNIQUE`, `NOT NULL`                | Müşterinin e-posta adresi ve kullanıcı adı.                            |
| `phone`         | `VARCHAR(20)`  | `NULLABLE`                          | Müşterinin telefon numarası.                                           |
| **`password_hash`** | **`VARCHAR(255)`** | **`NOT NULL`**                      | Müşterinin hash'lenmiş parolası.                                       |
| **`role`**          | **`VARCHAR(50)`**  | **`NOT NULL`, `DEFAULT 'CUSTOMER'`**  | Kullanıcı rolü.                                                        |
| `created_at`    | `DATETIME`     | `DEFAULT CURRENT_TIMESTAMP`         | Müşteri kaydının oluşturulduğu zaman damgası.                          |

---

### 2. `Users`

CRM sistemini yöneten şirket içi personelin (Admin, destek uzmanı vb.) bilgilerini saklar.

| Kolon Adı         | Veri Tipi      | Kısıtlamalar           | Açıklama                                                              |
| :---------------- | :------------- | :--------------------- | :-------------------------------------------------------------------- |
| `user_id`         | `BIGINT`       | `PK`, `AUTO_INCREMENT` | Benzersiz personel kimliği.                                           |
| `name`            | `VARCHAR(255)` | `NOT NULL`             | Personelin tam adı.                                                   |
| **`email`**       | **`VARCHAR(255)`** | **`UNIQUE`, `NOT NULL`** | Personelin e-posta adresi ve kullanıcı adı.                           |
| **`password_hash`** | **`VARCHAR(255)`** | **`NOT NULL`**         | Personelin hash'lenmiş parolası.                                      |
| **`role`**        | **`VARCHAR(50)`**  | **`NOT NULL`**         | Personelin rolü (örn: 'ADMIN', 'SENIOR_SUPPORT', 'JUNIOR_SUPPORT').   |
| `department`      | `VARCHAR(50)`  | `NOT NULL`             | Personelin bağlı olduğu departman (örn: 'BT', 'Finans').              |
| `expertise_level` | `VARCHAR(50)`  | `NULLABLE`             | Personelin uzmanlık seviyesi (örn: 'Senior', 'Junior').               |

---

### 3. `Pools`

Destek taleplerinin (ticket) yönlendirildiği departman havuzlarını tanımlar.

| Kolon Adı    | Veri Tipi      | Kısıtlamalar           | Açıklama                                               |
| :----------- | :------------- | :--------------------- | :----------------------------------------------------- |
| `pool_id`    | `BIGINT`       | `PK`, `AUTO_INCREMENT` | Benzersiz havuz kimliği.                               |
| `name`       | `VARCHAR(255)` | `NOT NULL`             | Havuzun adı (örn: 'BT Destek Havuzu').                 |
| `department` | `VARCHAR(50)`  | `NOT NULL`             | Havuzun ait olduğu departman (örn: 'BT', 'Finans').    |

---

### 4. `Support_Tickets`

Müşteriler tarafından oluşturulan destek taleplerinin (ticket) ana kaydını tutar.

| Kolon Adı     | Veri Tipi      | Kısıtlamalar                               | Açıklama                                                                |
| :------------ | :------------- | :----------------------------------------- | :---------------------------------------------------------------------- |
| `ticket_id`   | `BIGINT`       | `PK`, `AUTO_INCREMENT`                     | Benzersiz ticket kimliği.                                               |
| `customer_id` | `BIGINT`       | `FK` -> `Customers(customer_id)`           | Ticket'ı oluşturan müşteri.                                             |
| `issue`       | `TEXT`         | `NOT NULL`                                 | Müşterinin sorun açıklaması.                                            |
| `category`    | `VARCHAR(100)` | `NULLABLE`                                 | Ticket kategorisi (örn: 'Yazılım Hatası', 'Fatura Sorunu').             |
| `priority`    | `VARCHAR(50)`  | `DEFAULT 'Orta'`                           | Ticket'ın öncelik seviyesi (örn: 'Düşük', 'Orta', 'Yüksek', 'Acil').    |
| `status`      | `VARCHAR(50)`  | `DEFAULT 'Açık'`                           | Ticket'ın güncel durumu (örn: 'Açık', 'İşlemde', 'Kapalı').             |
| `pool_id`     | `BIGINT`       | `FK` -> `Pools(pool_id)`                   | Ticket'ın atandığı departman havuzu.                                    |
| `assigned_to` | `BIGINT`       | `FK` -> `Users(user_id)`, `NULLABLE`         | Ticket'ın atandığı spesifik personel.                                   |
| `created_at`  | `DATETIME`     | `DEFAULT CURRENT_TIMESTAMP`                | Ticket'ın oluşturulduğu zaman damgası.                                  |

---

### 5. `Ticket_History`

Bir ticket üzerinde yapılan tüm atama ve durum değişikliklerinin geçmişini (audit trail) tutar.

| Kolon Adı              | Veri Tipi | Kısıtlamalar                           | Açıklama                                       |
| :--------------------- | :-------- | :------------------------------------- | :--------------------------------------------- |
| `history_id`           | `BIGINT`  | `PK`, `AUTO_INCREMENT`                 | Benzersiz geçmiş kaydı kimliği.                |
| `ticket_id`            | `BIGINT`  | `FK` -> `Support_Tickets(ticket_id)`   | İlişkili ticket.                               |
| `previous_pool_id`     | `BIGINT`  | `FK` -> `Pools(pool_id)`, `NULLABLE`   | Önceki departman havuzu.                       |
| `new_pool_id`          | `BIGINT`  | `FK` -> `Pools(pool_id)`, `NULLABLE`   | Yeni departman havuzu.                         |
| `previous_assigned_to` | `BIGINT`  | `FK` -> `Users(user_id)`, `NULLABLE`   | Önceki atanan personel.                        |
| `new_assigned_to`      | `BIGINT`  | `FK` -> `Users(user_id)`, `NULLABLE`   | Yeni atanan personel.                          |
| `reason`               | `TEXT`    | `NULLABLE`                             | Değişikliğin nedeni (örn: "Yanlış atama").     |
| `changed_at`           | `DATETIME`| `DEFAULT CURRENT_TIMESTAMP`            | Değişikliğin yapıldığı zaman damgası.          |

---

### 6. `Notifications`

Ticket süreciyle ilgili (oluşturma, atama, kapatma vb.) tüm bildirimlerin kaydını tutar.

| Kolon Adı         | Veri Tipi      | Kısıtlamalar                         | Açıklama                                                |
| :---------------- | :------------- | :----------------------------------- | :------------------------------------------------------ |
| `notification_id` | `BIGINT`       | `PK`, `AUTO_INCREMENT`               | Benzersiz bildirim kimliği.                             |
| `ticket_id`       | `BIGINT`       | `FK` -> `Support_Tickets(ticket_id)` | İlişkili ticket.                                        |
| `message`         | `TEXT`         | `NOT NULL`                           | Bildirim mesajının içeriği.                             |
| `sent_to`         | `VARCHAR(255)` | `NOT NULL`                           | Bildirimin gönderildiği adres (e-posta, telefon vb.).    |
| `created_at`      | `DATETIME`     | `DEFAULT CURRENT_TIMESTAMP`          | Bildirimin gönderildiği zaman damgası.                  | 