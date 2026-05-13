-- 1. Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS mesos;
USE mesos;

-- 2. Players table
CREATE TABLE IF NOT EXISTS players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(50) UNIQUE NOT NULL
);

-- 3. Matches table (The "Event")
CREATE TABLE IF NOT EXISTS matches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    match_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    player_count INT NOT NULL
);

-- 4. Junction table (The "Results")
-- This links players to matches and stores their specific score
CREATE TABLE IF NOT EXISTS player_matches (
    player_id INT NOT NULL,
    match_id INT NOT NULL,
    score INT NOT NULL,
    PRIMARY KEY (player_id, match_id),
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE
);

-- 5. Optional: A view to simplify leaderboard queries
CREATE OR REPLACE VIEW global_rankings AS
SELECT p.nickname
    ,m.player_count
    ,SUM(pm.score) as total_points
FROM player_matches pm
JOIN players p
    ON pm.player_id = p.id
JOIN matches m
    ON pm.match_id = m.id
GROUP BY p.nickname, m.player_count;