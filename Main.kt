package sparklib

fun main(args: Array<String>) {
    if(args.isEmpty()) return
    when(args[0]) {
        "setup" -> Setup.download()
        "build" -> Build.run()
        "clean" -> Build.clean()
        "run" -> if(args.size>1) Build.runTask(args[1])
        "watch" -> Build.watch()
    }
}