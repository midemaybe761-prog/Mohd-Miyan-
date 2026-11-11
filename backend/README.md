# AI Video Studio Pro - Complete System Guide

Welcome to **AI Video Studio Pro**, a production-grade, enterprise-ready AI Video Generator, Script Writer, Video Editing Studio, Subtitle Engine, and Thumbnail Creator.

---

## 🏗️ Architecture & Dual Mode System

### MODE A: Local Offline Engine (Zero API Required)
* **Status**: 100% operational out of the box.
* **Rendering**: Android hardware-accelerated `MediaCodec` and `MediaMuxer` synthesizing H.264 video streams directly to `.mp4` saved into the device gallery and local app storage.
* **Narration**: On-device native `TextToSpeech` engine with pitch modulation and speed controls (Hindi Male/Female, English Male/Female).
* **Script Writing**: Built-in deterministic multi-genre script structuring engine.
* **Thumbnails**: Real-time canvas graphic compositor generating YouTube and Shorts thumbnails.

### MODE B: AI Cloud Engine (External AI & FFmpeg Server)
* **Backend**: Node.js + Express REST API with FFmpeg video processing pipeline.
* **Supported AI Providers**: Google Gemini, OpenAI (GPT-4o/Sora), Runway Gen-3, ElevenLabs, Pika Labs.
* **Asynchronous Jobs**: Job submission with polling, progress updates (0% to 100%), and webhook callbacks.
* **Database**: PostgreSQL / Supabase schema included (`schema.sql`) and Firebase Firestore adapter (`firebase-config.js`).

---

## 🚀 Backend Setup Instructions (Node.js & Express)

### Prerequisites
* Node.js v18.0.0 or newer
* FFmpeg installed on host:
  * Ubuntu/Debian: `sudo apt update && sudo apt install -y ffmpeg`
  * macOS: `brew install ffmpeg`
  * Windows: Download from https://ffmpeg.org/download.html and add to PATH

### Quick Start
1. Navigate to the backend folder:
   ```bash
   cd backend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Copy the environment configuration:
   ```bash
   cp .env.example .env
   ```
4. Start the server:
   ```bash
   npm start
   ```
   Server will listen on `http://0.0.0.0:5000`. Test via:
   ```bash
   curl http://localhost:5000/api/health
   ```

---

## 📱 Android Application Build Instructions

### Prerequisites
* Android Studio Ladybug (2024.2+) or newer
* Android SDK 36 (Minimum SDK 24, Android 7.0+)
* JDK 17 or JDK 21

### Building the Project
1. Open the project root in Android Studio.
2. Allow Gradle to sync dependencies automatically.
3. Select your device or emulator (Android 8.0+ recommended).
4. Run or debug via **Shift + F10**.

### Generating Release / Debug APK
To build the debug APK via Gradle:
```bash
gradle :app:assembleDebug
```
Output APK location:
`app/build/outputs/apk/debug/app-debug.apk`

To build the release signed APK:
```bash
gradle :app:assembleRelease
```

---

## 🔑 Secure Secrets Management
To configure Gemini or third-party AI keys securely on Android without hardcoding:
* AI Studio automatically injects API keys at runtime via `.env` and `BuildConfig.GEMINI_API_KEY`.
* Alternatively, in the in-app **Studio Settings** or **Admin Panel**, administrators can configure custom backend URLs, API providers, and authorization tokens with instant "Test API" health verification.

---

## 🎬 Feature Summary
1. **Home Dashboard**: Quick action tiles, real-time telemetry, recent project cards.
2. **AI Video Generator**: Prompt to full scenes, duration selection (30s to 10 min), canvas ratio (9:16 Shorts, 16:9 Landscape, 1:1 Square, 4:5 Portrait), quality (480p to 4K), frame rates (24, 30, 60 FPS).
3. **AI Script Writer**: Multilingual (English, Hindi हिंदी), genres (Educational, Entertainment, Storytelling, Cooking, Health, Motivation).
4. **AI Voice Generator**: Aarav (Hindi Male), Priya (Hindi Female), Marcus (English Male), Emma (English Female), James (British Male) with live audio preview and adjustable speaking speed.
5. **Video Editing Studio**: Multi-track timeline (Scenes, Narration, Music, Subtitles), scene trimming, speed control, transition effects (Fade, Flash, Dissolve, Zoom), undo/redo stack.
6. **AI Thumbnail Generator**: YouTube (16:9), Shorts (9:16), Instagram (1:1) presets, customizable bold typography, glow borders, and PNG export.
7. **Video Export System**: Real-time H.264 MP4 hardware rendering, ETA estimation, cancellation, and direct gallery sharing.
