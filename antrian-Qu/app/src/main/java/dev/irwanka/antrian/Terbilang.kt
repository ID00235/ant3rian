package dev.irwanka.antrian

object Terbilang {
    val satuan: Array<String> = arrayOf("nol" , "satu", "dua"  , "tiga"   , "empat",
        "lima", "enam", "tujuh", "delapan", "sembilan")
    val suffix: Map<Int, String> = mapOf(
        1000000 to "juta"   ,
        1000 to "ribu"   ,
        100 to "ratus"  ,
        10 to "puluh")
    val sortedSuffix = suffix.keys.sortedDescending()

    fun terbilang(angka: Int): String = when(angka) {
        in 0L..9L -> satuan[angka.toInt()]
        in 11L..19L -> (satuan[(angka % 10L).toInt()] + " belas")
            .replace("satu belas", "sebelas")
        else -> {
            val batas: Int? = try { sortedSuffix.first {angka >= it}}
            catch (e: NoSuchElementException) { null }
            batas?.let {
                "${terbilang(angka / batas)} ${suffix[batas]} ${if (angka % batas > 0L) terbilang(angka % batas) else ""} "
                    .replace("satu puluh", "sepuluh")
                    .replace("satu ratus", "seratus")
                    .replace("satu ribu", "seribu").trim()
            } ?: ""
        }
    }
}