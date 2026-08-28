package zhiqiu.iztro.star

import zhiqiu.iztro.model.Star

fun initStars(): MutableList<MutableList<Star>> =
    MutableList(12) { mutableListOf() }
