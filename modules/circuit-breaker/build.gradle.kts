plugins {
    `java-library`
}

dependencies {
    api("io.github.resilience4j:resilience4j-spring-boot3")
    api("io.github.resilience4j:resilience4j-annotations:2.2.0")
    api("io.github.resilience4j:resilience4j-circuitbreaker:2.2.0")
    api("io.github.resilience4j:resilience4j-timelimiter:2.2.0")
}
