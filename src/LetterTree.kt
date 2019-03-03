import java.io.*
import java.util.*
import kotlin.collections.HashMap

fun main() {
    val root = readDictionaryAsBinary()
//        createLetterDictionary()
//    writeDictionaryAsBinary(root)

    var cont = true
    var query: String?
    while (cont) {
        println("Enter a word to look up:")
        query = readLine()
        println(root.search(query!!.toLowerCase()) ?: "Word does not exist.")
        println("Would you like to look up another word?")
        cont = readLine()!!.toLowerCase().contains("y")
    }
}

fun createLetterDictionary(): Letter<String> {
    val file = File("dictionary_compact.json")
    val scanner = Scanner(file)
    var s = scanner.nextLine()
    scanner.close()

    s = s.substring(1, s.length - 1)

    val r = s.split("\",\"")

    val root = Letter<String>()
    r.forEach {
        val t = it.split("\":\"")
        var prev = root
        t[0].forEachIndexed { index, c ->
            val nextLetter = Letter<String>(c)
            if (prev.nextLetters.contains(c)) {
                prev = prev.nextLetters[c]!!

            } else {
                prev.nextLetters[c] = nextLetter
                prev = nextLetter
            }
            if (index == t[0].lastIndex) {
                prev.isWordEnding = true
                prev.definition = t[1]
            }
        }
    }

    return root
}

fun writeDictionaryAsBinary(root: Letter<String>) {
    val fileOutputStream = FileOutputStream("dictionary_compact.dat")
    val objectOutputStream = ObjectOutputStream(fileOutputStream)

    objectOutputStream.writeObject(root)

    objectOutputStream.close()
}

fun readDictionaryAsBinary(): Letter<String> {
    val fileInputStream = FileInputStream("dictionary_compact.dat")
    val objectInputStream = ObjectInputStream(fileInputStream)

    return objectInputStream.readObject() as Letter<String>
}

class Letter <T> (
    val c: Char = '\u0000',
    val nextLetters: HashMap<Char, Letter<T>> = HashMap(),
    var isWordEnding: Boolean = false,
    var definition: T? = null
) : Serializable {


    override fun toString(): String {
        return "$nextLetters"
    }

    fun search(query: String): T? {

        return if (query.isEmpty()) {
            if (isWordEnding) {
                definition!!
            } else {
                null
            }
        } else {
            if (nextLetters.containsKey(query[0])) {
                nextLetters[query[0]]!!.search(query.substring(1))
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