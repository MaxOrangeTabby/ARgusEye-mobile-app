# ArgusEye Mobile App

The companion Android app for **[ArgusEye](https://github.com/murphymy04/argus)** — the smart-glasses system that recognizes faces, recalls past conversations, and surfaces a context card on the wearer's HUD.

After each session with the glasses, you open this app to put names to faces, review conversation history, and edit notes. Anything you label here flows back into the knowledge graph so the next time that person walks up, the glasses know who they are.

---

## What It Does

The glasses pipeline auto-clusters every unknown face it sees into pending "Person N" buckets. This app is how you turn those buckets into real identities and review what was said.

- **Home** — pending unlabeled face clusters from your most recent sessions. Tap one, type a name, and it becomes a real contact in the knowledge graph.
- **Contacts** — every labeled person, with profile photo, notes, and a per-contact conversation history.
- **Conversations** — full transcripts of every session, searchable, with speaker attribution. Tap a participant to jump to their contact page.
- **You** — your profile and sign-out. Sign in with Google or GitHub via Firebase Auth.

All data is fetched live from the **People API** (FastAPI, port 5000) running on the backend machine.

---

## System Context

```
Smart Glasses (Inmo Air 3 / Android phone)
        │
        │  camera + mic stream
        ▼
ArgusEye Backend  ─ ─ ─  People API (:5000)  ◄────  ArgusEye Mobile App  (this repo)
   │                          │                          │
   │                          ▼                          │ label faces
   │                    SQLite identities                │ edit notes
   │                          ▲                          │ browse interactions
   ▼                          │                          │
Neo4j knowledge graph ◄───────┴──────────────────────────┘
```

**Related repos:**

| Repo | What it is |
|---|---|
| **[ARsenal-Ravengers](https://github.com/murphymy04/ARsenal-Ravengers)** | Backend — face detection, transcription, knowledge graph, People API |
| **[ArgusEye Glasses App](https://github.com/MaxOrangeTabby/ARgusEye-glasses-app-v2)** | Unity Android app — runs on the Inmo Air 3 (or any Android phone) as the camera, mic, and HUD |
| **ArgusEye Mobile App** *(this repo)* | Android companion app — label faces and review conversations |

---

## Prerequisites

| Tool | Why you need it | Install |
|---|---|---|
| **Android Studio** (Ladybug or newer) | Build and run the app | [developer.android.com/studio](https://developer.android.com/studio) |
| **JDK 17** | Required by the Gradle build | Bundled with Android Studio |
| **Android device or emulator** | API level **34+** (Android 14+) | Set up in Android Studio |
| **ArgusEye backend running** | The app has nothing to display without it | See the [backend README](https://github.com/murphymy04/ARsenal-Ravengers#readme) |

The app uses Firebase Auth (Google + GitHub sign-in). A `google-services.json` is already committed under `arguseye/app/` for the project's existing Firebase project — no extra setup is required to build.

---

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/MaxOrangeTabby/ARgusEye-mobile-app
cd ARgusEye-mobile-app/arguseye
```

### 2. Open in Android Studio

`File → Open → ` select the `arguseye/` directory. Let Gradle sync — first sync downloads the Android SDK components, Compose BOM, Firebase BOM, and Retrofit.

### 3. Pick a target device

| Target | What to do |
|---|---|
| **Emulator** | Create an AVD with API 34+. The default backend URL (`http://10.0.2.2:5000/`) already maps the emulator to your laptop's `localhost`. |
| **Physical phone** | Enable USB debugging, plug it in. You'll need to point the app at your laptop's LAN IP (see [Configuration](#configuration)). |

### 4. Run

Hit ▶ in Android Studio, or from the command line:

```bash
cd arguseye
./gradlew installDebug
```

---

## Configuration

The app talks to the People API (port 5000 on the backend machine). The base URL is stored in `SharedPreferences` and defaults to:

```
http://10.0.2.2:5000/
```

`10.0.2.2` is the Android emulator's alias for the host machine's `localhost`, so on the emulator it Just Works as long as the backend is running locally.

### Pointing at a different backend

On a physical device, the emulator alias won't resolve — you need your laptop's LAN IP.

1. Find your laptop's IP (`ipconfig` on Windows / `ifconfig` on macOS / Linux).
2. Make sure the phone and laptop are on the same network.
3. Open the app, go to **Settings**, paste `http://<your-ip>:5000/`, hit **Save & Apply**.

The trailing slash matters — Retrofit requires it. The app appends one if you forget.

> Cleartext HTTP is allowed via `android:usesCleartextTraffic="true"` in the manifest — this is intentional for the local-network development setup. If you deploy the People API behind HTTPS, switch to `https://` in Settings.

---

## API Surface

The app talks to the People API via Retrofit. Every request carries a Firebase ID token in the `Authorization: Bearer …` header (added by `RetrofitClient`'s auth interceptor).

| Endpoint | Method | What it does |
|---|---|---|
| `/api/people/labeled` | GET | List all named contacts |
| `/api/people/unlabeled` | GET | List pending face clusters awaiting a name |
| `/api/people/{id}/label` | POST | Assign a real name to a cluster |
| `/api/people/{id}/notes` | POST | Update freeform notes for a contact |
| `/api/interactions/labeled` | GET | List every conversation with labeled participants |

See [arguseye/app/src/main/java/com/example/argus_eye/data/remote/api/ApiService.kt](arguseye/app/src/main/java/com/example/argus_eye/data/remote/api/ApiService.kt) for the canonical contract.

---

## Project Structure

```
arguseye/
├── app/
│   ├── build.gradle.kts                      ← App module Gradle config
│   ├── google-services.json                  ← Firebase config (committed)
│   └── src/main/java/com/example/argus_eye/
│       ├── core/
│       │   └── MainActivity.kt               ← Entry point + auth/main routing
│       ├── ui/
│       │   ├── LandingView.kt                ← Pre-auth splash
│       │   ├── LoginView.kt                  ← Google + GitHub sign-in
│       │   ├── RegisterView.kt
│       │   ├── MainView.kt                   ← Bottom-nav scaffold (Home/Contacts/Conversations/You)
│       │   ├── HomeScreen.kt                 ← Pending unlabeled face clusters
│       │   ├── ContactsView.kt               ← Labeled contacts list
│       │   ├── ContactDetailsView.kt         ← Single contact + notes + history
│       │   ├── ConversationHistView.kt       ← Conversation list and transcript detail
│       │   ├── ProfileView.kt                ← "You" tab
│       │   ├── SettingsScreen.kt             ← Backend base-URL editor
│       │   └── theme/                        ← Material 3 theming
│       ├── data/
│       │   ├── model/                        ← ContactModel, InteractionResponse, LabelRequest, …
│       │   ├── local/
│       │   │   └── PrefManager.kt            ← SharedPreferences (stores base URL)
│       │   └── remote/api/
│       │       ├── ApiService.kt             ← Retrofit interface
│       │       ├── RetrofitClient.kt         ← OkHttp + Firebase-token auth interceptor
│       │       ├── AuthManager.kt            ← Google + GitHub sign-in via Firebase
│       │       ├── MainController.kt
│       │       └── controller/               ← Per-screen state holders
│       └── AndroidManifest.xml
├── build.gradle.kts                          ← Root Gradle
├── settings.gradle.kts
└── gradle/                                   ← Wrapper + version catalog
```

The architecture is plain Compose + per-screen "controllers" (Kotlin classes that hold `mutableStateOf` UI state and call into Retrofit). No Hilt, no ViewModels — the controllers live as `remember { … }` instances in `MainView`.

---

## Authentication

Sign-in is handled by `AuthManager` using Firebase Auth, with two providers:

- **Google Sign-In** — via the Credential Manager API and the OAuth client baked into [arguseye/app/src/main/java/com/example/argus_eye/core/MainActivity.kt:73](arguseye/app/src/main/java/com/example/argus_eye/core/MainActivity.kt). The Firebase project is shared with the backend so the `Authorization` header it issues is recognized server-side.
- **GitHub Sign-In** — via Firebase's OAuth provider flow.

After sign-in, the Firebase ID token is attached to every Retrofit request automatically — see `RetrofitClient.authInterceptor`.

---

## Testing

```bash
cd arguseye
./gradlew test                  # JVM unit tests
./gradlew connectedAndroidTest  # Instrumented tests (needs a running emulator/device)
```

Unit tests use JUnit + Mockito + OkHttp's `MockWebServer` for the controllers, and `kotlinx-coroutines-test` for suspending API calls.

---

## Troubleshooting

**App opens to a blank list / "Failed to load"**
The People API isn't reachable. Confirm the backend stack is running (`./run.sh` from the [backend repo](https://github.com/murphymy04/ARsenal-Ravengers)) and that the base URL in **Settings** points at it. On a physical device, `10.0.2.2` will not work — use your laptop's LAN IP.

**`CLEARTEXT communication not permitted` error**
The base URL must be `http://…` (not `https://…`) for the dev backend, and the manifest already opts in via `android:usesCleartextTraffic="true"`. If you've changed network security config, revert that.

**Home tab is empty even though the glasses saw new people**
The backend has to be running in **enroll** mode (`./run.sh --enroll`) for unknown faces to be auto-clustered. In `--retrieval` mode no new pending clusters are created.

**Compose preview / build fails on JDK mismatch**
The build targets JVM 17. In Android Studio: `File → Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK` → pick a 17.
