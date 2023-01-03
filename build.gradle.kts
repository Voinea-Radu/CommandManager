plugins {
    java
}

// Main libs versions
extra["forge"] = "44.0.41"
extra["minecraft"] = "1.19.3"
extra["parchment"] = "2022.12.18-1.19.3"
extra["velocity"] = "3.1.1"
extra["spigot-api"] = "1.8-R0.1-SNAPSHOT"
extra["spongeapi"] = "7.4.0-SNAPSHOT"

// LightDream
extra["Logger"] = "3.0.0"
extra["Lambda"] = "3.7.0"
extra["MessageBuilder"] = "3.0.6"
extra["RedisManager"] = "1.10.2"
extra["FileManager"] = "2.3.0"

// Other
extra["reflections"] = "0.10.2"
extra["lombok"] = "1.18.24"
extra["luckperms"] = "5.4"

group = "com.pokeninjas"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}

fun getVersion(id: String): String {
    return rootProject.extra[id] as String
}