class WasmJsPlatform: Platform {
    override val name: String = "WasmJs"
}

actual fun getPlatform(): Platform {
    return WasmJsPlatform()
}