import java.io.*
import java.util.*
import kotlin.collections.HashMap

fun main() {
//    val root = readDictionaryAsBinary<HashMap<String, String>>("dictionary_compact_2.dat")
//        val root = createDictionary()
//    val root = createLetterDictionary()
//    writeDictionaryAsBinary(root, "dictionary_compact_2.dat")

    val root = createLetterDictionary()
    writeDictionaryAsBinary(root, "dictionary_compact_3.dat")


    var cont = true
    var query: String?
    while (cont) {
        println("Enter a word to look up:")
        query = readLine()
        val row = query!!.length
        val letter = query[query.lastIndex]
        val value = valueOf(query.trim())
        println(root[row][letter]?.get(value) ?: "Word does not exist.")
        println("Would you like to look up another word?")
        cont = readLine()!!.toLowerCase().contains("y")
    }
}

fun createDictionary(): HashMap<String, String> {
    val file = File("dictionary_compact.json")
    val scanner = Scanner(file)
    var s = scanner.nextLine()
    scanner.close()

    var savedChars = 0
    var totalChars = 0

    s = s.substring(1, s.length - 1)

    val r = s.split("\",\"")

    val dictionary = HashMap<String, String>()

    val root = Letter<String>()
    r.forEach {
        val t = it.split("\":\"")

        dictionary[t[0]] = t[1]
    }
    return dictionary
}

fun createLetterDictionary(): List<HashMap<Char, HashMap<Int, String>>> {
    val file = File("dictionary_compact.json")
    val scanner = Scanner(file)
    var s = scanner.nextLine()
    scanner.close()

    var savedChars = 0
    var totalChars = 0

    s = s.substring(1, s.length - 1)

    val list = List<HashMap<Char, HashMap<Int, String>>>(45) { HashMap(26) }
    val r = s.split("\",\"")
    r.forEach {
        val t = it.split("\":\"")
        val row = t[0].length
        val letter = t[0][t[0].lastIndex]
        val value = valueOf(t[0])
        if (!list[row].contains(letter)) {
            list[row][letter] = HashMap()
        } else if (list[row][letter]!!.contains(value)) {
            throw IllegalArgumentException("${t[0]}'s definition overlaps with ${list[row][letter]!![value]}: $value")
        }
        totalChars += t[0].length
        savedChars += t[0].length - 1
        list[row][letter]!![value] = t[1]

    }

    println("Congratulations! You saved $savedChars chars worth of data instead of $totalChars!")

    return list
}

val powersOf26 = Array(45) { Math.pow(26.0, it.toDouble()) }

fun valueOf(c: Char, pos: Int): Double = (c - 'a' + 1).toDouble() *
        if (pos < 45) powersOf26[pos] else Math.pow(26.0, pos.toDouble())

fun valueOf(string: String): Int {
    return string.hashCode()
//    var value = 0.0
//    string.forEachIndexed { i, c ->
//        value += valueOf(c, i)
//    }
//    return value
}

fun <T> writeDictionaryAsBinary(dictionary: T, fileName: String) {
    val fileOutputStream = FileOutputStream(fileName)
    val objectOutputStream = ObjectOutputStream(fileOutputStream)

    objectOutputStream.writeObject(dictionary)

    objectOutputStream.close()
}

fun <T> readDictionaryAsBinary(fileName: String): T {
    val fileInputStream = FileInputStream(fileName)
    val objectInputStream = ObjectInputStream(fileInputStream)
    return objectInputStream.readObject() as T
}

class Letter<T>(
    val c: Char = '\u0000',
    val nextLetters: HashMap<Char, Letter<T>> = HashMap(),
    var isWordEnding: Boolean = false,
    var definition: T? = null
) : Serializable {


    override fun toString(): String {
        return "$nextLetters"
    }

    fun get(query: String): T? {

        return if (query.isEmpty()) {
            if (isWordEnding) {
                definition!!
            } else {
                null
            }
        } else {
            if (nextLetters.containsKey(query[0])) {
                nextLetters[query[0]]!!.get(query.substring(1))
            } else {
                null
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Letter<*>) return false

        if (c != other.c) return false

        return true
    }

    override fun hashCode(): Int {
        return c.hashCode()
    }


}