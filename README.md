# Système de Micro-Finance Multi-Monétaire (Fullstack Portfolio)

Ce projet est une application web de **Micro-Finance Multi-Monétaire** robuste, moderne et scalable. Conçue selon une approche pragmatique et modulaire, elle démontre la mise en œuvre d'une architecture découplée, d'une sécurité renforcée et d'un traitement transactionnel de données volumineuses.

Démonstration rapide des fonctionnalités: ./screenshots/demo.gif

Le projet intègre un backend **Spring Boot** structuré en **Architecture Hexagonale (Ports & Adaptateurs)** et un frontend **Angular 19** utilisant les dernières fonctionnalités réactives (**Signals** et composants **Standalone**).

---

## 🛠️ Stack Technique

### Backend (Spring Boot)
* **Architecture && Core :** Java, Spring Boot 3, Architecture Hexagonale (Séparation stricte du code métier et des environnements techniques).
* **Sécurité :** Spring Security, JWT stocké dans un **Cookie HTTP-Only** protection contre XSS,et protection active contre les failles **CSRF (CSRF Token)**.
* **Persistance && Données :** PostgreSQL, Spring Data JPA, **Liquibase** pour le versioning et la migration de la base de données.
* **Batchs && Automatisation :** **Spring Batch** combiné à l'annotation `@Scheduled` pour le calcul et l'application automatique des taux d'intérêt à chaque fin de mois pour un grand volume des comptes.
* **Communications Extérieures :** `RestClient` (Spring Framework) sécurisé par **Resilience4j** (Gestion des pannes via Retry, Circuit Breaker) pour la résilience face aux API tierces.
* **Notification :** `spring-boot-starter-mail` pour l'envoi automatisé d'emails.
* **Documentation && DevOps :** Swagger UI (OpenAPI), Docker & Docker-Compose (`Dockerfile` et `docker-compose.yml`) pour une conteneurisation complète.
* **Test : JUnit, Mockito, H2 pour le test d'intégration des JpaRepositories

### Frontend (Angular 19)
* **Core Architecture :** Angular v19, Composants **Standalone**, Architecture moderne.
* **Gestion d'État :** **Angular Signals** pour l'état local et la réactivité, **NgRx** dédié spécifiquement à la gestion globale de l'authentification (Login/Logout) pour centraliser les états et les flux du traitement et aussi faciliter la maintenance en cas d'évolution de business metier.
* **Design && UI :** **Tailwind CSS** associé à **Angular Material** pour des composants UX/UI professionnels et réactifs.

---

## ✨ Fonctionnalités Principales (Features)

### 🔓 Authentification & Profil
* Connexion (Login) sécurisée, déconnexion (Logout) gérées par NgRx.
* Changement sécurisé de mot de passe.
* Gestion du profil utilisateur (Affichage et modification des informations).

### ⚙️ Paramétrage & Administration (CRUD)
* **Gestion des employés :** Administration des status.
* **Gestion des monnaies :** Configuration des devises prises en charge par le système multi-monétaire.
* **Gestion des types de comptes :** Définition des règles métier et taux d'intérêt applicables.

### 👥 Gestion des Clients
* Création de nouveaux clients.
* Vue en liste avec support complet du **tri**, de la **recherche**, de la **pagination** et affichage détaillé.

### 💳 Gestion des Comptes
* Ouverture et création de comptes.
* Consultation des soldes, listes, tris, recherches et pagination.
* **Suivi du cycle de vie du compte :** Historique complet et traçabilité des statuts (`Création` ➡️ `Activation` ➡️ `Suspension` ➡️ `Fermeture`).

### 💸 Gestion des Opérations & Transactions
* Enregistrement des opérations de **Dépôt** et de **Retrait**.
* Exécution de **Transferts** de compte à compte par le client.
* Consultation de l'historique des transactions et **export / import au format PDF**.

### 📊 Tableau de Bord (Dashboard Statistique)
* Indicateurs clés (KPI) : Nombre total de clients, nombre de comptes.
* **Solde global consolidé** exprimé en **MGA** (Ariary).
* Répartition visuelle par type de compte (Nombre et solde).
* Graphique d'enregistrement journalier des nouveaux clients pour suivre l'activité.

---

## 📐 Architecture Backend : Focus Hexagonal
```
L'application applique strictement les principes du Clean Architecture via l'**Architecture Hexagonale**.
Cela garantit que la logique métier (le domaine) reste totalement isolée des frameworks, des bases de données et des interfaces extérieures.

              ┌───────────────────────────────────────────────┐
              │                  INFRASTRUCTURE               │
              │                                               │
              │   ┌───────────┐                               │               
              │   │   REST    │                               │               
              │   │Controllers│               ┌─────────────┐ │
              │   │     ▲     │               │JPA Entities │ │
              │   │     │     │               │  JPA Repo   │ │
              │   │UseCaseProxy               │Service tiers│ │
              └───│─────┬─────│───────────────│─────│───────│─┘
                        │                           │
                        ▲ appeler                   ▼ implémenter
                        │                           │
              ┌─────────│───────────────────────────│──────────┐
              │         │          APPLICATION      │          │
              │┌───────────────────┐      ┌───────────────────┐│
              ││    Inbound Port   │      │   Outbound Port   ││
              │UseCase/Implémentation     │Service/Repository ││
              │└────────┼──────────┘      └─────────│─────────┘│
              └─────────│───────────────────────────│──────────┘
                        │                           │
                        ▲ appeler                   ▲ appeler
                        │                           │
              ┌─────────│───────────────────────────│─────────┐
              │                     DOMAIN                    │
              │                                               │
              │         Business Logic & Core Entities(POJO)  │
              │                                               │
              └───────────────────────────────────────────────┘
```
* **Domain (Noyau métier) :** Contient les entités pures et les règles de gestion (calculs, validations,vo).
* **Ports :** Interfaces qui définissent comment le monde extérieur interagit avec le domaine (`Inbound` / Use Cases) et comment le domaine interagit avec l'extérieur (`Outbound` / SPI).
* **Adapters (Infrastructure) :** Les composants techniques externes.
    * *Driving Adapters :* Contrôleurs REST, Jobs Spring Batch.
    * *Driven Adapters :* Implémentations des repositories Spring Data JPA, `RestClient`(API tierces).

---

## 🚀 Installation et Démarrage

### Prérequis
* Java 17+
* Node.js (v20+) & Angular CLI (v19+)
* Docker & Docker-Compose

### 1. Cloner le projet
```Bash```
git clone https://github.com/rivohery/micro-finance.git
cd projet-microfinance

### 2. Démarrage globale(environment + application)
```Bash```
docker-compose up -d

### 3. Démarrage backend
```Bash```
cd ../backend
./mvnw spring-boot:run(Linux/macOS) ou mvnw spring-boot:run(Windows)

### 4. Démarrage frontend
```Bash```
cd ../frontend
npm install
ng serve


### API Documentation (Swagger UI) : Accessible sur http://localhost:8088/api/v1/swagger-ui/index.html une fois l'application démarrée.
### Rendez-vous sur http://localhost:4200 pour accéder à l'application angular.
### Utilisateur par défaut(admin): pseudo = alibou, password = 0000 