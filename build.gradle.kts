plugins {
    id("java")
}

group = "ru.epphy"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // JDA
    implementation("net.dv8tion:JDA:5.4.0")

    // Gson
    implementation("com.google.code.gson:gson:2.13.0")

    // SLF4J
    implementation("org.slf4j:slf4j-api:2.0.17")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    // Unit test
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}