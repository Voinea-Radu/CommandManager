buildscript {
    dependencies {
        classpath("org.hibernate.build.gradle:gradle-maven-publish-auth:2.0.1")
    }
    repositories {
        maven("https://repository.jboss.org/nexus/content/groups/public/")
        mavenLocal()
        mavenCentral()
    }
}


plugins {
    id("java")
    id("maven-publish")
}

group = "dev.lightdream"
version = "1.4.0"


repositories {
    maven ("https://repo.spongepowered.org/maven/")
    maven ("https://repo.lightdream.dev/")
}

dependencies {
    // Sponge
    implementation("org.spongepowered:spongeapi:${getVersion("spongeapi")}")

    // Project
    implementation(project(":Common")){
        exclude("org.projectlombok")
    }

    // LightDream
    implementation("dev.lightdream:Logger:${getVersion("Logger")}"){
        exclude("org.projectlombok")
    }
    implementation("dev.lightdream:Lambda:${getVersion("Lambda")}"){
        exclude("org.projectlombok")
    }
    implementation("dev.lightdream:MessageBuilder:${getVersion("MessageBuilder")}"){
        exclude("org.projectlombok")
    }

    // Lombok
    compileOnly("org.projectlombok:lombok:${getVersion("lombok")}")
    annotationProcessor("org.projectlombok:lombok:${getVersion("lombok")}")

    // Utils
    implementation("org.reflections:reflections:${getVersion("reflections")}")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven ("https://repo.lightdream.dev/")
    }
}

fun getVersion(id: String): String {
    return rootProject.extra[id] as String
}
