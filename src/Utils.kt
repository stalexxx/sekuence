import kotlin.reflect.KClass

fun <T : Any> KClass<T>.createInstance(count: Int, vararg args: Any?): T {
    // TODO: throw a meaningful exception
    val noArgsConstructor = constructors.singleOrNull { it.parameters.count() == count }
        ?: throw IllegalArgumentException("Class should have a single no-arg constructor: $this")

    return noArgsConstructor.call(*args)
}