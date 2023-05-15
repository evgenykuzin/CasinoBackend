import org.springframework.http.ResponseEntity
import ru.jekajops.casino.Response
import java.util.function.Consumer
import java.util.function.Supplier

fun <T> T.print(appendable: String = ""): T {
    println(this.toString().plus(appendable))
    return this
}