import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "com.github.msfukui"
version = "0.1.0"

repositories {
    mavenCentral()
}

// IntelliJ Platform Plugin設定
intellij {
    version.set("2023.1.5")
    type.set("IC") // IntelliJ IDEA Community Edition
    
    plugins.set(listOf(/* Plugin dependencies */))
}

tasks {
    // Kotlin設定
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    
    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("241.*")
    }
    
    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }
    
    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
    
    runIde {
        // デバッグ用の設定
        jvmArgs = listOf("-Xmx2048m", "-XX:+UnlockDiagnosticVMOptions")
    }
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}