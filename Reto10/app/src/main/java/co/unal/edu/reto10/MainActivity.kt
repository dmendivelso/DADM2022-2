package co.unal.edu.reto10

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.unal.edu.reto10.model.Concession
import co.unal.edu.reto10.ui.theme.Reto10Theme
import co.unal.edu.reto10.ui.theme.*
import co.unal.edu.reto10.view.ConcessionItem
import co.unal.edu.reto10.viewModel.ConcessionViewModel

class MainActivity : ComponentActivity() {

    private val concessionViewModel by viewModels<ConcessionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Reto10Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen(concessionViewModel = concessionViewModel)
                }
            }
        }
    }
}


@Composable
fun HomeScreen(modifier: Modifier = Modifier, concessionViewModel: ConcessionViewModel) {
    Column(modifier.padding(10.dp)) {
        Text(
            "Listado de concesiones ANI",
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Purple700,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        DropdownTipo(concessionViewModel)
        ConcessionList(concessionViewModel)

    }
}

@Composable
fun ConcessionList(concessionViewModel: ConcessionViewModel) {
    var filterConcessions: List<Concession>
    LazyColumn {
        val search = concessionViewModel.selectedFilter
        filterConcessions = if (search == "") {
            concessionViewModel.getConcessionList()
            val concessions = concessionViewModel.concessionListResponse
            concessions
        } else {
            concessionViewModel.getConcessionbyTipoList(search)
            val filter = concessionViewModel.concessionListResponse
            filter
        }
        itemsIndexed(items = filterConcessions) { index, item ->
            ConcessionItem(concession = item)
        }
    }
}


@Preview
@Composable
fun DefaultPreview() {
    Reto10Theme {
        Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
            Text(
                "Listado de concesiones ANI",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Purple700
            )
            val concession = Concession("1", "32423423", "Google", "Google")
            ConcessionItem(concession = concession)
        }
    }
}

@Composable
fun DropdownTipo(concessionViewModel: ConcessionViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Seleccionar tipo: ", "Aeroportuario", "Carretero", "Portuario", "Fluvial")

    var selectedIndex by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(6.dp)
            .wrapContentSize(Alignment.TopStart)
            .background(
                Color.LightGray
            )
    ) {
        Text(
            items[selectedIndex],
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 7.dp)
                .clickable(onClick = { expanded = true })

        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White
                )
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    var search = ""
                    if (s == "Seleccionar tipo: ") {
                        search = ""
                    } else {
                        search = s
                    }
                    concessionViewModel.selectedFilter = search
                }) {
                    Text(
                        text = s,
                        color = Color.Black
                    )
                }
            }
        }
    }
}