plugins {
    id("java")
    id("maven-publish")
    id("jacoco")

    // 🛑 CORREÇÃO: Usando o ID e a versão CORRETOS conforme a documentação oficial.
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "megalodonte"
version = "1.0.0-beta"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    // gerar javadoc
    withSourcesJar()
    withJavadocJar()
}


// 🛑 2. CONFIGURA O PLUGIN DO JAVAFX
javafx {
    // Define a versão do JavaFX para ser usada em todos os módulos
    version = "17.0.10" // Mantida a versão 17.0.10.

    // Lista os módulos JavaFX que sua biblioteca PRECISA para compilar.
    // O plugin adiciona automaticamente a dependência para a sua plataforma de build.
    modules("javafx.controls")
}

dependencies {
    // Dependências de teste (mantidas)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Mockito
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.10.0")

    implementation("megalodonte:megalodonte-base:1.0.0-beta")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.jar {
    archiveBaseName.set("megalodonte-router")

    manifest {
        attributes(
            "Implementation-Title" to "Megalodonte Router",
            "Implementation-Version" to project.version
        )
    }
}

// Configuração de Publicação (mantida)
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "megalodonte-router"
        }
    }
}