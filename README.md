# Logger

## How to add to your project

## Fabric versioning

| Minecraft version | Fabric version | command-manager-fabric vebric |
|-------------------|----------------|-------------------------------|
| 1.20.1            | 0.15.10        | [1.0.0 - 1.0.6]               |
| 1.21.1            | 0.16.9         | [1.1.0 - ]                    |

```kotlin
repositories {
    maven("https://repository.voinearadu.com/repository/maven-releases/")
    maven("https://repo.voinearadu.com/") // The short version of the above (might be slower on high latency connections)
}

dependencies {
    implementation("com.voinearadu:command-manager-common:VERSION")
    implementation("com.voinearadu:command-manager-velocity:VERSION")
    implementation("com.voinearadu:command-manager-fabric-1-19:VERSION")
}
```