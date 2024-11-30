package org.latinpray.io

import okio.source
import org.latinpray.AndroidInjector

// For android, we have to access it the usual way assets files are accessed.
// Okio provides a simple extension to convert an InputStream to a Source.
actual val defaultAssetFileProvider: AssetFileProvider = AssetFileProvider { path ->
    AndroidInjector.application.assets.open(
        path.removePrefix("assets/")
    ).source()
}

actual fun listAssetsInDirectory(directory: String): List<String> {
    return AndroidInjector.application.assets.list(directory.removePrefix("assets/"))?.toList() ?: emptyList()
}
