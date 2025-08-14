Teknoloji seti

UI: React 18 + Vite + TypeScript
Router: React Router v6
Data: Tanstack Query (React Query) + Axios
Durum: Zustand
UI kütüphanesi: MUI
Form/Validasyon: react-hook-form + Zod
Build/CI: Vite, env ile servis URL’leri
Proje türü: Düz React SPA
Önce authentication ekranı
interceptor ile refresh otomasyonu

frontend/
  .env
  index.html
  package.json
  tsconfig.json
  vite.config.ts

  src/
    main.tsx
    App.tsx

    app/
      providers/
        QueryProvider.tsx
        ThemeProvider.tsx
      router/
        index.tsx            # BrowserRouter + AppRoutes
        routes.tsx           # Tüm route tanımları
        guards/
          AuthGuard.tsx
          RoleGuard.tsx

    config/
      env.ts                 # VITE_* okuma (USER/DEPARTMENT/TICKET API URL)
      constants.ts           # Sabitler (sayfalama varsayılanları vb.)

    shared/
      components/
        Layout/
          AppLayout.tsx
          Header.tsx
          Sidebar.tsx
      hooks/
        useAuth.ts
        usePagination.ts
      libs/
        axios/
          axiosClient.ts     # baseURL, headers
          authInterceptor.ts # 401 → refresh otomasyonu
        query/
          queryClient.ts
          queryKeys.ts
      stores/
        auth.store.ts        # accessToken, refreshToken, userId, roles
      types/
        auth.ts
        ticket.ts
        department.ts
        category.ts
        person.ts
      utils/
        storage.ts           # localStorage erişimi
        jwt.ts               # token decode (yalnızca client-side ihtiyaçları)
      validation/
        auth.schema.ts
        ticket.schema.ts
        publicTicket.schema.ts

    features/
      auth/
        pages/
          LoginPage.tsx
        components/
          LoginForm.tsx
        api/
          auth.api.ts        # /api/auth/login, refresh, logout
        validation/
          login.schema.ts
      public/
        pages/
          CreatePublicTicketPage.tsx
        components/
          PublicTicketForm.tsx
        api/
          public.api.ts      # GET /api/public/categories, POST /api/tickets/public
      tickets/
        pages/
          CreateEmployeeTicketPage.tsx
          TicketDetailPage.tsx
          MyAssignmentsPage.tsx       # Kullanıcının üstlendikleri (sayfalama/sıralama)
          DepartmentPoolPage.tsx      # Kendi departman havuzu (token’dan dept)
        components/
          TicketForm.tsx
          TicketList.tsx
        api/
          tickets.api.ts      # POST /api/tickets, GET /api/tickets/{id}, claim/close/reassign, listeler
      admin/
        pages/
          CategoriesPage.tsx
        components/
          CategoryForm.tsx
        api/
          categories.api.ts    # /api/management/categories

    pages/
      HomePage.tsx
      NotFoundPage.tsx

    assets/
      images/
      icons/

    styles/
      theme.ts
      globals.css

  public/
    favicon.svg

    Env değişkenleri: VITE_USER_API_URL, VITE_DEPARTMENT_API_URL, VITE_TICKET_API_URL

Interceptor: shared/libs/axios/authInterceptor.ts

Query: shared/libs/query/{queryClient,queryKeys}.ts

Zustand: shared/stores/auth.store.ts (userId=sub, roles; personId==userId)

Listeleme varsayılanları: ?page=0&size=20&sort=createdDate,desc

Pool: Departman token’dan türetilecek (UI tarafında dept param gerekmez)