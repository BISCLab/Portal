package com.bisc.portal.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bisc.portal.R
import com.bisc.portal.ui.theme.NeonRed

private const val CONTACT_EMAIL = "bisc.lab@pm.me"
private const val BITCOIN_ADDRESS = "bc1qf6f60r6m6fw9tpagu2u6w440lkvgyc8862hk8c"

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var donationsExpanded by remember { mutableStateOf(false) }
    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.1.0"
        }.getOrDefault("0.1.0")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            Image(
                painter = painterResource(R.drawable.bisc_logo_info),
                contentDescription = "BISC Lab logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(200.dp)
            )

            Spacer(Modifier.height(20.dp))

            //region App info
            AboutCategoryHeader(
                icon = Icons.Outlined.Info,
                iconBg = NeonRed,
                title = "Portal",
                subtitle = "v$versionName · privacy-first bookmark launcher"
            )
            Spacer(Modifier.height(6.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppInfoRow("Version", versionName)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    AppInfoRow("License", "GPL-3.0")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    AppInfoRow("Distribution", "F-Droid compatible")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    AppInfoRow("Package", "com.bisc.portal")
                }
            }

            Spacer(Modifier.height(20.dp))

            //endregion

            //region Developer & support
            AboutCategoryHeader(
                icon = Icons.Outlined.Person,
                iconBg = Color(0xFF3D4F73),
                title = "Developer",
                subtitle = "BISC Lab."
            )
            Spacer(Modifier.height(6.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "BISC Lab. designs and builds privacy-respecting tools " +
                                   "that put the user in control — not corporations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    context.startActivity(
                                        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$CONTACT_EMAIL"))
                                    )
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Outlined.Email, contentDescription = null,
                                 tint = NeonRed, modifier = Modifier.size(20.dp))
                            Text(
                                text = CONTACT_EMAIL,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = NeonRed
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { donationsExpanded = !donationsExpanded }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Donate",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            if (donationsExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    AnimatedVisibility(visible = donationsExpanded) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "If Portal saves you time and you feel like giving something back, " +
                                           "a small donation is always welcome — any amount, no pressure at all. " +
                                           "It helps cover development time and keeps this project alive and free for everyone.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 20.sp
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val clipboard = context.getSystemService(android.content.ClipboardManager::class.java)
                                            clipboard?.setPrimaryClip(
                                                android.content.ClipData.newPlainText("Bitcoin address", BITCOIN_ADDRESS)
                                            )
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Bitcoin",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = BITCOIN_ADDRESS.take(12) + "…" + BITCOIN_ADDRESS.takeLast(6),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "Tap to copy the full address. For other methods " +
                                           "(e.g. Paysafecard), write to $CONTACT_EMAIL.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            //endregion

            //region Privacy & help FAQ
            AboutFAQSection(
                icon = Icons.Outlined.Lock,
                iconBg = Color(0xFF2D8FD4),
                title = "Privacy & Help",
                subtitle = "No ads, no tracking, no cloud — your data stays on your device",
                intro = "Portal is completely free, open source, and will always stay that way. " +
                        "Below you'll find answers to common questions. If something isn't covered, " +
                        "feel free to reach out at the email address above."
            ) {
                QAItem(
                    question = "Does Portal use the internet?",
                    answer = "No. Portal does not hold the internet permission — it is physically " +
                             "impossible for it to make network requests. Your bookmarks open in a " +
                             "browser app, which is entirely separate from Portal. You can choose " +
                             "which browser to use in Settings → Browser (defaults to your system " +
                             "default). Portal itself never connects to any server."
                )
                QADivider()
                QAItem(
                    question = "Does Portal track me or collect data?",
                    answer = "No analytics, no crash reporters, no telemetry of any kind. " +
                             "Portal has no way to send anything anywhere — see above. " +
                             "All data (your tiles, pages, and preferences) lives only in " +
                             "your device's local storage and never leaves it."
                )
                QADivider()
                QAItem(
                    question = "What about the optional usage statistics?",
                    answer = "The in-app statistics (Settings → Statistics) record which links you " +
                             "open and when — locally on your device, for your eyes only. This feature " +
                             "is off by default. Turning it on does not share anything externally; " +
                             "the data stays in the app's private database."
                )
                QADivider()
                QAItem(
                    question = "What permissions does Portal request?",
                    answer = "Portal requests no permissions beyond what Android grants every app by default. " +
                             "It has no internet access and cannot go online. " +
                             "It does not access location, camera, microphone, or contacts. " +
                             "The only storage access is to its own private folder — used solely when you " +
                             "choose a custom icon from your gallery. That's everything."
                )
                QADivider()
                QAItem(
                    question = "Why doesn't Portal include official app icons?",
                    answer = "Official app icons and brand logos are intellectual property of their " +
                             "respective companies. Including them without permission would raise copyright " +
                             "and trademark concerns — and pulling them automatically from installed apps " +
                             "conflicts with Portal's privacy-first philosophy (no background scanning of " +
                             "your installed apps).\n\n" +
                             "You can easily add any icon yourself:\n" +
                             "1. Download the icon from the brand's official website or via a web search.\n" +
                             "2. Edit the tile in Portal → tap the icon preview → choose 'Gallery' → " +
                             "select the saved image.\n\n" +
                             "The image is stored only in Portal's private folder on your device and is " +
                             "never uploaded or shared anywhere."
                )
                QADivider()
                QAItem(
                    question = "Is Portal safe to use in a privacy-sensitive environment?",
                    answer = "Yes. Because Portal has no network access and no third-party SDKs, " +
                             "it cannot exfiltrate data regardless of what is stored in it. " +
                             "It is fully compatible with F-Droid's strict reproducible-build and " +
                             "anti-feature requirements."
                )
                QADivider()
                QAItem(
                    question = "How does the backup export / import work?",
                    answer = "Settings → Backup lets you save all your tiles, pages and preferences " +
                             "to a JSON file anywhere on your device using Android's standard file picker " +
                             "(no storage permission required). You can later import that file on the same " +
                             "device or on another Android device running Portal.\n\n" +
                             "The export includes: all tile and page data, visual settings, and button " +
                             "colors. App lock credentials are intentionally excluded — you would set a " +
                             "new password on the target device.\n\n" +
                             "Importing replaces all current data. The file stays wherever you saved it " +
                             "and is never uploaded anywhere."
                )
                QADivider()
                QAItem(
                    question = "How does app lock work and is it secure?",
                    answer = "Settings → App Lock lets you require a password before Portal's home screen " +
                             "becomes visible. The password is hashed with SHA-256 and a random salt — it " +
                             "is never stored in plain text.\n\n" +
                             "On setup, Portal generates a one-time 16-character reset code. Write it down " +
                             "and keep it safe — it is the only recovery option if you forget your password. " +
                             "No biometric authentication, no cloud backup, no other recovery method is " +
                             "offered, by design.\n\n" +
                             "Note: app lock protects the Portal home screen only. The backup file and the " +
                             "app's private database are still accessible to anyone with physical access and " +
                             "the right tools. For full device protection use your Android screen lock."
                )
                QADivider()
                QAItem(
                    question = "Is the source code available?",
                    answer = "Yes. Portal is released under GPL-3.0. You are free to inspect, " +
                             "modify, and redistribute the source. If you find a security issue " +
                             "or have questions, reach out at $CONTACT_EMAIL."
                )
            }

            Spacer(Modifier.height(20.dp))

            //endregion

            //region Credits
            AboutCategoryHeader(
                icon = Icons.Outlined.Star,
                iconBg = Color(0xFF7B61FF),
                title = "Credits",
                subtitle = "Open source BISC Lab. builds on"
            )
            Spacer(Modifier.height(6.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Simple Icons",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "The brand icons used in Portal are provided by Simple Icons " +
                               "(simpleicons.org), an open-source icon set available under CC0.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "All brand names, logos, and trademarks are the property of " +
                               "their respective owners. Portal is not affiliated with, endorsed by, " +
                               "or sponsored by any of the represented brands.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(48.dp))
            //endregion
        }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
private fun AboutFAQSection(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    intro: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(6.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = intro,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Questions & Answers",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        content()
                    }
                }
            }
        }
    }
}

@Composable
private fun QAItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            Icon(
                if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = answer,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun QADivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
private fun AboutCategoryHeader(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AppInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall,
             color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall,
             fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
