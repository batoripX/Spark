package com.batorip.spark

import java.io.File

object CppCompiler {
    fun compile(src:String,out:String){
        File(out).mkdirs()
        println("\u001B[34m[SPARK]\u001B[0m Compiling C++: $src → $out")
        Runtime.getRuntime().exec("sdk/android-ndk/toolchains/llvm/prebuilt/linux-x86_64/bin/clang++ -o $out $src/*.cpp").waitFor()
        println("\u001B[32m[SPARK]\u001B[0m C++ compiled")
    }
}