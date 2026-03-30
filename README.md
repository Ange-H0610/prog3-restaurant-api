# PROG3 Restaurant Management API

## Description
API REST pour la gestion d'un restaurant, développée dans le cadre des exercices TD1 à TD5.

## Technologies utilisées
- Java 11
- Spring Boot 2.7.0
- PostgreSQL
- Maven
- HikariCP

## Structure du projet
prog3-restaurant-api/
├── src/
│ ├── main/
│ │ ├── java/com/hei/prog3/
│ │ │ ├── config/ # Configuration (DataSource)
│ │ │ ├── controller/ # Contrôleurs REST
│ │ │ ├── entity/ # Entités JPA
│ │ │ ├── repository/ # Accès aux données
│ │ │ ├── service/ # Logique métier
│ │ │ └── Application.java
│ │ └── resources/
│ │ └── application.yml
│ └── test/ # Tests unitaires
├── database/
│ └── scripts/ # Scripts SQL
├── pom.xml
└── README.md

text

## Installation

### 1. Prérequis
- Java 11+
- PostgreSQL 12+
- Maven 3.6+

### 2. Configuration de la base de données
```bash
