package com.batorip.spark
data class Task(
    val name:String,
    val type:String,
    val src:String?=null,
    val out:String?=null,
    val input:String?=null,
    val dependsOn:List<String>?=emptyList()
)