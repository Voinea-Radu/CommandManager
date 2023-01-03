plugins {
    id("java")
    id("idea")
    id("net.minecraftforge.gradle") version ("5.1.+")
    id("org.parchmentmc.librarian.forgegradle") version ("1.+")
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

group = "dev.lightdream"
version = "1.1.2"

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

    compileOnly("net.luckperms:api:${getVersion("luckperms")}")
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