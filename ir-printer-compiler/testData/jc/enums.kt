// SOURCE
// FILE: Enum.kt
enum class A

// EXPECT
// FILE: Enum.kt
class A : Enum<A> {
  fun values(): Array<A> {
    <<IrSyntheticBodyImpl>>
  }
  fun valueOf(value: String): A {
    <<IrSyntheticBodyImpl>>
  }
  val entries: EnumEntries<A>
    get() {
      <<IrSyntheticBodyImpl>>
    }
}
