plugins {
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // redis
    api("org.springframework.boot:spring-boot-starter-cache")
    api("org.springframework.boot:spring-boot-starter-data-redis")
}
