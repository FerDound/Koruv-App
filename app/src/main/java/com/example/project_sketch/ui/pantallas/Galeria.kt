package com.example.project_sketch.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.project_sketch.Post
import kotlin.collections.chunked

fun LazyListScope.galeria(
    posts: List<Post>,
    seleccionados: Set<Post>,
    onPostClick: (Post) -> Unit,
    onLongClick: (Post) -> Unit
) {
    items(posts.chunked(3)) { fila ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            fila.forEach { post ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .combinedClickable(
                            onClick = { onPostClick(post) },
                            onLongClick = { onLongClick(post) }
                        )
                ) {
                    AsyncImage(
                        model = post.imagenUrl,
                        contentDescription = post.texto,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (post in seleccionados) {
                        Box(modifier = Modifier.fillMaxSize().background(Color(0x6640A3FF)))
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = "Seleccionado",
                            tint = colorScheme.background,
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                        )
                    }
                }
            }
            repeat(3 - fila.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}