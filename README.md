# Système de Micro-Finance Multi-Monétaire (Fullstack Portfolio)

Application web fullstack de **Microfinance Multi-devises** conçue pour illustrer les bonnes pratiques d'ingénierie logicielle : **Architecture Hexagonale**, **traitements par lots (Spring Batch)**, **sécurité renforcée (JWT via Cookies HttpOnly + CSRF)** et **composants réactifs Angular 19 (Signals) et **Standalone**.


**Démo rapide des fonctionnalités** : `./screenshots/demo.gif`


---


## 🛠️ Stack Technique

### Backend (Java / Spring Boot)
* **Architecture & Core** : Java 17, Spring Boot 3.x, Architecture Hexagonale (Ports & Adapters).
* **Sécurité** : Spring Security, JWT stocké dans un Cookie `HttpOnly` (protection XSS) avec validation active de tokens CSRF.
* **Persistance & Données** : PostgreSQL, Spring Data JPA, Liquibase (versioning et migrations de schémas).
* **Batchs & Automatisation** : Spring Batch orchestré par `@Scheduled` pour le calcul et la capitalisation automatique mensuelle des intérêts sur grand volume de comptes.
* **Résilience & Services Tiers** : `RestClient` sécurisé avec **Resilience4j** (Retry, Circuit Breaker) pour la consommation d'API externes.
* **Notification** : `spring-boot-starter-mail` pour l'envoi d'emails transactionnels.
* **Documentation & DevOps** : Swagger UI (OpenAPI 3), Docker & Docker Compose.
* **Tests** : JUnit 5, Mockito, H2 (avec `@DataJpaTest` pour la validation des repositories JPA et requêtes personnalisées).

### Frontend (Angular 19)
* **Core Architecture** : Angular v19, Composants Standalone, Architecture par fonctionnalités (Feature modules).
* **Gestion d'État** : **Angular Signals** pour l'état local réactif, **NgRx** pour la centralisation du flux d'authentification (Session/Tokens).
* **Design & UI** : Tailwind CSS v4 & Angular Material.


---


## ✨ Fonctionnalités Principales

### 🔓 Authentification & Sécurité
* Connexion/Déconnexion sécurisées gérées via NgRx et cookies sécurisés.
* Changement de mot de passe et gestion du profil utilisateur.

### ⚙️ Administration & Paramétrage
* **Gestion des employés** : Administration des statuts et rôles.
* **Configuration Multi-Devises** : Paramétrage des monnaies prises en charge et des taux de change.
* **Types de Comptes** : Définition des règles de gestion, plafonds et taux d'intérêt.

### 👥 Gestion de la Clientèle
* Immatriculation et création de fiches clients.
* Data tables avec tri, recherche textuelle, pagination et vue détaillée.

### 💳 Gestion des Comptes & Opérations
* Ouverture de comptes multi-devises.
* Suivi strict du cycle de vie d'un compte : `Création ➡️ Activation ➡️ Suspension ➡️ Fermeture`.
* Dépôts, Retraits et Transferts de compte à compte.
* Historique des transactions avec export de relevés au format PDF.

### 📊 Tableau de Bord (Dashboard)
* KPI clés : Nombre de clients, nombre de comptes actifs.
* Consolidation du solde global en monnaie nationale (MGA).
* Répartition visuelle par type de compte et suivi graphique des nouvelles adhésions.

---


## 📐 Architecture Backend : Focus Hexagonal & Isolation Transactionnelle

L'application applique les principes de l'**Architecture Hexagonale / Clean Architecture** pour maintenir le noyau métier totalement isolé de dépendances techniques ou de frameworks.

Pour résoudre le défi du découplage vis-à-vis du framework Spring tout en conservant la gestion déclarative des transactions, le pattern **UseCaseProxy** a été mis en œuvre au niveau de la couche d'infrastructure.

```
              ┌─────────────────────────────────────────────────────────────┐
              │                        INFRASTRUCTURE                       │
              │                                                             │
              │   ┌──────────────┐                       ┌──────────────┐   │
              │   │     REST     │                       │  Spring Data │   │
              │   │ Controllers  │                       │ JPA Repos &  │   │
              │   └──────┬───────┘                       │Services Tiers│   │
              │          │                               └──────▲───────┘   │
              │          ▼ appeler                              │           │
              │   ┌──────────────┐                              │           │
              │   │ UseCaseProxy │ (Gestion du @Transactional)  │           │
              └───│──────┬───────│──────────────────────────────│───────────┘
                         │                                      │
                         │ appeler                              │ implémenter
                         ▼                                      │
              ┌──────────│──────────────────────────────────────│───────────┐
              │          │              APPLICATION             │           │
              │   ┌──────┴───────┐                              │           │
              │   │ Inbound Port │ (Interfaces des Use Cases)   │           │
              │   └──────┬───────┘                              │           │
              │          │                                      │           │
              │          ▼ implémenter                          │           │
              │   ┌──────────────┐                              │           │
              │   │ UseCaseImpl  │ (Services Applicatifs POJO)  │           │
              └───│──────┬───────│──────────────────────────────│───────────┘
                         │                                      │
                         │ utiliser                             │ appeler
                         ▼                                      │
              ┌──────────│──────────────────────────────────────│───────────┐
              │          │                 DOMAIN               │           │
              │          │                               ┌──────┴───────┐   │
              │          └──────────────────────────────►│Outbound Port │   │
              │                                          │ (Interfaces) │   │
              │                                          └──────────────┘   │
              │            Core Business Entities & POJOs                   │
              └─────────────────────────────────────────────────────────────┘

* **DOMAIN (Noyau métier pur) :** Contient les entités métiers pures, les Value Objects et les Outbound Ports (interfaces définissant les besoins de persistance et de services externes). Totalement indépendant de tout framework.
* **APPLICATION (Orchestration des Cas d'Usage) :** 
  * *Inbound Ports :* Interfaces définissant les contrats d'utilisation du système (Use Cases).
  * *UseCaseImpl :* Implémentations concrètes des Use Cases. Ce sont des POJOs pur Java sans aucune annotation Spring (pas de `@Service`, `@Transactional`, etc.), garantissant une isolation totale et une testabilité unitaire ultra-rapide.
* **INFRASTRUCTURE (Adaptateurs & Framework) :**
  * *Driving Adapters :* Contrôleurs REST Spring MVC, Jobs Spring Batch.
  * *Driven Adapters :* Implémentations des Repositories Spring Data JPA, adaptateurs REST (`RestClient` pour services tiers).
  * *transactional (Pattern UseCaseProxy) :* Composants de la couche infrastructure annotés `@Service` et `@Transactional`. Ils encapsulent l'appel aux Use Cases pour gérer les frontières de transaction au niveau de l'infrastructure Web sans polluer le noyau applicatif.
  * *config :* Configurations du framework (Spring Security, Spring Batch, RestClient) et instanciation explicite des beans `UseCaseImpl` via des méthodes `@Bean` pour les injecter dans le conteneur IoC.
```

---

## 🚀 Installation et Démarrage

### Prérequis
* **Java 17+**
* **Node.js v20+** & **Angular CLI v19+**
* **Docker & Docker Compose**

### 1. Cloner le projet
```bash
git clone https://github.com/rivohery/micro-finance.git
cd micro-finance
```

### 2. Option A : Démarrage complet avec Docker (Recommandé)
Lance l'ensemble des conteneurs (PostgreSQL, MailDev, Backend et Frontend) :
```bash
docker compose up -d --build
```
* **Frontend Angular** : `http://localhost:4200`
* **Swagger UI (API)** : `http://localhost:8088/api/v1/swagger-ui/index.html`
* **MailDev (Boîte mail de test)** : `http://localhost:1080`

---

### 3. Option B : Démarrage en Mode Développement Local

#### Étape 1 : Lancer la base de données et les services de support
```bash
docker compose up -d postgres maildev
```

#### Étape 2 : Lancer le Backend (Spring Boot)
```bash
cd backend
./mvnw spring-boot:run   # Linux / macOS
# ou
mvnw.cmd spring-boot:run # Windows
```

#### Étape 3 : Lancer le Frontend (Angular)
```bash
cd ../frontend
npm install
ng serve
```

---

## 🔑 Compte Administrateur par défaut

Pour tester l'application après le démarrage, utilisez l'identifiant administrateur pré-configuré :
* **Identifiant** : `alibou`
* **Mot de passe** : `0000`