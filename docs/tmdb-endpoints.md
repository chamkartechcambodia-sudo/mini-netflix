# TMDB API v3 — Endpoint Reference

Reference list of every endpoint provided by **The Movie Database API v3**, with one-line purposes.
Full official documentation: <https://developer.themoviedb.org/reference>.

This file is a quick lookup so we don't have to keep opening the docs site while planning a sprint or picking a feature for the extension phase.

---

## Universal info

- **Base URL:** `https://api.themoviedb.org/3`
- **Auth (this course):** every request needs `?api_key=<KEY>`. The shared class key lives in `local.properties` → `BuildConfig.TMDB_API_KEY` (never commit it).
- **Language:** add `&language=en-US` (default for this course — students don't read Vietnamese).
- **Pagination:** list endpoints accept `&page=<n>` (1-indexed). `total_pages` in the response tells you when to stop.
- **Image URLs:** images are **not** absolute — endpoints return relative paths like `/abc.jpg`. Build the full URL with `https://image.tmdb.org/t/p/<size>` + the path. Valid `<size>` for posters: `w92 / w154 / w185 / w342 / w500 / w780 / original`. For backdrops: `w300 / w780 / w1280 / original`. Call `GET /configuration` once to get the authoritative list.
- **Auth column legend:**
  - `api_key` — only the shared key is required.
  - `session` — also needs a user session ID (3-step login flow).
  - `session/guest` — accepts either a user session or a guest session.
  - `guest` — only the guest session works here.

---

## Endpoint catalog

| Category | Method | Endpoint | Purpose | Auth |
|---|---|---|---|---|
| Movies | GET | `/movie/popular` | Popular movies | api_key |
| Movies | GET | `/movie/top_rated` | Top-rated movies | api_key |
| Movies | GET | `/movie/now_playing` | Movies currently in theatres | api_key |
| Movies | GET | `/movie/upcoming` | Upcoming releases | api_key |
| Movies | GET | `/movie/latest` | Newest movie record added to TMDB | api_key |
| Movies | GET | `/movie/{id}` | Movie details (runtime, genres, budget, …) | api_key |
| Movies | GET | `/movie/{id}/credits` | Cast and crew | api_key |
| Movies | GET | `/movie/{id}/images` | Posters and backdrops | api_key |
| Movies | GET | `/movie/{id}/videos` | Trailers / clips (YouTube IDs) | api_key |
| Movies | GET | `/movie/{id}/recommendations` | Recommended movies | api_key |
| Movies | GET | `/movie/{id}/similar` | Similar movies | api_key |
| Movies | GET | `/movie/{id}/reviews` | User reviews | api_key |
| Movies | GET | `/movie/{id}/keywords` | Tags / keywords | api_key |
| Movies | GET | `/movie/{id}/alternative_titles` | Localised titles per country | api_key |
| Movies | GET | `/movie/{id}/release_dates` | Per-country release dates + age ratings | api_key |
| Movies | GET | `/movie/{id}/translations` | Localised title/overview per language | api_key |
| Movies | GET | `/movie/{id}/external_ids` | IMDb / social IDs | api_key |
| Movies | GET | `/movie/{id}/watch/providers` | Streaming / rent / buy availability | api_key |
| Movies | GET | `/movie/{id}/lists` | Public lists that include this movie | api_key |
| Movies | GET | `/movie/{id}/account_states` | Current user's rating / watchlist / favourite for this movie | session |
| Movies | POST | `/movie/{id}/rating` | Rate this movie | session/guest |
| Movies | DELETE | `/movie/{id}/rating` | Remove your rating | session/guest |
| Movies | GET | `/movie/{id}/changes` | Recent metadata changes for one movie | api_key |
| Movies | GET | `/movie/changes` | All movies changed in a date window | api_key |
| TV | GET | `/tv/popular` | Popular shows | api_key |
| TV | GET | `/tv/top_rated` | Top-rated shows | api_key |
| TV | GET | `/tv/airing_today` | Shows airing today | api_key |
| TV | GET | `/tv/on_the_air` | Shows on the air this week | api_key |
| TV | GET | `/tv/latest` | Newest TV record added to TMDB | api_key |
| TV | GET | `/tv/{id}` | Show details | api_key |
| TV | GET | `/tv/{id}/credits` | Cast and crew (season-flattened) | api_key |
| TV | GET | `/tv/{id}/aggregate_credits` | Cast aggregated across all seasons | api_key |
| TV | GET | `/tv/{id}/alternative_titles` | Localised titles per country | api_key |
| TV | GET | `/tv/{id}/content_ratings` | Per-country content/age ratings | api_key |
| TV | GET | `/tv/{id}/episode_groups` | Custom episode orderings | api_key |
| TV | GET | `/tv/{id}/external_ids` | IMDb / TVDB / social IDs | api_key |
| TV | GET | `/tv/{id}/images` | Posters / backdrops / logos | api_key |
| TV | GET | `/tv/{id}/keywords` | Tags / keywords | api_key |
| TV | GET | `/tv/{id}/recommendations` | Recommended shows | api_key |
| TV | GET | `/tv/{id}/reviews` | User reviews | api_key |
| TV | GET | `/tv/{id}/screened_theatrically` | Episodes that aired in theatres | api_key |
| TV | GET | `/tv/{id}/similar` | Similar shows | api_key |
| TV | GET | `/tv/{id}/translations` | Localised title/overview per language | api_key |
| TV | GET | `/tv/{id}/videos` | Trailers / clips | api_key |
| TV | GET | `/tv/{id}/watch/providers` | Streaming availability | api_key |
| TV | GET | `/tv/{id}/account_states` | Current user's state for this show | session |
| TV | POST | `/tv/{id}/rating` | Rate this show | session/guest |
| TV | DELETE | `/tv/{id}/rating` | Remove your rating | session/guest |
| TV | GET | `/tv/{id}/changes` | Recent changes for one show | api_key |
| TV | GET | `/tv/changes` | All shows changed in a date window | api_key |
| TV Season | GET | `/tv/{id}/season/{n}` | Season details + episode list | api_key |
| TV Season | GET | `/tv/{id}/season/{n}/credits` | Season-level cast / crew | api_key |
| TV Season | GET | `/tv/{id}/season/{n}/aggregate_credits` | Aggregated cast across episodes | api_key |
| TV Season | GET | `/tv/{id}/season/{n}/external_ids` | TVDB / IMDb IDs | api_key |
| TV Season | GET | `/tv/{id}/season/{n}/images` | Season posters | api_key |
| TV Season | GET | `/tv/{id}/season/{n}/translations` | Localised name / overview | api_key |
| TV Season | GET | `/tv/{id}/season/{n}/videos` | Trailers / clips | api_key |
| TV Season | GET | `/tv/{id}/season/{n}/watch/providers` | Streaming availability | api_key |
| TV Season | GET | `/tv/{id}/season/{n}/account_states` | User's per-episode ratings for this season | session |
| TV Season | GET | `/tv/{id}/season/{n}/changes` | Recent changes for a season | api_key |
| TV Episode | GET | `/tv/{id}/season/{n}/episode/{e}` | Episode details | api_key |
| TV Episode | GET | `/tv/{id}/season/{n}/episode/{e}/credits` | Cast + guest stars | api_key |
| TV Episode | GET | `/tv/{id}/season/{n}/episode/{e}/external_ids` | IMDb / TVDB IDs | api_key |
| TV Episode | GET | `/tv/{id}/season/{n}/episode/{e}/images` | Episode stills | api_key |
| TV Episode | GET | `/tv/{id}/season/{n}/episode/{e}/translations` | Localised name / overview | api_key |
| TV Episode | GET | `/tv/{id}/season/{n}/episode/{e}/videos` | Clips | api_key |
| TV Episode | GET | `/tv/{id}/season/{n}/episode/{e}/account_states` | User's rating for this episode | session |
| TV Episode | POST | `/tv/{id}/season/{n}/episode/{e}/rating` | Rate this episode | session/guest |
| TV Episode | DELETE | `/tv/{id}/season/{n}/episode/{e}/rating` | Remove your episode rating | session/guest |
| TV Episode | GET | `/tv/{id}/season/{n}/episode/{e}/changes` | Recent changes for an episode | api_key |
| TV Episode Group | GET | `/tv/episode_group/{group_id}` | Custom episode group (alternative orderings) | api_key |
| People | GET | `/person/popular` | Popular people | api_key |
| People | GET | `/person/latest` | Newest person record added | api_key |
| People | GET | `/person/{id}` | Person details (biography, birthday) | api_key |
| People | GET | `/person/{id}/movie_credits` | Their movie roles | api_key |
| People | GET | `/person/{id}/tv_credits` | Their TV roles | api_key |
| People | GET | `/person/{id}/combined_credits` | All credits combined | api_key |
| People | GET | `/person/{id}/images` | Profile photos | api_key |
| People | GET | `/person/{id}/external_ids` | IMDb / social IDs | api_key |
| People | GET | `/person/{id}/translations` | Localised biographies | api_key |
| People | GET | `/person/{id}/changes` | Recent changes for one person | api_key |
| People | GET | `/person/changes` | All people changed in a date window | api_key |
| Credits | GET | `/credit/{credit_id}` | Details of a single cast/crew credit | api_key |
| Search | GET | `/search/movie` | Search for a movie by title | api_key |
| Search | GET | `/search/tv` | Search for a TV show | api_key |
| Search | GET | `/search/person` | Search for a person | api_key |
| Search | GET | `/search/multi` | Search movies + TV + people in one call | api_key |
| Search | GET | `/search/company` | Search production companies | api_key |
| Search | GET | `/search/collection` | Search collections (e.g. "Harry Potter Collection") | api_key |
| Search | GET | `/search/keyword` | Search keywords | api_key |
| Trending | GET | `/trending/{media_type}/{time_window}` | Trending items (`all\|movie\|tv\|person` × `day\|week`) | api_key |
| Discover | GET | `/discover/movie` | Filter / sort movies by many criteria | api_key |
| Discover | GET | `/discover/tv` | Filter / sort TV shows by many criteria | api_key |
| Genres | GET | `/genre/movie/list` | List of movie genres (ID → name) | api_key |
| Genres | GET | `/genre/tv/list` | List of TV genres (ID → name) | api_key |
| Keywords | GET | `/keyword/{id}` | Keyword details | api_key |
| Keywords | GET | `/keyword/{id}/movies` | Movies tagged with a keyword | api_key |
| Collections | GET | `/collection/{id}` | Collection details + member movies | api_key |
| Collections | GET | `/collection/{id}/images` | Collection posters / backdrops | api_key |
| Collections | GET | `/collection/{id}/translations` | Localised name / overview | api_key |
| Companies | GET | `/company/{id}` | Company details | api_key |
| Companies | GET | `/company/{id}/alternative_names` | Alternative company names | api_key |
| Companies | GET | `/company/{id}/images` | Company logos | api_key |
| Networks | GET | `/network/{id}` | TV network details (HBO, Netflix, …) | api_key |
| Networks | GET | `/network/{id}/alternative_names` | Alternative network names | api_key |
| Networks | GET | `/network/{id}/images` | Network logos | api_key |
| Lists | GET | `/list/{id}` | Read a user-created list | api_key |
| Lists | GET | `/list/{id}/item_status` | Check whether a movie is in a list | api_key |
| Lists | POST | `/list` | Create a new list | session |
| Lists | POST | `/list/{id}/add_item` | Add a movie to a list | session |
| Lists | POST | `/list/{id}/remove_item` | Remove a movie from a list | session |
| Lists | POST | `/list/{id}/clear` | Empty a list | session |
| Lists | DELETE | `/list/{id}` | Delete a list | session |
| Reviews | GET | `/review/{id}` | Single review details | api_key |
| Find | GET | `/find/{external_id}` | Look up TMDB ID by IMDb / TVDB / social ID (`external_source=` query) | api_key |
| Certifications | GET | `/certification/movie/list` | Per-country movie certifications (G, PG, R, …) | api_key |
| Certifications | GET | `/certification/tv/list` | Per-country TV certifications | api_key |
| Watch Providers | GET | `/watch/providers/regions` | Supported regions | api_key |
| Watch Providers | GET | `/watch/providers/movie` | Available streaming providers for movies | api_key |
| Watch Providers | GET | `/watch/providers/tv` | Available streaming providers for TV | api_key |
| Configuration | GET | `/configuration` | Image base URL + valid image sizes (use this instead of hardcoding) | api_key |
| Configuration | GET | `/configuration/countries` | Supported countries | api_key |
| Configuration | GET | `/configuration/languages` | Supported languages | api_key |
| Configuration | GET | `/configuration/jobs` | Crew job titles | api_key |
| Configuration | GET | `/configuration/primary_translations` | Primary translation locales | api_key |
| Configuration | GET | `/configuration/timezones` | Supported timezones | api_key |
| Account | GET | `/account` | Current user details | session |
| Account | GET | `/account/{id}/lists` | User's lists | session |
| Account | GET | `/account/{id}/favorite/movies` | User's favourite movies | session |
| Account | GET | `/account/{id}/favorite/tv` | User's favourite TV shows | session |
| Account | POST | `/account/{id}/favorite` | Mark / unmark a movie or show as favourite | session |
| Account | GET | `/account/{id}/rated/movies` | Movies the user rated | session |
| Account | GET | `/account/{id}/rated/tv` | TV shows the user rated | session |
| Account | GET | `/account/{id}/rated/tv/episodes` | TV episodes the user rated | session |
| Account | GET | `/account/{id}/watchlist/movies` | User's movie watchlist | session |
| Account | GET | `/account/{id}/watchlist/tv` | User's TV watchlist | session |
| Account | POST | `/account/{id}/watchlist` | Add / remove from watchlist | session |
| Auth | GET | `/authentication` | Validate the API key | api_key |
| Auth | GET | `/authentication/guest_session/new` | Create a guest session | api_key |
| Auth | GET | `/authentication/token/new` | Create a request token | api_key |
| Auth | POST | `/authentication/token/validate_with_login` | Validate a token with username + password | api_key |
| Auth | POST | `/authentication/session/new` | Create a user session from a validated token | api_key |
| Auth | POST | `/authentication/session/convert/4` | Convert a v4 access token into a v3 session | api_key |
| Auth | DELETE | `/authentication/session` | Log out (delete the session) | session |
| Guest Session | GET | `/guest_session/{id}/rated/movies` | Movies rated by the guest | guest |
| Guest Session | GET | `/guest_session/{id}/rated/tv` | TV shows rated by the guest | guest |
| Guest Session | GET | `/guest_session/{id}/rated/tv/episodes` | TV episodes rated by the guest | guest |

---

## What this course uses (per sprint)

| Sprint | Endpoint | Why |
|---|---|---|
| 1 | `GET movie/popular` | First list of movies on Home |
| 6 | `GET movie/{id}/videos` | Find the YouTube trailer to open via Intent |
| 7 | `GET movie/popular`, `top_rated`, `now_playing`, `upcoming` | 4 rows on the Netflix-style Home |
| 8 | `GET search/movie` | Search bar |
| 9 | *(none — local Room DB only)* | My List ❤ |

Anything in the catalog above and **not** in this small table is a candidate for an extension / advanced phase.

---

## Useful extension picks (ranked by impact for this course)

1. **`GET movie/{id}` for the Detail screen** — gives `runtime`, `genres[]` (with names, not just IDs), `budget`, `revenue`, `tagline`. Currently we pass the trimmed `Movie` via SafeArgs; calling this would let Detail show much richer info.
2. **`GET movie/{id}/credits`** — cast row with profile photos on the Detail screen (Sprint 11 in the plan).
3. **`GET movie/{id}/recommendations`** — "More Like This" row on Detail (Sprint 11).
4. **`GET discover/movie?with_genres=…`** — proper genre browsing with sort/filter (Sprint 12).
5. **`GET trending/{media}/{window}`** — a "Trending Today" row up top.
6. **`GET configuration`** — once at app start, cache base image URL + sizes instead of hardcoding `w500`/`w780`.

---

## Things to ignore (almost certainly out of scope)

- **Episode Groups** — only matters for shows with alternative orderings (Dragon Ball Z, etc.).
- **`/find/{external_id}`** — useful for migrating from IMDb-based apps; we don't have that need.
- **`changes` endpoints** — for syncing a local cache to TMDB updates; only relevant at large scale.
- **Authentication + Account + Lists** — pulls in the full 3-step login flow; only do this if Sprint 10 (Login) is included.

---

*Source: <https://developer.themoviedb.org/reference> · API version 3.*
*Maintained alongside the course materials in `MiniNetflix-KeHoach.md`.*
