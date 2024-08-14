package com.mirage.mafiagame.ext

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

fun String.asText() = Component.text(this)
fun String.asText(color: TextColor) = Component.text(this, color)
fun String.asText(r: Int, g: Int, b: Int) = Component.text(this, TextColor.color(r, g, b))
fun String.asText(color: TextColor, decoration: TextDecoration) = Component.text(this, color, decoration)
fun Component.color(r: Int, g: Int, b: Int) = this.color(TextColor.color(r, g, b))