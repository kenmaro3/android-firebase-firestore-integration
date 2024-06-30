package com.example.firebase_realtime1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.firebase_realtime1.ui.theme.Firebaserealtime1Theme
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    init {
        db.collection("items")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d("Firestore", "Current data: ${snapshot.documents}")
                    val list = snapshot.toObjects(Item::class.java)
                    _items.value = list
                }
            }
    }
}

data class Item(
    val id: String = "",
    val name: String = "",
    val description: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Firebaserealtime1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ItemList()
                }
            }
        }
    }
}

@Composable
fun ItemList(viewModel: FirestoreViewModel = viewModel()) {
    val items by viewModel.items.observeAsState(initial = emptyList())

    LazyColumn {
        items(items) { item: Item ->
            Text(text = item.name)
        }
    }
}

