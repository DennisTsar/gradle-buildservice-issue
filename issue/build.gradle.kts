import org.gradle.api.internal.provider.DefaultProvider
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener

val readFileFun = DefaultProvider {
    val testFile = layout.projectDirectory.file("test.txt").asFile
    { testFile.exists() }
}

abstract class MyListener : BuildService<MyListener.Params>, OperationCompletionListener {
    interface Params : BuildServiceParameters {
        val readFileFun: Property<() -> Boolean>
    }

    override fun onFinish(event: FinishEvent) {
        println("file exists: ${parameters.readFileFun.get().invoke()}")
    }
}

val myListener = project.gradle.sharedServices.registerIfAbsent("myListener", MyListener::class.java) {
    parameters.readFileFun.set(readFileFun)
}

interface BuildEventsHolder {
    @get:Inject
    val buildEventsListenerRegistry: BuildEventsListenerRegistry
}
objects.newInstance(BuildEventsHolder::class).buildEventsListenerRegistry.onTaskCompletion(myListener)
