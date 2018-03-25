
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.Random

object Rand {

    val random = Random()

    val nextDouble: Double
        get() = Random().nextDouble()

    val nextBool: Boolean
        get() = Random().nextBoolean()

    fun nextString(min: Int = 0, max: Int) = IntRange(0, int(min, max)).map { 'a' + randInt(max = 26) }.joinToString(separator = "")
    val nextInt
        get() = random.nextInt()

    fun randName(i: Int): String =
            IntRange(0, i - 1).joinToString(" ") { nextString(5, 10) }

    fun randInt(min: Int = 0, max: Int): Int = Math.round(max * nextDouble).toInt()
    fun randLong(min: Long, max: Long): Long = Math.round(max * nextDouble)

    fun int(min: Int = 0, max: Int): Int = min + Random().nextInt((max - min) + 1)

    val nInstant: Instant
        get() {
            return Instant.now() - datetimeDuration()
        }

    fun localDate(max: Int) = (Instant.now() - dateDuration(max = max)).atZone(ZoneId.systemDefault()).toLocalDate()

    private fun timeDuration(): Duration = Duration.ofHours(int(1, 23).toLong()) +
            Duration.ofMinutes(int(1, 59).toLong()) +
            Duration.ofSeconds(int(1, 59).toLong())

    private fun dateDuration(min: Int = 1, max: Int = 7): Duration = Duration.ofDays(int(min, max).toLong()) + timeDuration()

    private fun datetimeDuration(): Duration = dateDuration() + timeDuration()

    fun <T : Any> randomSublist(racers: List<T>): List<T> = racers.map { if (nextBool) it else null }. filter { it != null }.map { it!! }.toList()
    fun <T> randElement(elts: Array<out T>): T? = elts[random.nextInt(elts.size)]
//    fun enum(values: Array<FinalResult>) = values[randInt(values.size - 1)]


}

fun <T> List<T>.randElement() = get(Rand.int(max = size))