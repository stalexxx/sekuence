# sekuence
Sequence-based random generator for kotlin. Inspired by Clojure _test.check_

Examples


Random of object list
```kotlin
fun languages(): Sequence<String> = of("clojure", "haskell", "erlang", "scala", "python")

```
Combination with probabilities
```kotlin
fun mostlyInts(): Sequence<Any> = frequency(9 to int(), 1 to ret(1))
```

Positive and Even
```kotlin
fun evenAndPositive(): Sequence<Int> = posInt().fmap { it * 2 }
```

Power of two
```kotlin
fun powerOfTwo(): Sequence<Int> = posInt().fmap { Math.pow(2.0, it.toDouble()).toInt() }

```

Sequence of sorted int lists

```kotlin
fun sortedLists(): Sequence<List<Int>> = genList(int()).fmap { it.sorted() }
```

Pair example
```kotlin
fun intAndBoolean(): Sequence<Pair<Int, Boolean>> = genPair(int(), bool())
```
Anything by five

```kotlin
fun anythingButFive(): Sequence<Int> = int().except { it == 5 }
```


Binded
```kotlin
fun vectorAndElem(): Sequence<Pair<List<Int>, Int>> =
    genList(int().except { it == 13 })
        .bind { it to it.randElement() }
```
