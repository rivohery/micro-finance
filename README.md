# Système de Micro-Finance Multi-Monétaire (Fullstack Portfolio)

Application web fullstack de **Microfinance Multi-devises** conçue pour illustrer les bonnes pratiques d'ingénierie logicielle : **Architecture Hexagonale**, **traitements par lots (Spring Batch)**, **sécurité renforcée (JWT via Cookies HttpOnly + CSRF)** et **composants réactifs Angular 19 (Signals) et **Standalone**.


**Démo rapide des fonctionnalités** : `./screenshots/demo.gif`


---


## 🛠️ Stack Technique

### Backend (Java / Spring Boot)
* **Architecture & Core** : Java 17, Spring Boot 3.x, Architecture Hexagonale (Ports & Adapters).
* **Sécurité** : Spring Security, JWT stocké dans un Cookie `HttpOnly` (protection XSS) avec validation active de tokens CSRF.
* **Persistance & Données** : PostgreSQL, Spring Data JPA, Liquibase (versioning et migrations de schémas).
* **Batchs & Automatisation** : Spring Batch orchestré par `@Scheduled` pour :
  * le calcul et la capitalisation automatique mensuelle des intérêts sur grand volume de comptes ;
  * le recalcul hebdomadaire du solde consolidé en MGA.
* **Résilience & Services Tiers** : `RestClient` sécurisé avec **Resilience4j** (Retry, Circuit Breaker) pour la consommation d'API externes.
* **Notification** : `spring-boot-starter-mail` pour l'envoi d'emails transactionnels.
* **Documentation & DevOps** : Swagger UI (OpenAPI 3), Docker & Docker Compose.
* **Tests** : JUnit 5, Mockito, AssertJ, H2 (tests locaux), Testcontainers (CI GitHub Actions), `@DataJpaTest` pour la validation des repositories JPA et requêtes personnalisées.

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

**Note** : ce projet est un projet portfolio. Il vise un équilibre entre réalisme métier et périmètre maîtrisable pour un développeur junior. Les simplifications volontaires sont documentées dans la section [Limitations connues](#limitations-connues).

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

## 📐 Règles métier

### Statuts de compte et éligibilité aux intérêts

| Statut | Reçoit des intérêts ? | Justification |
|---|---|---|
| `ACTIVE` | ✅ Oui | Compte en fonctionnement normal |
| `PENDING` | ❌ Non | Compte en cours d'ouverture, pas encore actif |
| `SUSPENDED` | ❌ Non | Compte suspendu — le calcul des intérêts est arrêté |
| `CLOSED` | ❌ Non | Compte fermé, plus de mouvement possible |

> **Note** : la règle `SUSPENDED` reflète la pratique courante en microfinance : lorsqu'un compte est suspendu (fraude, gel administratif, non-respect du contrat), le calcul des intérêts est interrompu. Pour les comptes dormants, la pratique standard est également de suspendre le paiement des intérêts.

### Types de comptes éligibles

| Code | Type | Taux annuel (paramètrable) |
|---|---|---|
| `10` | Compte courant | 0 % (non éligible) |
| `20` | Compte épargne | 3,65 % |
| `30` | Compte business | 7,30 % |

### Calcul des intérêts

Le calcul suit la méthode **des soldes moyens pondérés par le temps** :

1. Pour chaque segment entre deux transactions du mois, on calcule :
   `intérêt_segment = solde_avant_deuxième_transaction × taux_journalier × nombre_de_jours_entre_transaction`
2. On somme les intérêts de tous les segments du mois.
3. Le total est ajouté au solde du compte (capitalisation).
4. Une trace `InterestRateTrace` est créée avec le montant et sa conversion en MGA.

**Convention de jours** : `ChronoUnit.DAYS.between()` — convention Actual/Actual (à titre indicatif, à paramétrer pour une mise en production réelle).

### Conversion de devise

Tous les intérêts sont convertis en **MGA** (Ariary malgache), devise de référence du système, pour permettre un reporting centralisé. Le taux de change est fourni par un port `CurrencyExchangePort`.

---

## 🔄 Traitements par lots (Spring Batch)

Deux jobs batch assurent les traitements périodiques du système. Ils sont déclenchés par `@Scheduled` et orchestrés par Spring Batch.

### Job 1 — `JobCalculInteretFinDeMois` (mensuel)

Calcule et capitalise les intérêts sur tous les comptes éligibles (épargne et business), puis enregistre une trace `InterestRateTrace` avec le montant et sa conversion en MGA.

| Élément | Configuration |
|---|---|
| **Fréquence** | Mensuelle (fin de mois) |
| **Reader** | `JpaPagingItemReader` — pagination de 100 comptes, mono-thread |
| **Filtres** | `accountType.code IN ('20', '30')` ET `accountStatus NOT IN ('CLOSED', 'PENDING', 'SUSPENDED')` |
| **Processor** | `InterestItemProcessor` — délègue au use case `AddMonthlyInterestUseCase` |
| **Writer** | `JpaItemWriter` — persistance par lots |
| **Chunk size** | 100 (1 transaction par paquet de 100 comptes) |
| **Retry** | 3 tentatives sur `DataAccessResourceFailureException` (erreur réseau/DB transitoire) |
| **Restart** | `saveState(true)` — reprise depuis le dernier `ExecutionContext` sauvegardé |

### Job 2 — `JobCalculSoldeMgaFinDeSemaine` (hebdomadaire)

Recalcule le solde consolidé de chaque compte en MGA (devise de référence), pour le reporting centralisé et l'alimentation du dashboard.

| Élément | Configuration |
|---|---|
| **Fréquence** | Hebdomadaire (fin de semaine) |
| **Reader** | `JpaPagingItemReader` — pagination de 100 comptes, mono-thread |
| **Filtres** | Comptes `ACTIVE` ou `SUSPENDED` (le solde reste suivi même si les intérêts sont gelés) |
| **Processor** | `MgaBalanceItemProcessor` — conversion via `CurrencyExchangePort` |
| **Writer** | `JpaItemWriter` — mise à jour du champ `mgaBalance` |
| **Chunk size** | 100 |
| **Retry** | 3 tentatives sur `DataAccessResourceFailureException` |
| **Restart** | `saveState(true)` |

> **Note** : les deux jobs sont indépendants et peuvent être relancés séparément. Le `JobRepository` persistant garantit la traçabilité des exécutions et la reprise après incident.

> **Note sur la conversion de devise** : le job hebdomadaire `JobCalculSoldeMgaFinDeSemaine` utilise le port `CurrencyExchangePort`, implémenté par un adaptateur qui interroge une **API publique gratuite**. Ce choix est acceptable pour un projet portfolio, mais ne conviendrait pas en production (voir [Limitations connues](#limitations-connues)).

---

## 🧪 Tests

### Stratégie

| Niveau | Type | Environnement | Ce qui est vérifié |
|---|---|---|---|
| Domaine | Unitaires | JUnit + Mockito | Règles de calcul, invariants, value objects |
| Application | Unitaires | JUnit + Mockito | `AddMonthlyInterestServiceApplication` : cas mono-transaction et multi-transactions |
| Infrastructure | `@DataJpaTest` | H2 (local) / Testcontainers (CI) | Requêtes JPQL du reader, les méthodes dans JpaRepository|
| Infrastructure | `@SpringBootTest` | H2 (local) / Testcontainers (CI) | Configuration du job batch, effets en base, conversion de devise |
| Sécurité | `@SpringBootTest` + `MockMvc` | H2 (local) / Testcontainers (CI) | Login et contrôle d'accès sur un contrôleur représentatif |

### Environnements de test

| Environnement | Base de données | Usage |
|---|---|---|
| **Local** | H2 (en mémoire) | Exécution rapide, itération quotidienne |
| **CI (GitHub Actions)** | Testcontainers + PostgreSQL | Validation proche de la production, isolation garantie |

> Le choix H2 en local privilégie la vitesse d'exécution ; Testcontainers en CI garantit que le code passe dans les mêmes conditions qu'en production (même moteur, mêmes types de données, mêmes contraintes).

### Couverture actuelle

| # | Test | Statut | Ce qui est vérifié |
|---|---|---|---|
| 1 | Use cases (succès + exception) | ✅ Couvert | Logique métier : cas nominal et cas d'erreur |
| 2 | Repositories (`@DataJpaTest`) | ✅ Couvert | Requêtes JPQL personnalisées, filtres |
| 3 | Wiring du job batch | ✅ Couvert | `JobExecution.getStatus() == COMPLETED`, reader/processor/writer branchés |
| 4 | Pagination batch | ✅ Couvert | Création de > `pageSize` comptes, vérification `readCount` / `writeCount` / `commitCount` via `StepExecution` |
| 5 | Sécurité (login + 1 contrôleur) | ✅ Couvert | Authentification (login) et contrôle d'accès sur **un contrôleur représentatif** |
| 6 | Concurrence (verrouillage optimiste) | ✅ Couvert | Gestion des accès concurrents via `@Version` |
| 7 | Rollback / restart / retry | ❌ Non couvert | Comportements du framework Spring Batch (voir [Limitations connues](#limitations-connues)) |

### Précisions sur le test de sécurité (#5)

Le test de sécurité couvre **volontairement un périmètre restreint** :

- ✅ Le **login** (génération du cookie JWT, validation des credentials)
- ✅ Le **contrôle d'accès** sur **un contrôleur représentatif** (401 sans cookie, 403 avec mauvais rôle)

Ce qui n'est **pas testé** : la totalité des endpoints, les cas de token expiré, la rotation de token, les attaques CSRF, etc.

> **Pourquoi ce périmètre restreint ?** Spring Security est un framework dont le comportement est largement couvert par ses propres tests. Écrire un test exhaustif revient à valider la librairie. On vérifie ici que **notre configuration** est correctement câblée sur un cas représentatif, pas que Spring Security fonctionne.

---

## ⚠️ Limitations connues

Cette section documente les choix de périmètre assumés et les fonctionnalités non couvertes par des tests. Elle est volontairement transparente : un projet portfolio qui prétend tout couvrir est souvent un projet qui ne teste rien en profondeur.

### Comportements Spring Batch non testés

- ❌ **Rollback par chunk en cas d'échec au milieu d'un chunk** : Spring Batch garantit contractuellement qu'un chunk est commité ou rollbacké atomiquement. Tester ce comportement revient à valider le framework, pas le code métier.
- ❌ **Restart depuis le dernier `ExecutionContext`** : les conditions nécessaires sont remplies (reader `saveState(true)`, `ORDER BY a.id ASC` pour une pagination stable, `JobRepository` persistant). Le comportement de reprise n'est pas testé.
- ❌ **Retry sur `DataAccessResourceFailureException`** : configuré (`retryLimit(3)`), mais non couvert par un test qui simule une erreur transitoire.

### Sécurité : couverture partielle

- ❌ **Tests exhaustifs Spring Security** : seuls le login et un contrôleur représentatif sont testés. La totalité des endpoints, l'expiration de token et la rotation ne sont pas couverts.

### Simplifications volontaires

- **Service de taux de change gratuit** : l'adaptateur `CurrencyExchangePort` interroge une **API publique gratuite** pour récupérer les taux de change. En production, il faudrait une source fiable avec SLA (banque centrale, fournisseur payant), une gestion de cache, et une gestion d'erreur robuste.
- Les **taux de change sont mockés** dans les tests. En production, l'adaptateur réel interroge l'API publique.
- La **convention de calcul des jours** est `Actual/Actual`. En microfinance réelle, les conventions `Actual/365`, `Actual/360` ou `30/360` sont fréquentes et devraient être configurables par type de compte.
- Le **taux d'intérêt est stocké sur le type de compte** et non historisé. Un vrai système conserverait un historique des taux par période.

### Pistes d'amélioration

1. Ajouter un test de rollback : forcer une exception au milieu d'un chunk et vérifier via `StepExecution` (`getReadCount()`, `getWriteCount()`, `getCommitCount()`, `getRollbackCount()`) et en base que seuls les chunks précédents sont commités.
2. Ajouter un test de restart : relancer le job avec des `JobParameters` identiques après un échec et vérifier que la lecture reprend au-delà du dernier chunk commité.
3. Paramétrer les conventions de calcul de jours (`daysInYear`, `daysInMonth`) sur `AccountTypeEntity`.
4. Historiser les taux d'intérêt par période.
5. Remplacer l'API publique de taux de change par un fournisseur fiable (SLA, cache, gestion d'erreur).

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