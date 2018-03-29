fun <T : Any, R : Any> Sequence<T>.bind(binder: (T) -> R): Sequence<R> =
    letSeq(object {
        val it1 = iterator()
    }) {
        binder(it1.next())
    }

fun <A : Any, B : Any, R : Any> bind(g1: Sequence<A>, g2: Sequence<B>, binder: (A, B) -> R): Sequence<R> =
    letSeq(object {
        val it1 = g1.iterator()
        val it2 = g2.iterator()
    }) {
        binder(it1.next(), it2.next())
    }

fun <A : Any, B : Any, C : Any, R : Any> bind(g1: Sequence<A>, g2: Sequence<B>, g3: Sequence<C>, binder: (A, B, C) -> R): Sequence<R> =
    letSeq(object {
        val it1 = g1.iterator()
        val it2 = g2.iterator()
        val it3 = g3.iterator()
    }) {
        binder(it1.next(), it2.next(), it3.next())
    }

inline fun <reified R : Any> bind(g1: Sequence<Any>, g2: Sequence<Any>, g3: Sequence<Any>): Sequence<R> =
    letSeq(object {
        val i1 = g1.iterator()
        val i2 = g2.iterator()
        val i3 = g3.iterator()
    }) {
        R::class.createInstance(3,
            i1.next(),
            i2.next(),
            i3.next())
    }
