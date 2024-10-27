package com.example.pr_20k
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {

    // Ссылка на базу данных Firebase
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация Firebase
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference

        setContent {
            InputScreen(database)
        }
    }
}

@Composable
fun InputScreen(database: DatabaseReference) {
    // Хранение значений ввода и список данных из базы
    val inputValues = remember { mutableStateListOf("", "", "", "", "", "") }
    var databaseData by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Шесть полей ввода
        for (i in 0 until 6) {
            TextField(
                value = inputValues[i],
                onValueChange = { inputValues[i] = it },
                label = { Text("Введите значение ${i + 1}") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Кнопка для добавления данных в Firebase
        Button(
            onClick = {
                val dataMap = inputValues.mapIndexed { index, value -> "value_$index" to value }.toMap()
                database.child("entries").push().setValue(dataMap)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить данные в Firebase")
        }

        // Кнопка для загрузки данных из Firebase
        Button(
            onClick = {
                database.child("entries").get().addOnSuccessListener { snapshot ->
                    val list = mutableListOf<String>()
                    snapshot.children.forEach { entry ->
                        val values = entry.children.joinToString(", ") { it.value.toString() }
                        list.add(values)
                    }
                    databaseData = list
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Загрузить данные из Firebase")
        }

        // Вывод данных из Firebase на экран
        Spacer(modifier = Modifier.height(16.dp))
        Text("Данные из базы данных:")
        databaseData.forEach { data ->
            Text(data, modifier = Modifier.padding(8.dp))
        }
    }
}
