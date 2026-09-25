# Getting Started - Installation

Eng / [한글](./Installation.ko-KR.md)

## I. Clone Repository

Below is downloading source code.

```
git clone https://github.com/sindohmes/mes4u.git
```

## II. PostgreSQL Setup

The first thing is to install PostgreSQL. The tested versions are listed as below. If you want to test another version, please contact us.

+ PostgreSQL 9.6.18 (OS: Red Hat 4.8.5-39, 64-bit)
+ PostgreSQL 9.6.19 (OS: Windows 10, 64-bit)
+ PostgreSQL 12.4 (OS: Windows 10, 64-bit)
+ PostgreSQL 17.7 (OS: Windows 11, 64-bit)

[PostgreSQL Download Link](https://www.postgresql.org/download/)

After completing the installation, run pgAdmin. You can create a user and database using the below command.

```PostgreSQL
CREATE USER mesuser PASSWORD 'your_db_password';
CREATE DATABASE nsmes OWNER 'mesuser';
ALTER DATABASE nsmes SET search_path TO nsmes;
```

The schema creation script creates a schema named 'nsmes', so the DB's search_path must be set to 'nsmes' as above.

You can use your own user and DB names other than 'mesuser' and 'nsmes'. In this case, however, DB Schema creation script and 'application.properties' file in Spring Framework should be modified accordingly (the DB name can be freely chosen, but 'search_path' must match the schema name in the script).

After competing user and DB registration, download the schema creation script and run it in PostgreSQL. This script includes essential INSERT script for making system data including Table, View, Sequence, Function and Index. When you execute the script, please connect by 'mesuser' account.

[PostgreSQL Schema Creation Script Download Link](./pgschemascript.sql)

## III. Java Spring Framework Setting

mes4u is an open-source software built using Spring Framework. It is based on Gradle project (Spring Boot 2.7, Java 17).

JDK 17 is required to build and run. Install JDK 17 and set it as the project's JDK (JAVA_HOME or your IDE's Gradle JVM setting). Gradle itself does not need to be installed; the included Gradle Wrapper (gradlew) downloads it automatically.

The directory structure is below.

![Backend Directory](./images/be_directory.png)

+ src/main/java: Back-end Java source code
+ src/main/resources/com: MyBatis XML 
+ src/main/resources/static: Front-end Deployment Directory
+ src/main/resources/application.properties: Spring configuration and PostgreSQL connection setup file
+ frontend: Front-end Vue.js source code and Front-end environments
+ build: deployment directory for WAR and Class (created by Gradle build)
+ build.gradle: Gradle Project configuration file
+ settings.gradle: Gradle Project name setting file
+ gradlew, gradlew.bat, gradle: Gradle Wrapper

In the list above, you can check the configuration from 'application.properties' and 'build.gradle' files.

### 1. application.properties

This file specifies Spring Framework configuration data. It is mainly used for PostgreSQL connection.

> **NOTE:** 'application.properties' is not stored in git (it is listed in '.gitignore') because it holds your own DB account and JWT secret. Create it yourself by copying the template before the first run:
>
> ```
> cp src/main/resources/application.properties.example src/main/resources/application.properties
> ```
>
> (Windows: `copy src\main\resources\application.properties.example src\main\resources\application.properties`) Then edit the DB URL, username, password and 'sdmes.app.jwtSecret' in the copied file.

```
spring.datasource.hikari.maximum-pool-size=4
spring.datasource.url=jdbc:postgresql://localhost:5433/nsmes
spring.datasource.username=mesuser
spring.datasource.password=your_db_password

spring.datasource.tomcat.max-wait=10000
spring.datasource.tomcat.max-active=20

spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation= true
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.PostgreSQLDialect
spring.jpa.open-in-view=false

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto= update
spring.data.jdbc.repositories.enabled=false

mybatis.mapper-locations=com/sindoh/sdmes/mapper/*.xml

# Project Custom Values
sdmes.datasource.db=nsmes
sdmes.app.jwtSecret=change-this-to-a-long-random-string
sdmes.app.jwtExpirationMs=86400000
```



Change the port (5433) and DB name (nsmes) in 'spring.datasource.url' to match your PostgreSQL (the PostgreSQL default port is 5432). 'sdmes.datasource.db' is the DB name passed to the report (label printing) procedure, so set it to the same DB name as in the URL. It is not used for the DB connection itself.

### 2. build.gradle

This file specifies libraries used by Spring Boot - Gradle Project. Currently it includes the following libraries:

+ Spring JDBC
+ Spring JPA
+ Spring Security
+ Spring MyBatis
+ JsonWebtoken
+ Jackson
+ Devtools
+ PostgreSQL
+ Lombok
+ Swagger-UI (springdoc-openapi)

If you want to add more libraries, just add them into 'dependencies' of 'build.gradle'. After that, run 'Gradle - Refresh Gradle Project' in your IDE (or any Gradle task) to download and use the libraries.

The Swagger-UI page is available at http://localhost:8080/swagger-ui.html and the API docs (OpenAPI) at http://localhost:8080/v3/api-docs.

NOTE: The JWT signing key is derived from 'sdmes.app.jwtSecret' (SHA-512). Set it to a long random string, and use a different value for each environment.

## IV. Vue.js Setting

Front-end Framework in mes4u is based on Vue.js and use npm for the installation and management. If you are unable to use npm, you must install node.js instead using the download link below.

[Node.js Downlaod Link](https://nodejs.org/ko/) (Node.js 20.19+ or 22.12+ is required for Vite)

Since Java WAR file and Vue.js build deployment files have been already uploaded, you can see and test the web page without any additional setup. To modify Vue.js code, the following steps should be carried out.

+ Vue.js dev-environment installation
```
npm install
```

+ Vue.js dev-environment execution
```
npm run dev
```

+ Vue.js build and deployment
```
npm run build
```

Vue.js environment directory is organized as below.

![Frontend Directory](./images/fe_directory.png)

+ build: used for build, should not be modified
+ node_modules: created when you install dev-environment; this directory does not exist in Git
+ public: public HTML file, should not be modified
+ src: Vue.js development source code files, adding or modifying codes is carried out in this directory
+ package.json: Vue.js libraries and configuration file
+ vite.config.js: dev and build environment file
+ .env.development: Base URL files to connect Back-end API in dev environment
+ .env.production: Base URL files to connect Back-end API in deployment environment

mes4u Vue.js uses the UI and Template below.

+ [Element Plus: MIT License](https://element-plus.org)
+ [vue-element-admin: MIT License](https://github.com/PanJiaChen/vue-element-admin)

## V. mes4u execution

mes4u can be executed by Spring Boot App of STS4 (Spring Tool Suite), by Gradle, or by executing the 'ROOT.war' file in 'build/libs' directory.

```
./gradlew bootRun
```

To create the WAR file (build/libs/ROOT.war) and run it:

```
./gradlew bootWar
java -jar build/libs/ROOT.war
```

On Windows, use 'gradlew.bat' instead of './gradlew'.

Default setup URL is http://localhost:8080. If you want to use another server, run it in Tomcat Server. When connected to http://localhost:8080, the following screen is shown.

![Login Page](./images/login_page.png)

After PostgreSQL installation and DB connection is completed, you can see the login page in Web. Log into mes4u using the default user name and password below.

+ ID: administrator
+ PW: administrator

After login, you are redirected to the main page where you can use various MES features.

![Dashboard](./images/dashboard.png)
