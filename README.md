# AgileExpert Smart Device Menu System

This repository contains my solution for the AgileExpert assignment.

It includes:

- a Spring Boot backend with JPA and PostgreSQL
- a Vue 3 dashboard for the fullstack part
- a CLI mode for the original console workflow

The system models a shared smart-device platform where each user can customize their own device menu, folders, shortcuts, theme, and wallpaper while data is stored in one central backend.

## Tech Stack

- Backend: Java 17, Spring Boot, Spring Data JPA, PostgreSQL, MapStruct, Lombok
- Frontend: Vue 3, Vite, Element Plus, Axios
- Tooling: Maven, Docker Compose, optional nginx reverse proxy

## Project Structure

```text
agileExpert/
  src/
  pom.xml
  docker-compose.yml

agileExpert-frontend/
  src/
  package.json
  nginx/default.conf
```

## Requirements

- Java 17+
- Maven
- Node.js 18+
- npm
- Docker

## Run The Project

### 1. Start PostgreSQL

```powershell
cd agileExpert
docker compose up -d
```

Default local database values:

- database: `agileexpert`
- user: `user`
- password: `123456`

The backend also supports environment variable overrides for:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

### 2. Start the backend API

```powershell
cd agileExpert
mvn spring-boot:run
```

Backend URL:

- [http://localhost:8080](http://localhost:8080)

### 3. Start the frontend

```powershell
cd agileExpert-frontend
npm install
npm run dev
```

Frontend URL:

- [http://localhost:5173](http://localhost:5173)

### 4. First use on a fresh database

After the backend starts with an empty database, the system creates one default user automatically:

- `Father`

If you want a richer demo state after that, click `Simulation` to replace the current data with demo users and menus.

## CLI Mode

The backend also supports the CLI version through a Spring profile:

`PowerShell`

```powershell
cd agileExpert
mvn spring-boot:run "-Dspring-boot.run.profiles=cli"
```

`cmd`

```cmd
cd agileExpert
mvn spring-boot:run -Dspring-boot.run.profiles=cli
```

## Main Rules

- Maximum users: `5`
- At least one user must remain in the system
- Themes are fixed to `Light` and `Dark`
- Users cannot create custom themes
- New users get the default home screen only when they are created
- Restarting the application does not rebuild existing user menus
- Default home screen for a new user:
  - root apps: `Camera`, `Google`
  - folders: `Games`, `Social`, `Tools`
  - folder apps: `Valorant`, `Messenger`, `Word`
- The same app cannot appear twice in one user's menu tree
- Deleting an app is blocked while any shortcut still references it
- Editing an app in Settings updates the shared app definition for all users
- Renaming a menu item from the device screen only changes that shortcut or folder label

## Wallpapers

- Web dashboard: upload a photo, give it a display name, then assign it to a user
- CLI: register a wallpaper by display name and existing file path
- Uploaded files are stored under `agileExpert/uploads/wallpapers`

## Optional nginx Setup

An example nginx config is included at:

```text
agileExpert-frontend/nginx/default.conf
```

It proxies:

- `/api` to the backend
- `/uploads` to backend static wallpaper files
