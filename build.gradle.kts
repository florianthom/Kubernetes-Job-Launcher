plugins {
	java
	id("org.springframework.boot") version "4.0.2-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.florianthom"
version = "0.0.1-SNAPSHOT"
description = "Demonstrating how to create and manage Kubernetes Jobs programmatically"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-json:4.0.1")

    implementation("io.kubernetes:client-java:25.0.0")
    implementation("io.kubernetes:client-java-api:25.0.0")

    implementation("software.amazon.awssdk:sqs:2.31.13")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
