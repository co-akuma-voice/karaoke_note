import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class SortDirection {
    None,
    Asc,
    Desc,
}

@Composable
fun <T> SortableTable(
    items: List<T>,
    columns: List<TableColumn<T>>,
    initialSortColumnIndex: Int = 0,
    onRowClick: (T) -> Unit = {}
) {
    var sortDirection by remember { mutableStateOf(SortDirection.None) }
    var sortColumnIndex by remember { mutableStateOf(initialSortColumnIndex) }
    val sortedItems = remember(items, sortColumnIndex, sortDirection) {
        val column = columns[sortColumnIndex]
        if (column.comparator == null) {
            items
        } else {
            when (sortDirection) {
                SortDirection.None -> items
                SortDirection.Asc -> items.sortedWith(column.comparator)
                SortDirection.Desc -> items.sortedWith(column.comparator.reversed())
            }
        }
    }

    Column {
        HeaderRow(columns, sortColumnIndex, sortDirection) { newSortColumnIndex, newSortDirection ->
            sortColumnIndex = newSortColumnIndex
            sortDirection = newSortDirection
        }
        Divider(color = Color.Gray, thickness = 1.dp)
        LazyColumn {
            itemsIndexed(sortedItems) { index, item ->
                val color = if (index % 2 == 0) Color.Gray.copy(alpha = 0.4f) else Color.Gray.copy(alpha = 0.2f)
                DataRow(columns, item, color) {
                    onRowClick(item)
                }
                if (index < items.size - 1) {
                    Divider(color = Color.Gray, thickness = 1.dp)
                }
            }
        }
    }
}

data class TableColumn<T>(
    val title: String,
    val content: @Composable (T) -> Unit,
    val comparator: Comparator<T>?,
    val weight: Float,
)

@Composable
fun <T> HeaderRow(
    columns: List<TableColumn<T>>,
    currentSortColumnIndex: Int,
    currentSortDirection: SortDirection,
    onSortChanged: (Int, SortDirection) -> Unit
) {
    Row(Modifier.fillMaxWidth()) {
        columns.forEachIndexed { index, column ->
            val isCurrentSortColumn = index == currentSortColumnIndex
            Box(
                modifier = Modifier
                    .weight(column.weight)
                    .then(
                        if (column.comparator == null) Modifier
                        else Modifier.clickable {
                            val newDirection = when {
                                isCurrentSortColumn && currentSortDirection == SortDirection.Asc -> SortDirection.Desc
                                else -> SortDirection.Asc
                            }
                            onSortChanged(index, newDirection)
                        }
                    )
            ) {
                Text(
                    text = column.title,
                    modifier = Modifier.align(Alignment.CenterStart),
                )

                if (isCurrentSortColumn) {
                    when (currentSortDirection) {
                        SortDirection.Asc -> Text(
                            text = "↑",
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                        SortDirection.Desc -> Text(
                            text = "↓",
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                        SortDirection.None -> { /* 何も表示しない */ }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> DataRow(columns: List<TableColumn<T>>, item: T, color: Color, onClick: () -> Unit) {
    Row(Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
    ) {
        columns.forEachIndexed { _, column ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color)
                    .weight(column.weight)
            ) {
                column.content(item)
            }
        }
    }
}