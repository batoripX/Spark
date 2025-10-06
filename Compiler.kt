package com.batorip.spark

import java.io.File

object Compiler {
    fun compileKotlin(src:String,out:String){
        File(out).mkdirs()
        println("\u001B[34m[SPARK]\u001B[0m Compiling Kotlin: $src → $out")
        Runtime.getRuntime().exec("sdk/kotlinc/bin/kotlinc $src/*.kt -d $out").waitFor()
        println("\u001B[32m[SPARK]\u001B[0m Kotlin compiled")
    }
    fun compileJava(src:String,out:String){
        File(out).mkdirs()
        println("\u001B[34m[SPARK]\u001B[0m Compiling Java: $src → $out")
        Runtime.getRuntime().exec("sdk/openjdk/bin/javac -d $out $src/*.java").waitFor()
        println("\u001B[32m[SPARK]\u001B[0m Java compiled")
    }
}