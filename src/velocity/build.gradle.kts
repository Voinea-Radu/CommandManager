@file:Suppress("VulnerableLibrariesLocal")

dependencies {
    // Velocity
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)

    // Project
    api(project(":command-manager-common"))

    // Dependencies
    if (project.properties["com.voinearadu.utils.local"] != null) {
        api(project(project.properties["com.voinearadu.utils.local"] as String))
    } else {
        api("com.voinearadu:utils:1.1.6")
    }

    // Annotations
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)
    testCompileOnly(libs.jetbrains.annotations)
    testAnnotationProcessor(libs.jetbrains.annotations)
}

