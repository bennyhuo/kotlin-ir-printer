package com.bennyhuo.kotlin.printer.sample

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.nio.charset.Charset

@Entity
data class LyricBean(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val rj: Long,
    @ColumnInfo(defaultValue = "")
    val sourceId: String,
    val nameBin: ByteArray,
    var nameEnc: String? = null,
    val lyricBin: ByteArray,
    var lyricEnc: String? = null,
    val duration: Long = 0,
    val documentUri: String,
    @ColumnInfo(defaultValue = "")
    val zipInnerPath: String = ""
) {
    val name by lazy {
        nameBin.toString(Charset.forName(nameEnc ?: Charsets.UTF_8.name()))
            .substringAfterLast('/')
    }
    val lyric by lazy { lyricBin.toString(Charset.forName(lyricEnc ?: Charsets.UTF_8.name())) }
}