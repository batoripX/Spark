import java.net.URL
import java.io.File
import java.util.zip.ZipInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.ZipEntry
import java.nio.file.Files

object Setup {
    fun download() {
        val sdkDir = File("sdk")
        if (!sdkDir.exists()) sdkDir.mkdirs()

        val kotlincZip = File(sdkDir, "kotlinc.zip")
        val kotlincDir = File(sdkDir, "kotlinc")
        if (!kotlincDir.exists()) {
            println("\u001B[33m[SPARK]\u001B[0m Downloading Kotlin compiler...")
            URL("https://github.com/JetBrains/kotlin/releases/download/v1.9.0/kotlin-compiler-1.9.0.zip")
                .openStream().use { kotlincZip.outputStream().use { it.write(it.readBytes()) } }
            unzip(kotlincZip, kotlincDir)
            println("\u001B[32m[SPARK]\u001B[0m Kotlin compiler ready")
        }

        val jdkTar = File(sdkDir, "openjdk.tar.gz")
        val jdkDir = File(sdkDir, "openjdk")
        if (!jdkDir.exists()) {
            println("\u001B[33m[SPARK]\u001B[0m Downloading OpenJDK...")
            URL("https://download.java.net/openjdk/jdk21/ri/openjdk-21+35_linux-x64_bin.tar.gz")
                .openStream().use { jdkTar.outputStream().use { it.write(it.readBytes()) } }
            untarGz(jdkTar, jdkDir)
            println("\u001B[32m[SPARK]\u001B[0m OpenJDK ready")
        }

        val ndkZip = File(sdkDir, "ndk.zip")
        val ndkDir = File(sdkDir, "android-ndk")
        if (!ndkDir.exists()) {
            println("\u001B[33m[SPARK]\u001B[0m Downloading Android NDK...")
            URL("https://dl.google.com/android/repository/android-ndk-r25b-linux.zip")
                .openStream().use { ndkZip.outputStream().use { it.write(it.readBytes()) } }
            unzip(ndkZip, ndkDir)
            println("\u001B[32m[SPARK]\u001B[0m Android NDK ready")
        }

        println("\u001B[32m[SPARK]\u001B[0m All SDK/NDK/Compilers are ready in sdk/")
    }

    private fun unzip(zipFile: File, targetDir: File) {
        ZipInputStream(zipFile.inputStream()).use { zis ->
            var entry: ZipEntry? = zis.nextEntry
            while (entry != null) {
                val file = File(targetDir, entry.name)
                if (entry.isDirectory) file.mkdirs() else {
                    file.parentFile.mkdirs()
                    FileOutputStream(file).use { fos -> zis.copyTo(fos) }
                }
                entry = zis.nextEntry
            }
        }
    }

    private fun untarGz(tarGzFile: File, targetDir: File) {
        targetDir.mkdirs()
        val process = ProcessBuilder("tar", "-xzf", tarGzFile.absolutePath, "-C", targetDir.absolutePath).inheritIO().start()
        process.waitFor()
    }
}