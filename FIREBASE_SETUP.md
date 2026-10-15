# Firebase security setup

The Realtime Database used to allow read and write for everyone. Because `app/google-services.json` ships inside
every copy of the APK, anyone could wipe or rewrite the whole database. The app now signs players in anonymously
and stores lobbies under `/lobbies/{name}` with an `owner` and a `guest`, and `database.rules.json` restricts
access to the two players of each lobby.

This file lists the manual steps needed to finish the migration, in order.

## 1. Enable Anonymous sign-in

Without this, tapping Create lobby or Join lobby always shows the internet error, because `Shared.signIn` fails.

1. Open the [Firebase console](https://console.firebase.google.com/) and select `kzfirebaseproject-aa87e`.
2. Go to **Build > Authentication**. If Authentication has never been used, click **Get started**.
3. Open the **Sign-in method** tab, click **Anonymous**, toggle **Enable**, and save.

## 2. Release the new app version before deploying the rules

The version currently on Google Play does not sign in and writes lobbies at the database root. The moment the new
rules are deployed, multiplayer stops working for every player who has not updated.

1. Build a signed release bundle (`./gradlew bundleRelease`, or **Build > Generate Signed Bundle** in Android
   Studio). `versionCode` is already bumped to `3` in `app/build.gradle`.
2. Upload it to Google Play and roll it out.
3. Wait until most active players are on the new version (Play Console > **Statistics** or **Android vitals**
   shows the version distribution). Optionally, use the
   [in-app updates API](https://developer.android.com/guide/playcore/in-app-updates) in a later version to push
   people to update.

If you would rather close the hole immediately and accept that old versions break, deploy the rules right away.

## 3. Test the rules

### Rules Playground (no install needed)

In the console, go to **Build > Realtime Database > Rules**, paste the content of `database.rules.json` into the
editor **without publishing**, and open **Rules Playground**. Check these cases, with **Authenticated** enabled
and the given UID:

| Operation | Location | UID | Data / existing state | Expected |
|---|---|---|---|---|
| Read | `/` | any | | Denied |
| Write | `/foo` | any | `"x"` | Denied |
| Read | `/lobbies/ABC/owner` | `stranger` | | Allowed |
| Read | `/lobbies/ABC/player1` | `stranger` | lobby owned by `alice` | Denied |
| Write | `/lobbies/ABC/owner` | `alice` | `"alice"`, lobby does not exist | Allowed |
| Write | `/lobbies/ABC/owner` | `bob` | `"bob"`, lobby owned by `alice` | Denied |
| Write | `/lobbies/ABC/guest` | `bob` | `"bob"`, owned by `alice`, no guest | Allowed |
| Write | `/lobbies/ABC/guest` | `carol` | `"carol"`, guest is already `bob` | Denied |
| Write | `/lobbies/ABC/player1/ready` | `bob` | `"1"`, bob is guest | Allowed |
| Write | `/lobbies/ABC/player1/ready` | `bob` | `"7"`, bob is guest | Denied (validation) |
| Write | `/lobbies/ABC/player2/hack` | `bob` | `"x"`, bob is guest | Denied (validation) |
| Write | `/lobbies/ABC` | `alice` | `null` (delete), alice is owner | Allowed |
| Write | `/lobbies/ABC` | `bob` | `null` (delete), bob is guest | Denied |

Without **Authenticated**, every read and write must be denied.

To simulate an existing lobby in the Playground, first create test data in the database (for example
`/lobbies/ABC` with `owner: "alice"`) while the old open rules are still active, and delete it afterwards.

### Local emulator (optional)

```
npx firebase-tools emulators:start
```

`firebase.json` starts the Auth emulator on port 9099 and the Database emulator on port 9000 using
`database.rules.json`. To point a debug build at it, temporarily add this in `Start.onCreate` before any Firebase
call (`10.0.2.2` is the host machine from the Android emulator):

```java
FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
```

Play a full game between two emulators (create, join, guess, replay, leave) and watch the emulator UI at
`http://localhost:4000` for denied requests.

## 4. Deploy the rules

The repository already contains `firebase.json` and `.firebaserc` (default project `kzfirebaseproject-aa87e`).

```
npx firebase-tools login
npx firebase-tools deploy --only database
```

Alternatively, paste `database.rules.json` into **Realtime Database > Rules** in the console and click **Publish**.
Keep `database.rules.json` as the source of truth either way, so the rules stay versioned in git.

## 5. Delete the old lobbies at the root

Old lobbies live directly at the database root (`/ABC123`, `/myLobby`, ...). The new rules make them unreachable
from the app, but they still take space.

1. Go to **Realtime Database > Data**.
2. Export a backup first (**⋮ > Export JSON**).
3. Delete every top-level key except `lobbies`: hover a key and click the trash icon, or from the command line:

   ```
   npx firebase-tools database:remove /SOME_OLD_LOBBY
   ```

   Do not use **Import JSON** for this while players are online: importing replaces the whole database,
   including live lobbies under `lobbies`.

## 6. Add App Check (recommended next step)

The rules stop strangers from touching other players' lobbies, but anyone can still sign in anonymously from a
script and create lobbies. App Check makes Firebase accept only requests coming from your genuine app installed
from Google Play.

1. Get the **SHA-256 of the app signing key**: Play Console > your app > **Test and release > App integrity >
   App signing**. Use the *app signing key* certificate, not the upload key.
2. Firebase console > **Project settings > General > Your apps > com.mouadouad0.kz > Add fingerprint**, and add
   that SHA-256.
3. Firebase console > **Build > App Check > Apps**, register the Android app with **Play Integrity**. Link the
   Google Cloud project in Play Console > **App integrity > Play Integrity API** if asked.
4. In `app/build.gradle`, add:

   ```groovy
   implementation 'com.google.firebase:firebase-appcheck-playintegrity'
   debugImplementation 'com.google.firebase:firebase-appcheck-debug'
   ```

5. Install the provider at the top of `Start.onCreate`, before any Firebase call. Debug builds cannot pass Play
   Integrity, so they use the debug provider:

   ```java
   FirebaseAppCheck appCheck = FirebaseAppCheck.getInstance();
   if (BuildConfig.DEBUG) {
       appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());
   } else {
       appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
   }
   ```

   `BuildConfig` requires `buildFeatures { buildConfig = true }` inside `android { }`. Because
   `DebugAppCheckProviderFactory` only exists in debug builds, the cleaner option is to put the debug install in
   a class under `app/src/debug/java` and the Play Integrity one under `app/src/release/java`, with the same class
   name, and call it from `Start`.

6. Run a debug build once; logcat prints `Enter this debug secret into the allow list in the Firebase Console`.
   Add that token in **App Check > Apps > ⋮ > Manage debug tokens**.
7. Release the update, then watch **App Check > APIs > Realtime Database** metrics. When almost all requests
   are verified, click **Enforce**. Like the rules, enforcing too early breaks players still on older versions.

## 7. Clean up abandoned lobbies (optional)

Lobbies are only deleted when the creator presses back on the waiting screen or creates another lobby. Lobbies
abandoned mid-game stay forever.

1. Store a creation time: add `reset.put("createdAt", ServerValue.TIMESTAMP);` to the lobby update in
   `Waiting.onCreate`, and allow it in `database.rules.json` next to `owner`:

   ```json
   "createdAt": {
     ".validate": "newData.isNumber() && newData.val() <= now"
   }
   ```

2. Write a scheduled Cloud Function (requires the Blaze pay-as-you-go plan) that runs every hour and deletes
   lobbies whose `createdAt` is older than, for example, 6 hours, using an `orderByChild("createdAt")` query on
   `/lobbies`. Add `".indexOn": ["createdAt"]` under `lobbies` in the rules for that query.

## Known limitation: reading the secret code

Both players can read the whole lobby, including the four secret digits (`rana`, `ranb`, `ranc`, `rand`), so a
player with a modified app or a database client can see the code. Rules cannot hide data from someone who must
also be able to write the game state. Preventing this would require moving code generation and answer checking
into Cloud Functions, so the digits never reach the clients.

## Optional: remove the old .aab files from git history

`app/debug/app-debug.aab` and `app/release/app-release.aab` are no longer tracked and are now ignored, but they
are still in past commits. To purge them (rewrites history, every clone must be re-cloned, and it needs a force
push):

```
pip install git-filter-repo
git filter-repo --invert-paths --path app/debug/app-debug.aab --path app/release/app-release.aab
git push --force --all
```

## Building locally

The system JDK on this machine has no `javac`. Build with Android Studio's bundled JDK:

```
JAVA_HOME=/snap/android-studio/current/jbr ./gradlew assembleDebug
```
