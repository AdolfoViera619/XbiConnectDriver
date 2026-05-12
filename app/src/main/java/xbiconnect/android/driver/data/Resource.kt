package xbiconnect.android.driver.data

/**
 * Three-state container for asynchronous data: idle/loading, success with data,
 * or error with a message. Mirrors the pattern used in the agent app so the
 * UI can branch on the state in a single `when` block.
 */
sealed interface Resource<out T> {
    data object Idle : Resource<Nothing>
    data object Loading : Resource<Nothing>
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String, val cause: Throwable? = null) : Resource<Nothing>
}
