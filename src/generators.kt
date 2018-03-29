//core gens
fun ints(min: Int = 0, max: Int = 10): Sequence<Int> = generateSequence { Rand.int(min, max) }

fun ints(range: IntRange) = ints(range.start, range.endInclusive)

fun longs(min: Long = 0, max: Long = 10): Sequence<Long> = generateSequence { Rand.long(min, max) }
fun longs(range: IntRange) = ints(range.start, range.endInclusive)

fun strings() = of("test")

fun bools(): Sequence<Boolean> = generateSequence { Rand.nextBool }
fun posInt(max: Int = 10): Sequence<Int> = ints(0, max)

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

fun <T : Any> Sequence<T>.fmap(mapper: (T) -> T): Sequence<T> = wrap(mapper)

fun <T> Sequence<T>.except(predicate: (T) -> Boolean): Sequence<T> = FilteringSequenceThread(this, predicate = predicate)

fun frequency(vararg freqs: Pair<Number, Sequence<Any>>): Sequence<Any> = generateSequence {
    freqs.map { }
    freqs.first().second.iterator().next()
}

fun <A : Any, B : Any> genPair(gen1: Sequence<A>, gen2: Sequence<B>): Sequence<Pair<A, B>> = generateSequence {
    gen1.iterator().next() to gen2.iterator().next()
}

fun <T : Any, R> let(obj: T, init: T.() -> R): R = obj.init()
fun <T : Any, R : Any> letSeq(obj: T, init: T.() -> R): Sequence<R> = generateSequence { obj.init() }

fun <T : Any> Sequence<T>.wrap(next: (T) -> T): Sequence<T> = WrappingSeq(this, next)

//implementation

class WrappingSeq<T>(val sequence: Sequence<T>, val next: (T) -> T) : Sequence<T> {
    override fun iterator(): Iterator<T> = let(object {
        val it = sequence.iterator()
    }) {
        object : Iterator<T> {
            override fun next(): T = next(it.next())
            override fun hasNext(): Boolean = it.hasNext()
        }
    }
}

class MappingSequenceThread<T>(
    val sequence: Sequence<T>,
    val mapper: (T) -> T

) : Sequence<T> {
    override fun iterator(): Iterator<T> = let(object {
        val it = sequence.iterator()
    }) {
        object : Iterator<T> {
            override fun next(): T = mapper(it.next())
            override fun hasNext(): Boolean = it.hasNext()
        }
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
                if (!predicate(it)) it else next()
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

//combined
fun emails(
    nameGen: Sequence<String> = strings(),
    domainGen: Sequence<String> = of("ya.ru", "gmail.com", "yahoo.com", "mail.ru")
): Sequence<String> = concat(nameGen, ret("@"), domainGen)

fun concat(vararg gens: Sequence<String>) = letSeq(
    object {
        val itrs = gens.map { it.iterator() }
    }) {
        itrs.map { it.next() }.joinToString(separator = "")
    }

fun names() = of("John", "Bill", "Hual")