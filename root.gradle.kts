plugins {
    alias(libs.plugins.loom) apply false
    alias(libs.plugins.preprocessorRoot)
}

preprocess {
    val fabric12104 = createNode("1.21.4-fabric", 12104, "yarn")
    val fabric12105 = createNode("1.21.5-fabric", 12105, "yarn")
    val fabric12106 = createNode("1.21.6-fabric", 12106, "yarn")

    fabric12106.link(fabric12105)
    fabric12105.link(fabric12104)
}