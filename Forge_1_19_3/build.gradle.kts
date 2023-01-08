plugins {
    id("java")
    id("maven-publish")
    id("idea")
    id("net.minecraftforge.gradle") version ("5.1.+")
    id("org.parchmentmc.librarian.forgegradle") version ("1.+")
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

group = "dev.lightdream"
version = getVersion("Forge_1_19_3")

minecraft {
    mappings("parchment", getVersion("parchment"))
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
}

repositories {
    mavenCentral()
    maven("https://repo.lightdream.dev/")
    maven("https://cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }
    maven("https://maven.parchmentmc.org")
}


dependencies {
    minecraft(
        group = "net.minecraftforge",
        name = "forge",
        version = "${getVersion("minecraft")}-${getVersion("forge")}",
    )

    // Project
    implementation(project(":Common"))

    // LightDream
    implementation("dev.lightdream:logger:+")
    implementation("dev.lightdream:lambda:+")
    implementation("dev.lightdream:message-builder:+")

    // LuckPerms
    compileOnly("net.luckperms:api:${getVersion("luckperms")}")

    // JetBrains
    compileOnly("org.jetbrains:annotations:${getVersion("jetbrains-annotations")}")
    annotationProcessor("org.jetbrains:annotations:${getVersion("jetbrains-annotations")}")
}

tasks {
    shadowJar {
        isZip64 = true
        archiveFileName.set("Forge-1.19.3.jar")
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
