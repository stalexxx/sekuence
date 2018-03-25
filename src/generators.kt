fun int(): Sequence<Int> = generateSequence { Rand.nextInt }
fun bool(): Sequence<Boolean> = generateSequence { Rand.nextBool }
fun posInt(): Sequence<Int> = int().fmap(Math::abs)

fun oneOfAny(vararg gens: Sequence<Any>): Sequence<Any> = OneOfSequenceThread(gens)
fun <T : Any> oneOf(vararg gens: Sequence<T>): Sequence<T> = OneOfSequenceThread(gens)

fun choose(from: Long = -100, to: Long = 100): Sequence<Long> = generateSequence { Rand.randLong(from, to) }
fun choose(from: Int = -10, to: Int = 10): Sequence<Int> = generateSequence { Rand.randInt(from, to) }

inline fun <reified T : Any> of(vararg elts: T): Sequence<T> = generateSequence {
    Rand.randElement(elts)
}

fun <T : Any> ret(value: T): Sequence<T> = generateSequence { value }
fun <T : Any> ret(g: () -> T): Sequence<T> = generateSequence(g)

fun <T> genList(from: Sequence<T>, min: Int = 0, max: Int = 10): Sequence<List<T>> = generateSequence {
    from.take(Rand.int(min, max)).toList()
}

fun <T : Any> Sequence<T>.fmap(mapper: (T) -> T): Sequence<T> = MappingSequenceThread(this, mapper = mapper)
fun <T> Sequence<T>.except(predicate: (T) -> Boolean): Sequence<T> = FilteringSequenceThread(this, predicate = predicate)

fun frequency(vararg freqs: Pair<Number, Sequence<Any>>): Sequence<Any> = generateSequence {
    //TODO()
    freqs.first().second.iterator().next()
}

fun <A : Any, B : Any> genPair(gen1: Sequence<A>, gen2: Sequence<B>): Sequence<Pair<A, B>> = generateSequence {
    gen1.iterator().next() to gen2.iterator().next()
}

fun <T : Any, R : Any> Sequence<T>.bind(binder: (T) -> R): Sequence<R> = generateSequence {
    binder(iterator().next())
}

fun <A : Any, B : Any, R : Any> bind(g1: Sequence<A>, g2: Sequence<B>, binder: (A, B) -> R): Sequence<R>  = generateSequence {
    binder(g1.iterator().next(), g2.iterator().next())
}

fun <A : Any, B : Any, C : Any, R : Any> bind(g1: Sequence<A>, g2: Sequence<B>, g3: Sequence<C>, binder: (A, B, C) -> R): Sequence<R> = generateSequence {
    binder(g1.iterator().next(), g2.iterator().next(), g3.iterator().next())
}

//implementation

class MappingSequenceThread<T>(
    val sequence: Sequence<T>,
    val mapper: (T) -> T

) : Sequence<T> {
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        val iterator: Iterator<T> = sequence.iterator()

        override fun next(): T = mapper(iterator.next())

        override fun hasNext(): Boolean = iterator.hasNext()
    }
}

class FilteringSequenceThread<T>(
    val sequence: Sequence<T>,
    val predicate: (T) -> Boolean

) : Sequence<T> {
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        val iterator: Iterator<T> = sequence.iterator()

        override fun next(): T =
            iterator.next().let {
                if (predicate(it)) it else next()
            }

        override fun hasNext(): Boolean = iterator.hasNext()
    }
}

@Suppress("UNCHECKED_CAST")
class OneOfSequenceThread<out T : Any>(val gens: Array<out Sequence<*>>) : Sequence<T> {
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        val iters = gens.map { it.iterator() }

        override fun next() =
            iters
                .filter { it.hasNext() }
                .randElement()
                .next() as T

        override fun hasNext(): Boolean = iters.any { it.hasNext() }
    }
}