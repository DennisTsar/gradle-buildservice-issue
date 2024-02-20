import org.gradle.api.internal.provider.DefaultProvider

val readFileFun = DefaultProvider {
    val testFile = layout.projectDirectory.file("test.txt").asFile
    { testFile.exists() }
}

abstract class MyTask : DefaultTask() {
    @get:Input
    abstract val readFileFunProp: Property<() -> Boolean>

    @TaskAction
    fun run() {
        println("file exists: ${readFileFunProp.get().invoke()}")
    }
}

tasks.register<MyTask>("myTask") {
    readFileFunProp.set(readFileFun)
}
