plugins {
  id("org.springframework.boot") version "2.6.5"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm") version "1.6.10"
  kotlin("plugin.spring") version "1.6.10"
  application
}

application {
  mainClass.set("CandleSticksMain")
}

group = "org.traderepublic.candlesticks"
version = "1.1.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
}

object DependencyVersions {
  const val coroutines = "1.5.2"
  const val http4k = "4.13.1.0"
  const val jackson = "2.13.+"
  const val mockk = "1.12.0"
}

dependencies {
  implementation(kotlin("stdlib"))
  testImplementation(kotlin("test"))


  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-validation")

  implementation(platform("org.http4k:http4k-bom:4.13.1.0"))
  implementation("org.http4k:http4k-core")
  implementation("org.http4k:http4k-server-netty")
  implementation("org.http4k:http4k-client-websocket:${DependencyVersions.http4k}")
  implementation("org.http4k:http4k-format-jackson:${DependencyVersions.http4k}")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.coroutines}")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${DependencyVersions.jackson}")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${DependencyVersions.jackson}")
  testImplementation("io.mockk:mockk:${DependencyVersions.mockk}")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
  useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
