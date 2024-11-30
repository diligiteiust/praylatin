
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ComposeUIViewController
import org.latinpray.Main


fun MainViewController() =
    ComposeUIViewController {
        val scope = rememberCoroutineScope()

        Main()
    }
