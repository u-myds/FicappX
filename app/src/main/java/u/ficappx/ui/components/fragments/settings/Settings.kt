package u.ficappx.ui.components.fragments.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit

fun SharedPreferences.check(key: String){
    if (this.getInt(key, 0) == 0) {
        this.edit { putInt(key, 1) }
    }
    else{
        this.edit {putInt(key, 0)}
    }
}


class Settings(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val use_mobile_api: MutableState<Int> = mutableIntStateOf(sharedPreferences.getInt("use_mobile_api", 0))

    fun check(key: String){
        sharedPreferences.check(key)
        update()
    }
    fun update(){
        use_mobile_api.value = sharedPreferences.getInt("use_mobile_api", 0)
    }
}