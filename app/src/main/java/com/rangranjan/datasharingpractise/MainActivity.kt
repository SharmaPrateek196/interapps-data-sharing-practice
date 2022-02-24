package com.rangranjan.datasharingpractise

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rangranjan.datasharingpractise.ui.theme.DataSharingPractiseTheme

class MainActivity : ComponentActivity() {
    private val editTextString = mutableStateOf("")

    private val readWritePermissionContract = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsStatusMap ->
        // if all permissions accepted
        if (!permissionsStatusMap.containsValue(false)) {
            takePictureIntent()
        } else {
            Toast.makeText(this, "all permissions not accepted", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { safeUri ->
            shareImageUriAsIntent(safeUri)
        } ?: run {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIncomingIntent()

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
                        Box(modifier = Modifier.height(10.dp))
                        CustomButton(text = "Share text") {
                            startActivity(getTextActionSendIntent(editTextString.value))
                        }
                        Box(modifier = Modifier.height(50.dp))
                        CustomButton(text = "Pick picture") {
                            readWritePermissionContract.launch(arrayOf(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
//                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ))
                        }
                    }
                }
            }
        }
    }

    private fun handleIncomingIntent() {
        intent?.let {
            if(intent.action == ACTION_SEND) {
                if(intent.type == "text/plain") {
                    editTextString.value = intent.getStringExtra(EXTRA_TEXT) ?: "Something went wrong"
                }
            }
        }
    }

    private fun takePictureIntent() {
            takePictureContract.launch("image/*")
    }

    private fun shareImageUriAsIntent(uri: Uri) {
        Intent().apply {
            action = ACTION_SEND
            type = "image/*"
            putExtra(EXTRA_STREAM, uri)
        }.let { intent ->
            startActivity(Intent.createChooser(intent, "Share Image via..."))
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

private fun getTextActionSendIntent(text: String): Intent {
    val intent = Intent().apply {
        action = ACTION_SEND
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