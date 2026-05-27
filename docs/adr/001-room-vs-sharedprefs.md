# ADR 001 — Use Room for local "My List" storage

- **Date:** 2026-05-27
- **Status:** Accepted
- **Sprint:** 9

## Context

Sprint 9 introduces "My List" — users mark movies as favorites via the heart icon on Detail, and see them in a dedicated screen. We need local persistence that:

- survives app restart
- survives device reboot
- updates the UI reactively when the underlying data changes (so toggling on Detail also updates My List in real time)

## Options considered

### A. SharedPreferences + JSON via Moshi
- **Pros:** No new dependency (Moshi already in deps). 0 build-config changes. Simple key/value API. Beginner-friendly.
- **Cons:** Reads/writes the entire JSON blob every time. No reactive queries (must re-parse on every read; no automatic UI updates). Doesn't scale beyond a few dozen entries. No teaching value for SQL / DB concepts.

### B. Direct AndroidX SQLite (no Room)
- **Pros:** No code-gen tooling — zero risk on AGP 9 built-in Kotlin (Data Binding has already shown us this is a real failure mode). Closer to "metal" — useful learning.
- **Cons:** Verbose. Manual SQL strings (no compile-time check). Manual cursor management. No type-safe entity mapping. Not industry-standard for new Android apps in 2026.

### C. Room *(chosen)*
- **Pros:** Industry-standard — every Android job interview asks about it. Compile-time SQL validation. Reactive queries via `LiveData<List<T>>` — the My List screen and the Detail heart auto-sync without any manual refresh. Native coroutines support (`suspend fun`). Excellent docs.
- **Cons:** Requires KSP plugin (annotation processing). New tooling on AGP 9 built-in Kotlin — small risk of version-mismatch build issues. One more dependency (~200 KB AAR).

## Decision

**Room** with KSP.

## Consequences

- New build config: KSP plugin + 3 Room dependencies (runtime / ktx / compiler).
- New `mylist/data/` package: `FavoriteMovie` (`@Entity`), `FavoriteDao`, `AppDatabase`.
- The DAO is reached via the singleton `AppDatabase.getInstance(context).favoriteDao()` — no DI framework needed.
- KSP version (`ksp = "2.1.10-1.0.31"` in `libs.versions.toml`) must align with the Kotlin version that AGP 9 bundles. If a future AGP upgrade breaks the match, the error message is clear — bump to the matching release listed at <https://github.com/google/ksp/releases>.
- `DetailViewModel` learned about `FavoriteDao` (small coupling). The Fragment passes the DAO in; the ViewModel itself doesn't construct it. This keeps the ViewModel testable without a fake DB.

## Migration path if KSP becomes painful

If a future AGP / Kotlin release breaks the KSP+Room pipeline and there's no quick KSP fix:

1. Drop Room. Remove the `mylist/data/*` package, the KSP plugin, the 3 Room deps.
2. Re-implement `MyListViewModel` as a SharedPreferences-backed store: a single string key holding a Moshi-serialised `List<Movie>`.
3. Re-implement the reactive part with a `MutableLiveData<List<Movie>>` whose updater writes the JSON and also calls `setValue`. Manual but functional.

Schema is tiny (one table). A one-time export from Room → JSON would be straightforward.

## References

- https://developer.android.com/training/data-storage/room
- https://github.com/google/ksp
- Mini Netflix Sprint 9 student & teaching guides.
