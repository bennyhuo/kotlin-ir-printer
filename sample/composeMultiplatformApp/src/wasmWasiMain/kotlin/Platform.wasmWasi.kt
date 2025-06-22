class WasmWasiPlatform: Platform {
    override val name: String = "WasmWasi"
}

actual fun getPlatform(): Platform {
    return WasmWasiPlatform()
}