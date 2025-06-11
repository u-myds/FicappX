package u.ficappx.ui.components.views

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import u.ficappx.AuthorAccountAcitvity
import u.ficappx.api.classes.Comment
import u.ficappx.ui.components.defined.TextComment

@Composable
fun CommentView(comment: Comment) {
    val context = LocalContext.current
    Box(Modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.secondaryContainer)){
        Column {
            Row {
                AsyncImage(comment.authorAvatar, "")
                Column {
                    Text(comment.author.name, modifier = Modifier.clickable {
                        val intent = Intent(context, AuthorAccountAcitvity::class.java)
                        val bundle = Bundle().apply { putParcelable("author",comment.author)}
                        intent.putExtras(bundle)
                        context.startActivity(intent)
                    })
                    Text(comment.postDate)
                }
            }
            TextComment(comment.textUnescaped)
        }
    }
}