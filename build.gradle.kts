plugins {
    java
    kotlin("jvm") version "1.9.25"
    `maven-publish`
}

group = "com.github.snowykte0426"
version = "1.1.3"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    api("org.slf4j:slf4j-api:2.0.9")
    

    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    compileOnly(kotlin("reflect"))

    compileOnly("org.springframework.boot:spring-boot-starter:3.1.5")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor:3.1.5")
    compileOnly("org.springframework:spring-web:6.2.8")

    compileOnly("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    compileOnly("org.glassfish:jakarta.el:4.0.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation(kotlin("reflect"))
    testImplementation("org.springframework.boot:spring-boot-starter:3.1.5")
    testImplementation("org.springframework.boot:spring-boot-configuration-processor:3.1.5")
    testImplementation("jakarta.validation:jakarta.validation-api:3.0.2")
    testImplementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    testImplementation("org.glassfish:jakarta.el:4.0.2")
    testImplementation("ch.qos.logback:logback-classic:1.5.13")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("peanut-butter")
                description.set("A comprehensive utility library providing essential tools and helper functions for Java development")
                url.set("https://github.com/snowykte0426/peanut-butter")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("snowykte0426")
                        name.set("Kim Tae Eun")
                        email.set("snowykte0426@naver.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/snowykte0426/peanut-butter.git")
                    developerConnection.set("scm:git:ssh://git@github.com/snowykte0426/peanut-butter.git")
                    url.set("https://github.com/snowykte0426/peanut-butter")
                }
            }
        }
    }
}

tasks.wrapper {
    gradleVersion = "8.10"
    distributionType = Wrapper.DistributionType.ALL
}