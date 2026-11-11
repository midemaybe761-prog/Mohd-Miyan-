-- AI Video Studio Pro - Database Schema (PostgreSQL / Supabase)

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    subscription_tier VARCHAR(50) DEFAULT 'PRO',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS projects (
    id VARCHAR(100) PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    prompt TEXT,
    full_script TEXT,
    script_language VARCHAR(50) DEFAULT 'English',
    duration_seconds INT DEFAULT 60,
    canvas_ratio VARCHAR(50) DEFAULT 'VERTICAL_9_16',
    quality VARCHAR(50) DEFAULT 'HD_720P',
    fps VARCHAR(50) DEFAULT 'FPS_30',
    visual_style VARCHAR(50) DEFAULT 'CINEMATIC',
    voice_id VARCHAR(100) DEFAULT 'hi_male_aarav',
    music_track_id VARCHAR(100) DEFAULT 'upbeat_cinematic',
    subtitles_enabled BOOLEAN DEFAULT TRUE,
    status VARCHAR(50) DEFAULT 'DRAFT',
    export_path TEXT,
    thumbnail_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS scenes (
    id VARCHAR(100) PRIMARY KEY,
    project_id VARCHAR(100) REFERENCES projects(id) ON DELETE CASCADE,
    scene_order INT NOT NULL,
    duration_seconds INT DEFAULT 5,
    title VARCHAR(255),
    visual_prompt TEXT,
    narration TEXT,
    camera_angle VARCHAR(255),
    transition VARCHAR(50) DEFAULT 'FADE',
    text_overlay VARCHAR(255),
    subtitle_text TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS thumbnails (
    id VARCHAR(100) PRIMARY KEY,
    project_id VARCHAR(100) REFERENCES projects(id) ON DELETE CASCADE,
    headline_text VARCHAR(255) NOT NULL,
    sub_text VARCHAR(255),
    ratio VARCHAR(50) DEFAULT 'YOUTUBE_16_9',
    style VARCHAR(50) DEFAULT 'VIRAL_CLICKBAIT',
    badge_text VARCHAR(100),
    image_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS generation_jobs (
    id VARCHAR(100) PRIMARY KEY,
    project_id VARCHAR(100) REFERENCES projects(id) ON DELETE CASCADE,
    provider VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'PROCESSING',
    progress INT DEFAULT 0,
    duration_seconds INT,
    download_url TEXT,
    error_message TEXT,
    started_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    completed_at TIMESTAMP WITH TIME ZONE
);
