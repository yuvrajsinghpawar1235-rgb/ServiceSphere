# ServiceSphere ITSM Enterprise Platform

ServiceSphere is an enterprise-grade IT Service Management (ITSM) and Asset Governance platform engineered with a Spring Boot REST API backend and a React (Vite) single-page application frontend.

## Tech Stack
* **Backend:** Java 21, Spring Boot, Spring Data JPA / Hibernate, MySQL.
* **Frontend:** React, Vite, Axios, Custom Modular Components.
* **Database Management:** MySQL Workbench 8.0 / Community Edition.

## Core Features
1. **Incident & Ticket Lifecycle:** Paginated filtering, priority constraints, and secure state transitions.
2. **Audit Trails & Comments:** Automated event logging via JPA entity listeners (`TicketHistory`) and public/internal comment feeds.
3. **Asset Governance:** Inventory tracking, lifecycle assignment/return rules, and warranty expiry alerts.
4. **Knowledge Base:** Role-enforced visibility (ordinary users see only published articles; staff can review drafts and archives).
5. **Live Operations Reports:** Server-side aggregated metrics for ticket summaries, SLA compliance rates, and agent workloads.

## Demo Accounts & Credentials
* **System Administrator:** `admin@servicesphere.com` | Password: `pass123` (Role ID: 5)
* **Support Agent:** `agent@servicesphere.com` | Password: `pass123` (Role ID: 2)
* **Support Manager:** `manager@servicesphere.com` | Password: `pass123` (Role ID: 3)
* **Employee / Requester:** `employee@servicesphere.com` | Password: `pass123` (Role ID: 1)

## Setup & Running Instructions
### Backend Setup
1. Configure your MySQL database connection in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/servicesphere?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update