plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

group = "dev.lightdream"
version = getVersion("Sponge")


repositories {
    maven ("https://repo.spongepowered.org/maven/")
    maven ("https://repo.lightdream.dev/")
}

dependencies {
    // Sponge
    implementation("org.spongepowered:spongeapi:${getVersion("spongeapi")}")

    // Project
    implementation(project(":Common"))

    // LightDream
    implementation("dev.lightdream:logger:+")
    implementation("dev.lightdream:lambda:+")
    implementation("dev.lightdream:message-builder:+")

    // Lombok
    compileOnly("org.projectlombok:lombok:${getVersion("lombok")}")
    annotationProcessor("org.projectlombok:lombok:${getVersion("lombok")}")

    // JetBrains
    compileOnly("org.jetbrains:annotations:${getVersion("jetbrains-annotations")}")
    annotationProcessor("org.jetbrains:annotations:${getVersion("jetbrains-annotations")}")
}

tasks {
    shadowJar {
        isZip64 = true
        archiveFileName.set("Sponge.jar")
        dependencies {
            include(project(":Common"))
        }
    }
}

tasks.getByName("jar").finalizedBy("shadowJar")

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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        val gitlabURL = project.findProperty("gitlab.url") ?: ""
        val gitlabHeaderName = project.findProperty("gitlab.auth.header.name") ?: ""
        val gitlabHeaderValue = project.findProperty("gitlab.auth.header.value") ?: ""

        val githubURL = project.findProperty("github.url") ?: ""
        val githubUsername = project.findProperty("github.auth.username") ?: ""
        val githubPassword = project.findProperty("github.auth.password") ?: ""

        maven(url = gitlabURL as String) {
            name = "gitlab"
            credentials(HttpHeaderCredentials::class) {
                name = gitlabHeaderName as String
                value = gitlabHeaderValue as String
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }

        maven(url = githubURL as String) {
            name = "github"
            credentials(PasswordCredentials::class) {
                username = githubUsername as String
                password = githubPassword as String
            }
        }
    }
}

tasks.register("publishGitLab") {
    dependsOn("publishMavenPublicationToGitlabRepository")
    description = "Publishes to GitLab"
}

tasks.register("publishGitHub") {
    dependsOn("publishMavenPublicationToGithubRepository")
    description = "Publishes to GitHub"
}

tasks.getByName("jar").finalizedBy("shadowJar")
