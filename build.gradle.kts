plugins {
    java
    `maven-publish`
}

group = "com.github.snowykte0426"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2")
    
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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