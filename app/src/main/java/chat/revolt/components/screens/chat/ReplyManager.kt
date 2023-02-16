package chat.revolt.components.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.revolt.R
import chat.revolt.api.RevoltAPI
import chat.revolt.api.asJanuaryProxyUrl
import chat.revolt.api.internals.WebCompat
import chat.revolt.api.routes.channel.SendMessageReply
import chat.revolt.components.generic.UserAvatar

@Composable
fun ManageableReply(
    reply: SendMessageReply,
    onToggleMention: () -> Unit,
    onRemove: () -> Unit,
) {
    val replyMessage = RevoltAPI.messageCache[reply.id] ?: return onRemove()
    val replyAuthor = RevoltAPI.userCache[replyMessage.author] ?: return onRemove()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(id = R.string.remove_reply_alt),
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable {
                    onRemove()
                }
                .padding(4.dp)
                .size(16.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        UserAvatar(
            username = replyAuthor.username!!,
            userId = replyAuthor.id!!,
            avatar = replyAuthor.avatar,
            rawUrl = replyMessage.masquerade?.avatar?.let { asJanuaryProxyUrl(it) },
            size = 16.dp
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = replyMessage.masquerade?.name ?: replyAuthor.username,
            fontSize = 12.sp,
            modifier = Modifier
                .clickable {
                    onToggleMention()
                }
                .padding(4.dp),
            color = if (replyMessage.masquerade?.colour != null) {
                WebCompat.parseColour(replyMessage.masquerade.colour)
            } else LocalContentColor.current,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = if (replyMessage.content?.trim().isNullOrEmpty()) {
                stringResource(id = R.string.reply_message_empty_has_attachments)
            } else {
                replyMessage.content!!
            },
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable {
                    onToggleMention()
                }
                .padding(4.dp)
                .weight(1f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = if (reply.mention) {
                stringResource(id = R.string.reply_mention_on)
            } else {
                stringResource(id = R.string.reply_mention_off)
            },
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable {
                    onToggleMention()
                }
                .padding(4.dp),
            color = if (reply.mention) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            },
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ReplyManager(
    replies: List<SendMessageReply>,
    onToggleMention: (SendMessageReply) -> Unit,
    onRemove: (SendMessageReply) -> Unit,
) {
    Column {
        replies.forEach { reply ->
            ManageableReply(
                reply = reply,
                onToggleMention = { onToggleMention(reply) },
                onRemove = { onRemove(reply) }
            )
        }
    }
}