package com.rangranjan.datasharingpractise

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rangranjan.datasharingpractise.ui.theme.DataSharingPractiseTheme

class MainActivity : ComponentActivity() {
    val editTextString = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DataSharingPractiseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomEditText(
                            text = editTextString.value,
                            onTextChange = { updatedText ->
                                editTextString.value = updatedText
                            },
                            Modifier.width(IntrinsicSize.Max)
                        )

                        CustomButton(text = "Share text") {
                            startActivity(getTextActionSendIntent(editTextString.value))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomEditText(text: String, onTextChange: (text: String) -> Unit, modifier: Modifier) {
    TextField(
        value = text,
        onValueChange = { updatedText ->
            onTextChange(updatedText)
        },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun CustomButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = text)
    }
}

fun getTextActionSendIntent(text: String): Intent {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    return Intent.createChooser(intent, "Share via...")
}

@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    DataSharingPractiseTheme {
        CustomEditText("Android", {}, Modifier.width(IntrinsicSize.Max))
    }
}