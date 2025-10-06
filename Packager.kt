package com.batorip.spark

import java.io.File

object Packager {
    fun jar(input:String,out:String){
        File(out).parentFile.mkdirs()
        println("\u001B[34m[SPARK]\u001B[0m Packaging JAR: $input → $out")
        Runtime.getRuntime().exec("sdk/openjdk/bin/jar cf $out -C $input .").waitFor()
        println("\u001B[32m[SPARK]\u001B[0m JAR packaged")
    }
}