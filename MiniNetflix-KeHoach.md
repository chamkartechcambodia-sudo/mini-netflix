# Kế hoạch dự án giảng dạy: Mini Netflix

> Dự án thay thế cho bài Udacity "Mars Real Estate" (Connect to the Internet).
> Mục tiêu **không chỉ** là dạy "Kết nối Internet & tiêu thụ REST API" trong Android —
> mà là đào tạo **engineer biết ship sản phẩm**: cuối khóa SV có 1 app cài được trên
> máy thật, 1 repo GitHub đẹp, và biết quản lý dự án ở mức cơ bản.

---

## 1. Triết lý dạy

> **1 buổi = 1 Sprint = 1 Feature đầy đủ + 1 Engineering Practice + 1 Deliverable đo được**

- Khóa học **KHÔNG** phải là "học gõ code Android" — mà là **xây 1 sản phẩm thật**.
- Mỗi sprint kết thúc bằng 1 thứ SV có thể đem khoe (PR merged, screenshot, GIF).
- **Git/GitHub/PR/Wireframe bắt đầu từ Sprint 6** — không dồn thành module lý thuyết cuối khóa.
- Theo nguyên tắc "deliver complete features" — mỗi phase = bản hoàn chỉnh của tính năng (không phải bản tối giản để bổ sung sau).

---

## 2. Mục tiêu học tập

### 2a. Kỹ năng kỹ thuật (giữ đúng tinh thần "Connect to the Internet")

| # | Mục tiêu | Thể hiện trong Mini Netflix |
|---|---|---|
| 1 | Hiểu REST / HTTP GET, JSON | Gọi `movie/popular`, đọc JSON thật của TMDB |
| 2 | Retrofit tạo network layer | `TmdbApiService` với `@GET`, `@Query` |
| 3 | Parse JSON → object Kotlin (Moshi reflection) | `Movie`, `MovieResponse`, `Video`, `VideoResponse` |
| 4 | Coroutines cho tác vụ bất đồng bộ | `viewModelScope.launch` + `suspend fun` + `async`/`awaitAll` (Sprint 7) |
| 5 | Xử lý trạng thái mạng | enum `TmdbApiStatus { LOADING, ERROR, DONE }` |
| 6 | RecyclerView dạng lưới + ngang + DiffUtil | `GridLayoutManager` (S3) → 4 hàng `LinearLayoutManager` ngang (S7) |
| 7 | Tải ảnh từ URL với Glide | Ghép URL `https://image.tmdb.org/t/p/{size}` |
| 8 | View Binding (KHÔNG Data Binding — AGP 9) | Mọi Fragment dùng `*Binding.inflate` |
| 9 | Navigation Component + Java SafeArgs | Truyền `Movie` (Serializable) sang Detail |
| 10 | Local DB (Room) | Sprint 9 — My List ❤ |
| 11 | Quyền INTERNET | `<uses-permission>` trong Manifest |
| 12 | **Bảo mật API key** | `local.properties` → `BuildConfig.TMDB_API_KEY` |
| 13 | **Ghép URL ảnh** | `base_url` + `size` + `poster_path` |
| 14 | **Intent (ACTION_VIEW)** | Sprint 6 — mở YouTube |
| 15 | **Secondary fetch trong Fragment** | Sprint 6 — Detail tự gọi API videos |

### 2b. Kỹ năng nghề (engineering practice — MỚI)

| # | Kỹ năng | Sprint khai trương |
|---|---|---|
| E1 | Git: branch, commit message format, push | Sprint 6 |
| E2 | GitHub: tạo repo, README, PR workflow | Sprint 6 |
| E3 | Pull Request: description có screenshot/GIF + wireframe link | Sprint 6 |
| E4 | Code review tay-2 (peer review, viết comment có ý nghĩa) | Sprint 7 |
| E5 | Wireframe trước khi code (giấy → Figma) | Sprint 6 (kickoff), Sprint 8 (chính thức) |
| E6 | GitHub Issues (`Fixes #N` trong PR) | Sprint 9 |
| E7 | ADR (Architecture Decision Record) ngắn | Sprint 9 |
| E8 | Semver + CHANGELOG + signed APK | Sprint 10 (capstone, tùy chọn) |

---

## 3. Công nghệ đã chốt (KHÓA theo ràng buộc lớp + AGP 9)

> Giữ stack quen thuộc với SV cơ bản: **KHÔNG** Compose, **KHÔNG** StateFlow, **KHÔNG** Hilt.

- **UI:** XML layouts + ConstraintLayout / FrameLayout / LinearLayout
- **Binding:** **View Binding** (Data Binding không hoạt động trên AGP 9 built-in Kotlin)
- **State:** `LiveData` / `MutableLiveData`
- **Async:** Kotlin Coroutines (`viewModelScope`, `async`, `awaitAll` cho Sprint 7)
- **Network:** Retrofit 2 + Moshi **reflection** (`moshi-kotlin` + `KotlinJsonAdapterFactory` — không ksp/codegen)
- **Ảnh:** Glide (gọi inline trong ViewHolder, không Binding Adapter)
- **List:** RecyclerView + `ListAdapter` + `DiffUtil`
- **Điều hướng:** Navigation Component + **Java SafeArgs** (`androidx.navigation.safeargs`, **không** `.kotlin`); Navigation **≥ 2.9.6** trên AGP 9
- **Object passing:** `Serializable` (KHÔNG `@Parcelize` — `kotlin-parcelize` không ổn trên built-in Kotlin)
- **Local DB:** Room (Sprint 9)
- **Bảo mật key:** `local.properties` + `buildConfigField`
- **Build script:** Kotlin DSL (`build.gradle.kts`)
- **Quản lý thư viện:** Version Catalog (`gradle/libs.versions.toml`)
- **Môi trường:** AGP **9.0.1**, Gradle **9.2.1**, `compileSdk 36.1`, Java 11, built-in Kotlin (không cần plugin `kotlin-android`)

---

## 4. TMDB API

- **Base URL:** `https://api.themoviedb.org/3/`
- **Ảnh:** `https://image.tmdb.org/t/p/{size}` + `poster_path` / `backdrop_path` (size: `w185 / w342 / w500 / w780 / original`)
- **Auth:** API Key v3 qua `?api_key=...` (cả lớp dùng chung 1 key do GV cấp)
- **Ngôn ngữ:** `&language=en-US` (vì SV không đọc tiếng Việt)

### Endpoint sử dụng theo Sprint

| Endpoint | Sprint | Mục đích |
|---|---|---|
| `GET movie/popular` | 1 | Lưới phim đầu tiên |
| `GET movie/{id}/videos` | 6 | Lấy trailer YouTube `key` |
| `GET movie/popular` | 7 | Hàng "Popular" trên Home Netflix-style |
| `GET movie/top_rated` | 7 | Hàng "Top Rated" |
| `GET movie/now_playing` | 7 | Hàng "Now Playing" |
| `GET movie/upcoming` | 7 | Hàng "Upcoming" |
| `GET search/movie?query=...` | 8 | Tìm kiếm phim |
| (không có endpoint mới) | 9 | My List lưu local bằng Room |

### Lưu ý dữ liệu (từ JSON thật TMDB)
- `results[]` chứa phim; mỗi phim có `poster_path` / `backdrop_path` tương đối → **phải ghép URL**.
- `vote_average` là `Double` → format 1 chữ số thập phân khi hiển thị (`★ %.1f`).
- `release_date` dạng `YYYY-MM-DD` → lấy 4 ký tự đầu để hiển thị năm.
- `genre_ids` là mảng số ID — bỏ qua hoặc dạy ở khóa nâng cao.
- Field thừa (`adult`, `video`, `popularity`…) cứ bỏ qua — Moshi tự bỏ field không khai báo.
- `videos.results[]`: lọc `site == "YouTube"` && `type == "Trailer"`, ưu tiên `official == true`. Mở `https://www.youtube.com/watch?v={key}`.

---

## 5. Kiến trúc (MVVM, đơn giản)

```
ViewModel  ──>  Network layer (Retrofit/TmdbApi)
   │             └── (Sprint 9) Room DAO cho My List
   │
   └── LiveData ──(observe trong Fragment)──> View Binding → XML
```

Không có Repository / Hilt / use-case ở khóa cơ bản. Stack tối giản, dễ trace.

### Cấu trúc package (`com.example.android.mininetflix`)

```
mininetflix/
├── MainActivity.kt                      # Chỉ host NavGraph
├── network/
│   ├── TmdbApiService.kt                # Retrofit + object TmdbApi
│   ├── Movie.kt                         # Serializable
│   ├── MovieResponse.kt
│   ├── Video.kt                         # Sprint 6
│   └── VideoResponse.kt                 # Sprint 6
├── overview/
│   ├── OverviewFragment.kt              # Sprint 7 → Netflix Home
│   ├── OverviewViewModel.kt             # Sprint 7 → 4 LiveData song song
│   └── MoviePosterAdapter.kt            # Tái dùng cho cả 4 hàng
├── detail/
│   └── DetailFragment.kt                # Sprint 6 thêm Play Trailer button
├── search/                              # Sprint 8
│   ├── SearchFragment.kt
│   └── SearchViewModel.kt
└── mylist/                              # Sprint 9
    ├── MyListFragment.kt
    ├── MyListViewModel.kt
    ├── data/
    │   ├── FavoriteMovie.kt             # @Entity
    │   ├── FavoriteDao.kt
    │   └── AppDatabase.kt
    └── ...
```

---

## 6. Các màn hình & tính năng

### Màn 1 — Home (Sprint 7: Netflix-style)
- **Hero card** trên cùng: backdrop của phim phổ biến #1 + tiêu đề overlay + tap → Detail.
- **4 hàng ngang** có nhãn (white bold): Popular · Top Rated · Now Playing · Upcoming.
- Mỗi hàng = `RecyclerView` ngang dùng `MoviePosterAdapter` (tái dùng).
- **Dark theme**: nền đen, accent đỏ Netflix `#E50914`.
- Kết cấu: `NestedScrollView` chứa hero + 4 RecyclerView ngang (đơn giản cho SV; gợi mở nested-RecyclerView như "real-world upgrade").

### Màn 2 — Detail (Sprint 5 ✅ + Sprint 6 mở rộng)
- Hero backdrop + gradient scrim + tiêu đề overlaid.
- Meta row: ★ rating đỏ · năm phát hành.
- Section OVERVIEW + mô tả.
- **Sprint 6**: thêm nút ▶ **Play Trailer** → Intent mở YouTube.
- **Sprint 9**: thêm icon ❤ Toggle My List.

### Màn 3 — Search (Sprint 8)
- `SearchView` trên toolbar (hoặc top bar).
- Debounce 300ms trước khi gọi API.
- Empty state khi chưa gõ; loading khi đang search; "Không tìm thấy" khi rỗng.

### Màn 4 — My List (Sprint 9)
- Lưới giống Home grid (RecyclerView `GridLayoutManager` 2 cột).
- Đọc từ Room qua `LiveData<List<FavoriteMovie>>`.
- Empty state "Chưa có phim nào — hãy thả ❤ ở màn Detail".

---

## 7. Lộ trình 10 Sprint

> Đã làm 6 sprint (0–5). Sprint 6 là điểm bắt đầu **engineering practice** chính thức.

| # | Sprint | Trạng thái | Feature | Practice (mới) | Deliverable |
|---|---|---|---|---|---|
| 0 | Project Kickoff | ✅ | Tạo project, dependency, INTERNET permission, API key trong `local.properties` → `BuildConfig` | — | App rỗng compile được |
| 1 | Network | ✅ | `Movie`, `MovieResponse`, `TmdbApiService`, gọi `getPopular()` trong ViewModel, log 20 tiêu đề | — | Logcat in ra 20 phim |
| 2 | Loading/Error state | ✅ | enum `TmdbApiStatus` + try/catch trong coroutine | — | `_status` chuyển LOADING→DONE/ERROR đúng |
| 3 | Poster Grid | ✅ | `MoviePosterAdapter` (`ListAdapter`+DiffUtil) + Glide trong ViewHolder + GridLayoutManager span 2 | — | Lưới poster hiện trên máy thật |
| 4 | Loading/Error UI | ✅ | ProgressBar + icon `ic_connection_error` ẩn/hiện theo `status` | — | Test airplane mode → icon lỗi |
| 5 | Detail Screen | ✅ | Navigation + SafeArgs + hero backdrop + scrim + meta row + OVERVIEW (Netflix-style) | — | Tap poster → màn Detail đầy đủ |
| **6** | **Play Trailer + Git/Wireframe Kickoff** | ⏳ TIẾP THEO | `Video`/`VideoResponse` + `getMovieVideos(id)` + nút ▶ Play Trailer trên Detail + Intent mở YouTube | 🟢 **Open repo GitHub + first PR + wireframe** (mini-lesson 20–25 phút đầu sprint) | Repo GitHub có lịch sử commit + PR `feat: play trailer` được merge + ảnh wireframe trong PR |
| 7 | **Netflix-style Home** | planned | Hero card + 4 hàng ngang (Popular/Top Rated/Now Playing/Upcoming) + dark theme + `async`/`awaitAll` 4 endpoint song song | **Code review tay-2** (đổi PR với bạn, ≥3 comment có ý nghĩa) | PR có ≥3 review comment + 1 commit `refactor:` xử lý comment |
| 8 | Search | planned | `search/movie` + SearchView + debounce 300ms + empty/loading/no-result states | **Wireframe-first**: Figma → PR description | PR có Figma link + GIF demo |
| 9 | My List (Room) | planned | `FavoriteMovie` entity + DAO + AppDatabase + icon ❤ toggle trên Detail + màn My List | **GitHub Issues**: mở 2–3 issue/sprint, đóng bằng PR `Fixes #N` + viết **ADR** "vì sao Room" | 3 issue đóng + ADR `docs/adr/001-room-vs-prefs.md` |
| 10 *(capstone)* | Release + Polish | optional | Dark mode toàn app + a11y (contentDescription) + signed APK + ProGuard | **Semver + CHANGELOG + README chuẩn recruiter** | APK ký số cài máy thật + tag `v1.0.0` + README có badges/screenshots |

---

## 8. Engineering Practices (chi tiết)

### Git workflow (chốt từ Sprint 6)
- **1 sprint = 1 branch = 1 PR** (squash merge vào `main`).
- Branch naming: `feat/play-trailer`, `feat/netflix-home`, `fix/parser-npe`, `refactor/viewmodel-split`.
- **Conventional commits**: `feat:` / `fix:` / `refactor:` / `docs:` / `style:` / `chore:`.
- **KHÔNG commit thẳng vào `main`** — kể cả GV.

### Pull Request template
```markdown
## What
1-2 câu mô tả tính năng.

## Why
Tại sao cần (link issue nếu có: Fixes #N).

## How
Ngắn: cách giải quyết, file chính.

## Screenshot / GIF
[ảnh hoặc gif demo]

## Wireframe
[link Figma hoặc ảnh wireframe tay]

## Definition of Done
- [x] Code compile + chạy
- [x] Test tay theo plan
- [x] 0 Lint warning mới
- [x] README cập nhật nếu cần
```

### Code review checklist (Sprint 7+)
SV đọc PR của bạn và bình luận về **ít nhất 3** trong các điểm sau:
1. Naming có rõ nghĩa không?
2. Có hardcode magic number / string không?
3. Có duplicate code có thể tách hàm không?
4. Có thể bị null pointer / crash trong edge case nào không?
5. Có rò rỉ binding/listener không (đặc biệt trong Fragment)?
6. Format có nhất quán với code base không?

### Wireframe process
- **Sprint 6**: vẽ tay trên giấy (chấp nhận).
- **Sprint 8+**: bắt buộc Figma (account free đủ dùng).
- Wireframe **trước** khi code, **không** sau.

### Definition of Done (mọi sprint từ S6)
- ✅ Code merged to `main` via PR (squash merge)
- ✅ PR description có screenshot/GIF
- ✅ PR description có wireframe link
- ✅ README cập nhật nếu có user-facing change
- ✅ 0 Lint warning mới (`./gradlew lint`)

---

## 9. Bảo mật API key (kỹ năng nghề bắt buộc)

`local.properties` (đã được gitignore sẵn):
```properties
TMDB_API_KEY=xxxxxxxxxxxxxxxx
```

`app/build.gradle.kts`:
```kotlin
val localProps = java.util.Properties()
val f = rootProject.file("local.properties")
if (f.exists()) { localProps.load(java.io.FileInputStream(f)) }

android {
    defaultConfig {
        buildConfigField("String", "TMDB_API_KEY", "\"${localProps.getProperty("TMDB_API_KEY", "")}\"")
    }
    buildFeatures {
        viewBinding = true   // KHÔNG dataBinding trên AGP 9
        buildConfig = true
    }
}
```

Dùng trong code: `BuildConfig.TMDB_API_KEY`.
→ Quy tắc: **KHÔNG BAO GIỜ commit API key lên Git.** Trước khi commit lần đầu, mở `.gitignore` xem có `local.properties` chưa.

---

## 10. Sketch code chính (đã chốt từ JSON thật)

```kotlin
// network/Movie.kt — Serializable (KHÔNG @Parcelize trên AGP 9 built-in Kotlin)
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "release_date") val releaseDate: String?
) : java.io.Serializable

// network/MovieResponse.kt
data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    @Json(name = "total_pages") val totalPages: Int
)

// network/Video.kt (Sprint 6)
data class Video(
    val id: String,
    val key: String,           // YouTube video ID
    val site: String,          // "YouTube"
    val type: String,          // "Trailer"
    val official: Boolean
)

// network/VideoResponse.kt (Sprint 6)
data class VideoResponse(
    val id: Int,
    val results: List<Video>
)

// network/TmdbApiService.kt
interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopular(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieResponse

    // Sprint 6
    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): VideoResponse

    // Sprint 7
    @GET("movie/top_rated")    suspend fun getTopRated(@Query("api_key") apiKey: String, ...): MovieResponse
    @GET("movie/now_playing")  suspend fun getNowPlaying(@Query("api_key") apiKey: String, ...): MovieResponse
    @GET("movie/upcoming")     suspend fun getUpcoming(@Query("api_key") apiKey: String, ...): MovieResponse

    // Sprint 8
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieResponse
}
```

```kotlin
// Sprint 6 — Play Trailer (5 dòng cốt lõi)
val videos = TmdbApi.retrofitService.getMovieVideos(movie.id, BuildConfig.TMDB_API_KEY)
val trailer = videos.results.firstOrNull { it.site == "YouTube" && it.type == "Trailer" && it.official }
    ?: videos.results.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }
trailer?.let {
    startActivity(Intent(Intent.ACTION_VIEW, "https://www.youtube.com/watch?v=${it.key}".toUri()))
}
```

```kotlin
// Sprint 7 — Fetch 4 endpoint song song
viewModelScope.launch {
    val key = BuildConfig.TMDB_API_KEY
    val popular   = async { TmdbApi.retrofitService.getPopular(key) }
    val topRated  = async { TmdbApi.retrofitService.getTopRated(key) }
    val nowPlaying= async { TmdbApi.retrofitService.getNowPlaying(key) }
    val upcoming  = async { TmdbApi.retrofitService.getUpcoming(key) }
    _popular.value    = popular.await().results
    _topRated.value   = topRated.await().results
    _nowPlaying.value = nowPlaying.await().results
    _upcoming.value   = upcoming.await().results
}
```

---

## 11. Rủi ro & gotcha môi trường (AGP 9 — học được trong Phase 5)

### Phải nhớ
1. **Data Binding KHÔNG hoạt động** trên AGP 9 built-in Kotlin (thiếu kapt) → dùng **View Binding** mọi nơi.
2. **`kotlin-parcelize` rủi ro** trên built-in Kotlin → dùng **`Serializable`** cho object truyền qua SafeArgs.
3. **SafeArgs dùng biến thể Java** (`androidx.navigation.safeargs`, KHÔNG `.kotlin`).
4. **Moshi chỉ dùng reflection** (`moshi-kotlin` + `KotlinJsonAdapterFactory`) — KHÔNG ksp codegen.
5. **Navigation phải ≥ 2.9.6** trên AGP 9 — bản cũ hơn (2.8.x) lỗi `safeargs plugin must be used with android plugin` khi Sync. Project đang pin `navigation = "2.9.8"`.
6. **`FragmentContainerView` KHÔNG được làm layout root** — View Binding parser bị NPE `Cannot read field "elmName" because "root" is null`. **Phải bọc trong layout cha** (FrameLayout / ConstraintLayout). Đã sửa `activity_main.xml`.
7. **CLI build sandbox không tải được artifact mới** — chỉ build được khi user đã Sync trong Android Studio. Sau Sync, có thể chạy `gradlew :app:mergeDebugResources --offline` từ CLI để verify nhanh.

### Khác
- **API key** dùng chung cả lớp; nhắc giấu qua `local.properties`.
- **Rate limit TMDB** thoáng, đủ cho cả lớp.
- **Nội dung** mặc định `include_adult=false`; Popular/Top Rated là phim đại chúng → an toàn.
- **`genre_ids`** chỉ là ID — bỏ qua hoặc dạy ở khóa nâng cao.
- **Phiên bản thư viện** gom hết vào Version Catalog → dễ cập nhật mỗi kỳ.

---

## 12. Ánh xạ với bài Mars cũ (tham khảo nhanh)

| Thành phần Mars | Mini Netflix |
|---|---|
| `MarsProperty` | `Movie` |
| `getProperties()` | `getPopular()` / `getTopRated()` / `getNowPlaying()` / `getUpcoming()` |
| `MarsApiStatus` | `TmdbApiStatus` |
| `PhotoGridAdapter` | `MoviePosterAdapter` |
| Detail: giá + loại | Detail: ★ điểm + năm + overview + ▶ trailer |
| `bindImage` | Glide gọi inline trong ViewHolder (View Binding) |

Khác biệt lớn so với Mars: thêm **Netflix-style multi-row Home** (Sprint 7), **Search** (Sprint 8), **My List Room** (Sprint 9), và toàn bộ tầng **Engineering Practice** (Git/PR/wireframe/code review).

---

## 13. Checklist trước khi bắt đầu khóa

- [x] Đăng ký TMDB + có API key v3
- [x] Test key trên trình duyệt — JSON trả về OK
- [x] Test URL ảnh poster — hiện ảnh OK
- [x] Chốt mức hỗ trợ: GV cung cấp **đáp án mẫu hoàn chỉnh + student-guide + teaching-guide cho mỗi sprint**
- [x] Chốt môi trường: AGP 9.0.1 + Gradle 9.2.1 + compileSdk 36.1 + Java 11
- [x] Chốt stack (View Binding + LiveData + Retrofit/Moshi + Glide + Nav/SafeArgs Java + Serializable)
- [x] Chốt pedagogy: 10-sprint, 1-sprint-1-feature-1-practice-1-deliverable
- [ ] Trước Sprint 6: SV tạo tài khoản GitHub + Figma (free)
- [ ] Trước Sprint 6: chuẩn bị mini-lesson Git/wireframe 20–25 phút
- [ ] Trước Sprint 9: chuẩn bị mẫu ADR + cách viết Issue tốt

---

*Cập nhật lần cuối: 2026-05-25 — pedagogy shift sang "engineer-who-ships-products" + roadmap 10-sprint + gotchas AGP 9.*
