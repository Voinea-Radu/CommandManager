@file:Suppress("VulnerableLibrariesLocal")

dependencies {
    // Velocity
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)

    // Project
    api(project(":command-manager-common"))
}

