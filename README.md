# 🎓 Doctoral Monitoring Portal — EMSI

A centralized web platform to digitize and optimize the doctoral journey: registration, monitoring, and thesis defense.

---

## 📋 Context

The lifecycle of a doctoral student involves many administrative steps traditionally handled manually (emails, paper documents), leading to errors, delays, and lack of visibility. This project addresses this need by providing a modern web application covering initial registration, annual re-registration, and the thesis defense process.

---

## 🎯 Objectives

- Simplify registration and thesis defense requests for doctoral students
- Streamline validation steps (thesis director, administration)
- Centralize all data and documents related to each doctoral student's journey
- Automatically verify prerequisites for defense (publications, training hours)
- Provide real-time visibility on the status of each application

---

## 👥 Users

| Role | Description |
|------|-------------|
| **Doctoral Student** | Submits applications, tracks progress, uploads thesis manuscript, requests defense |
| **Supervisor / Thesis Director** | Monitors doctoral students, validates applications, proposes jury members |
| **Administrator** | Validates applications, manages the global process, schedules defenses |

---

## ⚙️ Features

### Module 1 — Authentication
- Account creation for new candidates
- Secure login / logout
- Role and permission management (DOCTORANT, ENCADRANT, ADMINISTRATEUR)
- Automatic redirect based on role after login

### Module 2 — Registration Process
- Registration form with personal information and thesis subject
- Validation circuit: Student → Supervisor → Administration
- Real-time tracking of application status
- Personalized dashboard per role

### Module 3 — Defense Process
- Defense request initiated by the doctoral student
- Automatic prerequisite verification:
  - At least 2 indexed journal articles (Q1/Q2)
  - At least 2 conferences
  - 200 hours of doctoral training completed
- Jury proposal by the supervisor
- Validation and scheduling by the administration (date, room)

---

## 📏 Business Rules

- A doctoral student cannot re-register after 3 years without special exemption
- Maximum doctoral duration is 6 years (system alert when limit is reached)
- Defense request is only possible if all prerequisites are met
- The jury is proposed by the thesis director and validated by the administration

---

## 🏗️ Architecture

```
src/main/java/com/emsi/doctorat_portal/
├── controllers/
│   ├── AdminController.java
│   ├── AuthController.java
│   ├── DoctorantController.java
│   ├── EncadrantController.java
│   └── HomeController.java
├── entities/
│   ├── User.java
│   ├── Doctorant.java
│   ├── Publication.java
│   ├── Soutenance.java
│   └── Document.java
├── services/
│   ├── DoctoratService.java
│   ├── DoctoratServiceImpl.java
│   ├── SoutenanceService.java
│   ├── DocumentService.java
│   └── CustomUserDetailsService.java
├── repositories/
├── enums/
│   ├── Role.java
│   ├── StatutDossier.java
│   └── StatutSoutenance.java
└── config/
    └── SecurityConfig.java
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3.x, Spring MVC, Spring Data JPA, Spring Security |
| Frontend | Thymeleaf, Bootstrap 5 |
| Database | MySQL |
| Security | BCrypt password hashing |
| Build | Maven |
| Version Control | Git / GitHub |
| IDE | IntelliJ IDEA |

---

## 🗄️ Data Model

- `User` ↔ `Doctorant` — @OneToOne — one account = one doctoral profile
- `Doctorant` → `User` (supervisor) — @ManyToOne — multiple students per supervisor
- `Doctorant` → `Publication` — @OneToMany — one student has multiple publications
- `Doctorant` ↔ `Soutenance` — @OneToOne — one defense per student
- `Doctorant` → `Document` — @OneToMany — multiple documents per student

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- MySQL

### Steps

1. Clone the repository
```bash
git clone https://github.com/ilias2312/Portail-Doctorat-Backend.git
cd Portail-Doctorat-Backend
```

2. Configure the database in `application.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/doctorat_portal
    username: root
    password: your_password
```

3. Create the MySQL database
```sql
CREATE DATABASE doctorat_portal;
ALTER TABLE doctorants MODIFY COLUMN statut VARCHAR(50);
ALTER TABLE soutenances MODIFY COLUMN statut VARCHAR(50);
```

4. Run the application
```bash
mvn spring-boot:run
```

5. Open your browser at `http://localhost:8080`

---

## 🔐 Test Accounts

| Role | Email | Password |
|------|-------|----------|
| Administrator | admin@emsi.ma | password |
| Supervisor | encadrant@emsi.ma | password |
| Doctoral Student | doctorant@emsi.ma | password |

---

## 📁 Project Specifications

Project developed as part of the JEE course — EMSI


---
