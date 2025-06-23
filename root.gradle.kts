plugins {
    alias(libs.plugins.loom) apply false
    alias(libs.plugins.preprocessorRoot)
}

preprocess {
    val fabric12104 = createNode("1.21.4-fabric", 12104, "yarn")
}