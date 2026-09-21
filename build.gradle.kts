plugins {
    java
    id("org.springframework.boot") version "3.5.16" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("jvm") version "2.0.21" apply false
    kotlin("plugin.spring") version "2.0.21" apply false
    id("org.liquibase.gradle") version "2.2.2"
}

allprojects {
    group = "com.euphoriav"
    version = "1.0.0-SNAPSHOT"

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    repositories {
        mavenCentral()
    }
}

dependencies {
    liquibaseRuntime("org.liquibase:liquibase-core:4.29.0")
    liquibaseRuntime("org.postgresql:postgresql:42.7.3")
    liquibaseRuntime("info.picocli:picocli:4.7.6")
}

liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changelogFile" to "db/changelog/changelog-master.yaml",
            "url" to "jdbc:postgresql://localhost:5432/registry",
            "username" to System.getenv("POSTGRES_USER"),
            "password" to System.getenv("POSTGRES_PASSWORD"),
        )
    }
    runList = "main"
}