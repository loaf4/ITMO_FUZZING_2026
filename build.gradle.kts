plugins {
    id("java")
    id("antlr")
}

group = "org.itmo.fuzzing"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    implementation(project(":instrumentation"))
    implementation("org.ow2.asm:asm:9.9")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("com.github.javaparser:javaparser-core:3.26.2")
    implementation("org.antlr:antlr4-runtime:4.13.1")
    // генератор парсеров из src/main/antlr/**/*.g4 (версия = runtime, иначе checkVersion в сгенерированном коде ругается)
    antlr("org.antlr:antlr4:4.13.1")
    // lab2: цель фаззинга — JSON-парсер Gson
    implementation("com.google.code.gson:gson:2.11.0")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// lab2: ANTLR-грамматики из src/main/antlr/org/itmo/fuzzing/lab2/parser/*.g4 → пакет org.itmo.fuzzing.lab2.parser.
// Плагин сам повторяет подпапки src/main/antlr в build/generated-src/antlr/main/, поэтому
// исходники окажутся в build/generated-src/antlr/main/org/itmo/fuzzing/lab2/parser/.
tasks.generateGrammarSource {
    arguments = arguments + listOf("-package", "org.itmo.fuzzing.lab2.parser", "-visitor")
}

// Runs a main class with the coverage java agent attached.
// Usage: ./gradlew runWithAgent -PmainClass=org.itmo.fuzzing.lect3.Main
tasks.register<JavaExec>("runWithAgent") {
    group = "application"
    description = "Runs the application with the coverage java agent"

    val agentJar = project(":instrumentation").tasks.named<Jar>("agentJar")
    dependsOn(agentJar)

    mainClass = (project.findProperty("mainClass") as String?) ?: "org.itmo.fuzzing.lect2.MutationCoverageFuzzer"
    classpath = sourceSets.main.get().runtimeClasspath

    jvmArgumentProviders.add(CommandLineArgumentProvider {
        listOf("-javaagent:${agentJar.get().archiveFile.get().asFile.absolutePath}")
    })
}
