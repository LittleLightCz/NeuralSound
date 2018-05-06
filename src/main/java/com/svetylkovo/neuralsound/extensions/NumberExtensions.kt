
package com.svetylkovo.neuralsound.extensions


fun Double.toNormalized() = (this / 2) + 0.5
fun Double.fromNormalized() = (this - 0.5) * 2