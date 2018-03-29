# sekuence
Sequence-based random generator for kotlin. Inspired by Clojure _test.check_

Examples


Random of object list
```kotlin
fun languages(): Sequence<String> = of("clojure", "haskell", "erlang", "scala", "python")

//[ scala kotlin python clojure erlang ]
```


Combination with probabilities
```kotlin
fun mostlyInts(): Sequence<Any> = frequency(9 to ints(), 1 to ret(1))
```

Positive and Even
```kotlin
fun evenAndPositive(): Sequence<Int> = posInt().fmap { it * 2 }

//
```

Power of two
```kotlin
fun powerOfTwo(): Sequence<Int> = posInt().fmap { Math.pow(2.0, it.toDouble()).toInt() }

```

Sequence of sorted ints lists

```kotlin
fun sortedLists(): Sequence<List<Int>> = genList(ints()).fmap { it.sorted() }
```

Pair example
```kotlin
fun intAndBoolean(): Sequence<Pair<Int, Boolean>> = genPair(ints(), bool())
```
Anything by five

```kotlin
fun anythingButFive(): Sequence<Int> = ints().except { it == 5 }
```


Binding
```kotlin
fun vectorAndElem(): Sequence<Pair<List<Int>, Int>> =
    genList(ints().except { it == 13 })
        .bind { it to it.randElement() }
        
```

Binding over type
```kotlin
data class User(var id: Int, var login: String, var email: String)
fun genUsers(): Sequence<User> = bind(ints(1..100), strings(), emails(), ::User)

```

Another way
```kotlin
data class User(var id: Int, var login: String, var email: String)
fun genUsers() = bind<User>(ints(1..100), names().fmap(String::toLowerCase), emails(strings())
```
returns

[ User(id=100, login=john, email=test@yahoo.com)
    User(id=60, login=hual, email=test@ya.ru)
    User(id=56, login=john, email=test@ya.ru)
     User(id=69, login=john, email=test@mail.ru)
     User(id=45, login=john, email=test@mail.ru) ]
