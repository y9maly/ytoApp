package me.maly.y9to.compose.components.writePost

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.arrow_forward_ios

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AccountHeader(
    writeAccount: WriteAccount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(top = 8.dp)) {
        Text("Post as",
            Modifier
                .padding(horizontal = 16.dp),
            color = Color(0xff425BFF),
            style = typography.titleMedium,
        )

        Row(Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp),
            verticalAlignment = CenterVertically
        ) {
            Box(Modifier
                .clip(CircleShape)
                .size(42.dp)
            ) {
                writeAccount.avatar()
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier
                .height(IntrinsicSize.Max)
                .weight(1f),
                verticalArrangement = SpaceEvenly,
            ) {
                Text(writeAccount.displayName, color = Color.White, style = typography.titleMedium)

                val writeAccountType = when (writeAccount) {
                    is WriteAccount.Personal -> "Personal account"
                }

                Text(writeAccountType, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.width(16.dp))

            Icon(painterResource(Res.drawable.arrow_forward_ios), null, tint = Color.Gray)
        }
    }
}
