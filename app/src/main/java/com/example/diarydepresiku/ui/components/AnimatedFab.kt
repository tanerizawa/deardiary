package com.example.diarydepresiku.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diarydepresiku.ui.theme.*

@Composable
fun AnimatedFab(
    onClick: () -> Unit,
    isExpanded: Boolean = false,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Edit,
    text: String = "Tulis Diary",
    containerColor: Color = SoftBlueDark,
    contentColor: Color = Color.White
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotation"
    )

    FloatingActionButton(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier
            .scale(scale)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        ),
        shape = if (isExpanded) RoundedCornerShape(24.dp) else CircleShape
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (isExpanded) 16.dp else 0.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation)
            )
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    
    // Reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@Composable
fun PulsatingFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Add,
    containerColor: Color = SoftBlueDark,
    contentColor: Color = Color.White,
    isPulsating: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsate")
    
    val pulsate by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPulsating) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsate"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isPulsating) 0.8f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        if (isPulsating) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .scale(pulsate)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                containerColor.copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        
        // Main FAB
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .scale(if (isPulsating) pulsate * 0.9f else 1f),
            containerColor = containerColor,
            contentColor = contentColor,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun MultiFab(
    fabIcon: ImageVector = Icons.Filled.Add,
    items: List<MultiFabItem>,
    showLabels: Boolean = true,
    onFabItemClicked: (MultiFabItem) -> Unit,
    onStateChanged: (MultiFabState) -> Unit,
    modifier: Modifier = Modifier,
    stateChanged: MultiFabState = MultiFabState.COLLAPSED
) {
    var currentState by remember { mutableStateOf(stateChanged) }
    
    val rotation by animateFloatAsState(
        targetValue = if (currentState == MultiFabState.EXPANDED) 45f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "rotation"
    )

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        // Sub FABs
        AnimatedVisibility(
            visible = currentState == MultiFabState.EXPANDED,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items.forEach { item ->
                    SubFabItem(
                        item = item,
                        showLabel = showLabels,
                        onFabItemClicked = onFabItemClicked
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = {
                currentState = when (currentState) {
                    MultiFabState.COLLAPSED -> MultiFabState.EXPANDED
                    MultiFabState.EXPANDED -> MultiFabState.COLLAPSED
                }
                onStateChanged(currentState)
            },
            containerColor = SoftBlueDark,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = fabIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation)
            )
        }
    }
}

@Composable
private fun SubFabItem(
    item: MultiFabItem,
    showLabel: Boolean,
    onFabItemClicked: (MultiFabItem) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showLabel) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
        
        SmallFloatingActionButton(
            onClick = { onFabItemClicked(item) },
            containerColor = item.backgroundColor,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

data class MultiFabItem(
    val icon: ImageVector,
    val label: String,
    val backgroundColor: Color = SoftBlueDark
)

enum class MultiFabState {
    COLLAPSED,
    EXPANDED
}
