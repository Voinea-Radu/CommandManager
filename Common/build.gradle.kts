plugins {
    id("java")
    id("maven-publish")
}

group = "dev.lightdream"
version = getVersion("Common")

repositories {
    maven ("https://repo.spongepowered.org/maven/")
    maven ("https://repo.lightdream.dev/")
    maven ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}


dependencies {
    // LightDream
    implementation("dev.lightdream:logger:${getVersion("logger")}")
    implementation("dev.lightdream:lambda:${getVersion("lambda")}")
    implementation("dev.lightdream:message-builder:${getVersion("message-builder")}")
    implementation("dev.lightdream:reflections:${getVersion("reflections")}")

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

        val selfURL = project.findProperty("self.url") ?: ""
        val selfUsername = project.findProperty("self.auth.username") ?: ""
        val selfPassword = project.findProperty("self.auth.password") ?: ""

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

        maven(url = selfURL as String) {
            name = "self"
            credentials(PasswordCredentials::class) {
                username = selfUsername as String
                password = selfPassword as String
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

tasks.register("publishSelf") {
    dependsOn("publishMavenPublicationToSelfRepository")
    description = "Publishes to Self hosted repository"
}
