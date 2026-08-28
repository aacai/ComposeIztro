package zhiqiu.iztro

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform