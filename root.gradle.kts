plugins {
    alias(libs.plugins.loom) apply false
    alias(libs.plugins.preprocessorRoot)
}

preprocess {
    val fabric12104 = createNode("1.21.4-fabric", 12104, "yarn")
    val fabric12105 = createNode("1.21.5-fabric", 12105, "yarn")

    fabric12105.link(fabric12104)
}