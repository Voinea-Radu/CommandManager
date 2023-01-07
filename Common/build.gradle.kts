plugins {
    id("java")
}

group = "dev.lightdream"
version = "1.0.0"

repositories {
    maven ("https://repo.spongepowered.org/maven/")
    maven ("https://repo.lightdream.dev/")
    maven ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}


dependencies {
    // LightDream
    implementation("dev.lightdream:logger:+")
    implementation("dev.lightdream:lambda:+")
    implementation("dev.lightdream:message-builder:+")
    implementation("dev.lightdream:reflections:+")

    // Lombok
    compileOnly("org.projectlombok:lombok:${getVersion("lombok")}")
    annotationProcessor("org.projectlombok:lombok:${getVersion("lombok")}")

    // JetBrains
    compileOnly("org.jetbrains:annotations:${getVersion("jetbrains-annotations")}")
    annotationProcessor("org.jetbrains:annotations:${getVersion("jetbrains-annotations")}")
}

fun getVersion(id: String): String {
    return rootProject.extra[id] as String
}

configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(10, "seconds")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Jar> {
    archiveFileName.set("${rootProject.name}.jar")
}