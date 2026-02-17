# ShelfLife API

A robust, offline-first ready backend for the ShelfLife mobile app. Built as a Modular Monolith using Spring Boot and
Kotlin.

## 🚀 Features

### 🏗 Architecture

* **Modular Monolith**: Clean separation of concerns between `app`, `common`, `user`, `pantry`, `insight`, and
  `notification` modules.
* **Event-Driven**: Uses **RabbitMQ** for asynchronous communication between modules (e.g., User events triggering
  Emails).
* **Scalable**: Uses **Redis** for high-performance IP and Email rate limiting (Lua scripts).

### 👤 User Module

* **Authentication**: Custom JWT implementation with Access & Refresh tokens.
* **OAuth2**: Google Sign-In verification.
* **Security**: Role-based access control and API Key management.
* **Rate Limiting**: Advanced Lua-script-based IP and Email throttling to prevent abuse.

### 📦 Pantry Module

* **Inventory Management**: Full CRUD for pantry items.
* **Smart Storage**: Integrated with **Supabase Storage** (S3) for efficient image handling.
* **Sync Support**: Endpoints designed for client-side offline-sync logic (keeping images alive during moves).

### 📈 Insight Module

* **History Tracking**: Logs consumed and wasted items.
* **Batch Operations**: Optimized batch deletion for clearing history efficiently.

### 🔔 Notification Module

* **Email Service**: HTML email templates (Verification, Password Reset) sent via SMTP (Brevo).
* **Async Processing**: Listens to RabbitMQ queues to send emails without blocking the main API.

## 🛠️ Tech Stack

* **Language**: Kotlin (JDK 21)
* **Framework**: Spring Boot 4.0.2
* **Database**: PostgreSQL (via Supabase)
* **Rate Limiting**: Redis
* **Message Queue**: RabbitMQ
* **Storage**: Supabase Storage
* **Build Tool**: Gradle (Kotlin DSL)

## ️Getting Started

### Prerequisites

* JDK 21 installed.
* **Redis** installed and running locally (Default port: 6379).
* **RabbitMQ** installed and running locally (Default port: 5672).
* A running **PostgreSQL** database (Local or Supabase).

### Configuration

Create a `secrets.properties` file in the root directory (or set these as Environment Variables) to configure the app:

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_db_password

# Mail (Brevo/Mailgun)
SHELFLIFE_MAIL_HOST=smtp-relay.brevo.com
SHELFLIFE_MAIL_USERNAME=your_email
SHELFLIFE_MAIL_PASSWORD=your_smtp_key

# Supabase (Storage)
SUPABASE_URL=[https://your-project.supabase.co](https://your-project.supabase.co)
SUPABASE_KEY=your_service_role_key

# Security
JWT_SECRET=your_long_random_secret_string
GOOGLE_CLIENT_ID=your_google_client_id

# Infrastructure (Defaults)
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672