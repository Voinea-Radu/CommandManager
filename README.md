# Logger

## How to add to your project

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