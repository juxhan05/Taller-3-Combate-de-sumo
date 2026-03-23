-- ══════════════════════════════════════════════════════════════════════════
--  Script de creación de base de datos: sumo_db
--  Proyecto : Combate de Sumo — Programación Avanzada
--  Motor    : MySQL 8.0+
--  Ejecutar : source specs/script_db.sql  (desde MySQL Workbench o terminal)
-- ══════════════════════════════════════════════════════════════════════════

-- 1. Crear base de datos y seleccionarla
CREATE DATABASE IF NOT EXISTS sumo_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sumo_db;

-- ── Tabla de luchadores ────────────────────────────────────────────────────
-- Almacena los datos básicos de cada rikishi registrado desde el cliente.
-- ha_combatido = 0 → disponible para combatir
-- ha_combatido = 1 → ya participó, no puede volver a luchar
CREATE TABLE IF NOT EXISTS luchadores (
    id             INT            AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100)   NOT NULL,
    peso           DOUBLE         NOT NULL,
    victorias      INT            NOT NULL DEFAULT 0,
    ha_combatido   TINYINT(1)     NOT NULL DEFAULT 0,
    fecha_registro TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Tabla de kimarites por luchador ───────────────────────────────────────
-- Relación 1:N con luchadores.
-- Cada luchador puede tener un subconjunto de las 82 técnicas oficiales.
-- ON DELETE CASCADE: si se borra el luchador, se borran sus técnicas.
CREATE TABLE IF NOT EXISTS kimarites_luchador (
    id               INT           AUTO_INCREMENT PRIMARY KEY,
    id_luchador      INT           NOT NULL,
    nombre_kimarite  VARCHAR(100)  NOT NULL,
    descripcion      VARCHAR(255)  DEFAULT '',
    FOREIGN KEY (id_luchador)
        REFERENCES luchadores(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ══════════════════════════════════════════════════════════════════════════
--  Verificar creación de tablas
-- ══════════════════════════════════════════════════════════════════════════
-- SHOW TABLES;

-- ══════════════════════════════════════════════════════════════════════════
--  Consulta de verificación (ejecutar después de registrar luchadores)
-- ══════════════════════════════════════════════════════════════════════════
-- SELECT l.id, l.nombre, l.peso, l.victorias, l.ha_combatido,
--        k.nombre_kimarite, k.descripcion
-- FROM luchadores l
-- LEFT JOIN kimarites_luchador k ON l.id = k.id_luchador
-- ORDER BY l.id, k.id;
