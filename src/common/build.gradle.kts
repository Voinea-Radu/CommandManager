dependencies {
    // Dependencies
    if (project.properties["com.voinearadu.utils.local"] != null) {
        api(project(project.properties["com.voinearadu.utils.local"] as String))
    } else {
        api("com.voinearadu:utils:1.1.6")
    }
    api(libs.luckperms)
    api(libs.kyori.minimessage)

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
