package com.xl.yuvdemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xl.yuvdemo.ui.theme.YUVDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YUVDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    ImageActivity::class.java
                                )
                            )
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)) {
                            Text(text = "处理图片")
                        }

                        Button(onClick = {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    CameraActivity::class.java
                                )
                            )
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)) {
                            Text(text = "处理视频")
                        }

                    }
                }
            }
        }
    }
}

