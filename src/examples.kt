import kotlin.reflect.full.createInstance

//(def five-through-nine (ret/choose 5 9))
//  (ret/sample five-through-nine)
// ;; => (6 5 9 5 7 7 6 9 7 9)
fun fiveThroughtNine(): Sequence<Int> = choose(5, 9)

//A random element from a vector
//(def languages (ret/of ["clojure" "haskell" "erlang" "scala" "python"]))
//(ret/sample languages)
//;; => ("clojure" "scala" "clojure" "haskell" "clojure" "erlang" "erlang"
//;; =>  "erlang" "haskell" "python")

fun languages(): Sequence<String> = of("kotlin", "clojure", "haskell", "erlang", "scala", "python")

//An integer or nil
//
//(def ints-or-nil (ret/one-of [ret/ints (ret/return nil)]))
//(ret/sample ints-or-nil)
//;; => (nil 0 -2 nil nil 3 nil nil 4 2)
fun intOrNil(): Sequence<Int> = oneOf(ints(), ret(1))
//fun someOrNil(): Sequence<Int> = Gen.oneOfSame(Gen.ints(), ret(1))//todo

//(def mostly-ints (ret/frequency [[9 ret/ints] [1 (ret/return nil)]]))
//(ret/sample mostly-ints)
//;; => (0 -1 nil 0 -2 0 6 -6 8 7)
fun mostlyInts(): Sequence<Any> = frequency(9 to ints(), 1 to ret(1))

//(def even-and-positive (ret/fmap #(* 2 %) ret/pos-ints))
//(ret/sample even-and-positive 20)
//;; => (0 0 2 0 8 6 4 12 4 18 10 0 8 2 16 16 6 4 10 4)
fun evenAndPositive(): Sequence<Int> = posInt().fmap { it * 2 }

//;; generate exponents with ret/s-pos-ints (strictly positive integers),
//;; and then apply the lambda to them
//(def powers-of-two (ret/fmap #(ints (Math/pow 2 %)) ret/s-pos-ints))
//(ret/sample powers-of-two)
//;; => (2 2 8 16 16 64 16 2 4 4)
fun powerOfTwo(): Sequence<Int> = posInt().fmap { Math.pow(2.0, it.toDouble()).toInt() }

//Sorted seq of integers
//
//;; apply the sort function to each generated vector
//(def sorted-vec (ret/fmap sort (ret/vector ret/ints)))
//(ret/sample sorted-vec)
//;; => (() (-1) (-2 -2) (-1 2 3) (-1 2 4) (-3 2 3 3 4) (1)
//;; => (-4 0 1 3 4 6) (-5 -4 -1 0 2 8) (1))
fun sortedLists(): Sequence<List<Int>> = genList(ints()).fmap { it.sorted() }

//An integer and a boolean
//
//(def ints-and-boolean (ret/tuple ret/ints ret/boolean))
//(ret/sample ints-and-boolean)
//;; => ([0 false] [0 true] [0 true] [3 true] [-3 false]
//;; =>  [0 true] [4 true] [0 true] [-2 true] [-9 false])
fun intAndBoolean(): Sequence<Pair<Int, Boolean>> = genPair(ints(), bools())

//Any number but 5
//
//(def anything-but-five (ret/such-that #(not= % 5) ret/ints))
//(ret/sample anything-but-five)
//;; => (0 0 -2 1 -3 1 -4 7 -1 6)
fun anythingButFive(): Sequence<Int> = ints().except { it == 5 }

//A vector and a random element from it
//
//(def vector-and-elem (ret/bind (ret/not-empty (ret/vector ret/ints))
//#(ret/tuple (ret/return %) (ret/of %))))
//(ret/sample vector-and-elem)
//;; =>([[-1] -1]
//;; => [[0] 0]
//;; => [[-1 -1] -1]
//;; => [[2 0 -2] 2]
//;; => [[0 1 1] 0]
//;; => [[-2 -3 -1 1] -1]
//;; => [[-1 2 -5] -5]
//;; => [[5 -7 -3 7] 5]
//;; => [[-1 2 2] 2]
//;; => [[-8 7 -3 -2 -6] -3])
fun vectorAndElem(): Sequence<Pair<List<Int>, Int>> =
    genList(ints().except { it == 13 })
        .bind { it to it.randElement() }

data class User(var id: Int, var login: String, var email: String)


fun genUsers() = bind<User>(
    ints(1..100),
    names().fmap(String::toLowerCase),
    concat(strings(), ret("@"), names()))


inline fun <reified T : Any> genObj(crossinline function: T.() -> Unit): Sequence<T> = generateSequence<T> {
    T::class.createInstance().apply(function)
}

fun main(args: Array<String>) {
    genUsers().sample(5).print()
    fiveThroughtNine().sample(5).print()
    languages().sample(5).print()
    evenAndPositive().sample(10).print()
    anythingButFive().sample(10).print()
}

fun <T : Any> Sequence<T>.sample(count: Int = 10): Sequence<T> {
    return this.take(count)
}

private fun <T : Any> Sequence<T>.print() {
    toList().joinToString(prefix = "[ ", postfix = " ]\n", separator = " ").also { print(it) }
}
