package com.example.loginui.manager

import android.widget.NumberPicker

class NumberPickerManager {
    fun configureRepsPicker(repsPicker: NumberPicker){
        repsPicker.minValue = 0
        repsPicker.maxValue = 30
        repsPicker.wrapSelectorWheel = true
    }

    fun configureSetsPicker(repsPicker: NumberPicker){
        repsPicker.minValue = 0
        repsPicker.maxValue = 10
    }

    fun configureWeightPicker(repsPicker: NumberPicker){
        repsPicker.minValue = 0
        repsPicker.maxValue = 80
    }
}