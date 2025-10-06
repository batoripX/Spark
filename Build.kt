package com.batorip.spark

import java.io.File
import com.fasterxml.jackson.module.kotlin.*
import com.fasterxml.jackson.databind.ObjectMapper
import java.security.MessageDigest
import java.nio.file.*

object Build {
    private val mapper = ObjectMapper().registerKotlinModule()
    private val hashFile = File(".sparkhash")
    private val executed = mutableSetOf<String>()

    fun run(){
        println("\u001B[33m[SPARK]\u001B[0m Starting build...")
        runProject(File("spark.yaml"))
        File("projects").walk().filter{it.isFile && it.name=="spark.yaml"}.forEach{runProject(it)}
        println("\u001B[33m[SPARK]\u001B[0m Build complete")
    }

    private fun runProject(file:File){
        val tasks = mapper.readValue(file, Array<Task>::class.java).toList()
        tasks.forEach{runTask(it.name,tasks)}
    }

    fun runTask(name:String,tasks:List<Task>){
        if(executed.contains(name)) return
        val task = tasks.find{it.name==name} ?: return
        task.dependsOn?.forEach{runTask(it,tasks)}
        val srcFiles = when(task.type){
            "kotlin" -> File(task.src!!).walk().filter{it.isFile && it.extension=="kt"}.toList()
            "java" -> File(task.src!!).walk().filter{it.isFile && it.extension=="java"}.toList()
            "cpp" -> File(task.src!!).walk().filter{it.isFile && it.extension=="cpp"}.toList()
            else -> emptyList()
        }
        val hash = srcFiles.map{it.readBytes().md5()}.joinToString("")
        if(hashFile.exists() && hashFile.readText().contains(hash)) {
            println("\u001B[36m[SPARK]\u001B[0m Skipped: $name")
            executed.add(name)
            return
        }
        when(task.type){
            "kotlin" -> Compiler.compileKotlin(task.src!!,task.out!!)
            "java" -> Compiler.compileJava(task.src!!,task.out!!)
            "cpp" -> CppCompiler.compile(task.src!!,task.out!!)
            "jar" -> Packager.jar(task.input!!,task.out!!)
        }
        hashFile.appendText(hash+"\n")
        println("\u001B[32m[SPARK]\u001B[0m Task completed: $name")
        executed.add(name)
    }

    fun clean(){
        File("build").deleteRecursively()
        if(hashFile.exists()) hashFile.delete()
        executed.clear()
        println("\u001B[31m[SPARK]\u001B[0m Clean complete")
    }

    fun watch(){
        println("\u001B[33m[SPARK]\u001B[0m Watching for changes...")
        val paths = mutableListOf<Path>()
        File("src").walk().filter{it.isDirectory}.forEach{paths.add(it.toPath())}
        val watcher = FileSystems.getDefault().newWatchService()
        paths.forEach{ it.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY) }
        while(true){
            val key = watcher.take()
            key.pollEvents().forEach{
                println("\u001B[33m[SPARK]\u001B[0m Change detected, rebuilding...")
                run()
            }
            key.reset()
        }
    }

    private fun ByteArray.md5():String = MessageDigest.getInstance("MD5").digest(this).joinToString(""){"%02x".format(it)}
}