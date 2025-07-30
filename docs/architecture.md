# CRM Platformu – Teknik Mimari

---

> **Belge Durumu**: v1 – {{date}} tarihine kadar yapılan görüşmelere dayanan ilk taslak. Mimari kararlar değiştikçe bu dosyayı güncelleyin.

## 1. Vizyon ve Kapsam
Şirket içi personelin (USER) ve şirket dışı müşterilerin (CUSTOMER) destek taleplerini yönetmek için mikro-servis mimarisinde reaktif (non-blocking) CRM platformu.

• Personel: JWT tabanlı kimlik doğrulama / refresh-token, iç talepler oluşturma & yönetme.  
• Müşteri: Kayıt olmadan ticket açma, ticket durumunu e-posta ile takip edebilme.  
• Tamamen Docker (Docker Desktop + docker-compose) üzerinde geliştirilebilir, MS SQL veritabanı kullanır.  
• Veri erişimi, servisler arası iletişim ve HTTP katmanı Spring WebFlux + R2DBC ile reaktif.

## 2. Mikroservis Topolojisi (V1)
| Servis | Port (geliştirici) | Sorumluluklar |
|--------|-------------------|----------------|
| **gateway-service** | 8080 | Tek giriş noktası, path tabanlı yönlendirme, JWT doğrulama, CORS, rate-limit |
| **user-service** | 8081 | Giriş/Kayıt, JWT & refresh token üretimi, kullanıcı kimliği (`user` tablosu) |
| **department-service** | 8082 | Departman hiyerarşisi & Person profili CRUD (`department`, `person` tabloları) |
| **ticket-service** | 8083 | Ticket ve TicketAssignments yönetimi, kural tabanlı ilk atama |

> **Not**: notification-service, file-service vb. sonraki sürümlerde eklenebilir.

## 3. Katmanlı Yapı (Her Servis)
```
|— api (REST Controllers, DTOs)
|— service (Business use-cases)
|— data (ReactiveRepositories, Entities)
|— communication (WebClient çağrıları)
|— infrastructure (configurations, Flyway, logging)
|— security (JWT, method security)
```

## 4. Veri Modeli (Mantıksal)
### 4.1 Kullanıcı Alanı (user-service)
```
user (user_id PK)
  • user_name      : varchar, UNIQUE
  • password_hash  : varchar (BCrypt-10)
  • person_id      : FK → department-service.person.person_id
  • refresh_token_hash       : varchar  -- tek aktif token
  • refresh_token_expires_at : datetime
```

### 4.2 Organizasyon Alanı (department-service)
```
person (person_id PK)
  • name, email, phone, surname
  • department_id : FK → department.department_id
  • isActive      : bit

department (department_id PK)
  • name
  • parent_department_id : FK nullable (root = NULL)
  • isActive            : bit
  • isDeleted           : bit
  • deleted_UserId      : bigint nullable
  • isUpdated           : bit
  • updated_UserId      : bigint nullable
```

### 4.3 Müşteri ve Ticket Alanı (ticket-service)
```
customer (customer_id PK)
  • email        : varchar, UNIQUE
  • name, surname, phone

 tickets (ticket_id PK)
  • issue            : text
  • priority         : enum {LOW, MEDIUM, HIGH, CRITICAL}
  • isActive         : bit
  • created_date     : datetime
  • closed_date      : datetime nullable
  • isEmployee       : bit  -- TRUE if created by USER
  • creater_id       : FK → user.user_id OR customer.customer_id (polymorphic – handled at code level)

 ticket_assignments (assignment_id PK)
  • ticket_id     : FK → tickets.ticket_id
  • status        : enum {UNASSIGNED, ASSIGNED, IN_PROGRESS, COMPLETED}
  • is_in_pool    : bit
  • department_id : FK → department.department_id
  • person_id     : FK → person.person_id nullable
  • assignedDate  : datetime
  • completedDate : datetime nullable
```

## 5. İş Akışları
### 5.1 Kimlik Doğrulama Akışı
1. USER `POST /api/auth/login` uç noktasına `user_name` + şifre ile istek gönderir.  
2. user-service BCrypt hash’i doğrular; **Access-Token** (15 dk) ve **Refresh-Token** (7 gün) üretir.  
3. Refresh-token hash’i `user.refresh_token_hash` alanına yazılır; varsa eski hash üzerine yazılır.  
4. Gateway her istekte access token’ı (HMAC-SHA256) doğrular.  
5. `POST /api/auth/refresh` çağrısı sırasında:  
   – eski hash doğrulanır  
   – yeni hash & sona erme tarihi yazılır  
   – yeni access & refresh token’lar döndürülür.

### 5.2 Ticket Oluşturma (Çalışan)
```
USER → gateway → ticket-service /tickets
└─ Validate JWT (ROLE_USER)
   └─ Create ticket (status=OPEN, isEmployee=true, creater_id=userId)
      └─ RuleComponent determines initial department and writes first ticket_assignment
```

### 5.3 Ticket Oluşturma (Müşteri)
```
CLIENT (public UI) → gateway → ticket-service /tickets/public
└─ Validates reCAPTCHA (future) → Looks up customer.email
   └─ Existing customer? reuse id : create new customer
      └─ Create ticket (isEmployee=false)
         └─ Same RuleComponent assigns pool department
```

### 5.4 Ticket Yaşam Döngüsü
```
OPEN → IN_PROGRESS → RESOLVED → CLOSED
```
Geçişler, ticket-service içindeki `StateMachine` bileşeni tarafından method-level security ile zorlanır.

## 6. Enumlar
| Enum | Values |
|------|--------|
| **Priority** | LOW, MEDIUM, HIGH, CRITICAL |
| **TicketStatus** | OPEN, IN_PROGRESS, RESOLVED, CLOSED |
| **AssignmentStatus** | UNASSIGNED, ASSIGNED, IN_PROGRESS, COMPLETED |

## 7. Güvenlik ve Yetkilendirme
• Roller: `ROLE_USER`, `ROLE_CUSTOMER`  
• Controller metotları `@PreAuthorize("hasRole('USER')")` vb. anotasyonlarla korunur.  
• Şifre saklama: BCrypt strength = 10.  
• JWT: HMAC-SHA256 gizli anahtarı `${JWT_SECRET}` ortam değişkeninde saklanır.  
• V1 sürümünde admin rolü yoktur; gerekirse ileride eklenir.

## 8. Reaktif Teknoloji Yığını
| Katman | Teknoloji |
|---------|------------|
| HTTP Katmanı | Spring WebFlux (Netty) |
| DB Erişimi | Spring Data R2DBC + r2dbc-mssql |
| Servisler arası Çağrılar | `WebClient` (non-blocking) |
| Dayanıklılık | Resilience4j (circuit-breaker, retry) |

## 9. Veritabanı Göçleri
Flyway her servis için entegre (`db/migration`).  
Sürüm dosyaları: `V1__initial_schema.sql` …

## 10. Docker ve Lokal Geliştirme
```
docker-compose.yml
 ├─ mssql (1433)
 ├─ gateway-service  (8080)
 ├─ user-service     (8081)
 ├─ department-service (8082)
 └─ ticket-service   (8083)
```
• Servisler geliştirme ortamında hot-reload için `target/*.jar` dosyalarını mount eder.  
• `.env` dosyası veritabanı kimlik bilgileri ve JWT sırrını içerir.  

## 11. Loglama
• SLF4J + Logback; JSON formatlı loglar konsola (Docker) yazılır.  
• V1’de dağıtık izleme yok; ileride OpenTelemetry + Zipkin eklenebilir.

## 12. Gelecek Yol Haritası (V2+)
1. notification-service (email/SMS)  
2. file-service (attachments, MinIO/S3)  
3. Analytics / reporting  
4. Multi-department membership  
5. Multi-device refresh tokens (move to separate table)  
6. Public key JWT (RSA) & JWKS endpoint

---

**Belgenin Sonu** 