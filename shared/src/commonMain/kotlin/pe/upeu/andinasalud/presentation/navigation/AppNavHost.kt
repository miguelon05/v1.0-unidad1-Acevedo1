package pe.upeu.andinasalud.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.*
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pe.upeu.andinasalud.presentation.inicio.*
import pe.upeu.andinasalud.presentation.citas.*
import pe.upeu.andinasalud.presentation.detalle.*
import pe.upeu.andinasalud.presentation.solicitud.*
import pe.upeu.andinasalud.presentation.perfil.*

private data class DestinoPrincipal(val ruta: String, val titulo: String, val icono: ImageVector)
private val principales = listOf(
    DestinoPrincipal(Destinos.INICIO, "Inicio", Icons.Outlined.Home),
    DestinoPrincipal(Destinos.CITAS, "Citas", Icons.Outlined.CalendarMonth),
    DestinoPrincipal(Destinos.PERFIL, "Perfil", Icons.Outlined.Person),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(oscuro: Boolean, cambiarTema: (Boolean) -> Unit) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route ?: Destinos.INICIO
    val esPrincipal = principales.any { it.ruta == ruta }
    fun principal(destino: String) {
        nav.navigate(destino) {
            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    val titulo = when (ruta) {
        Destinos.INICIO -> "AndinaSalud"
        Destinos.CITAS -> "Mis citas"
        Destinos.PERFIL -> "Mi perfil"
        Destinos.AJUSTES -> "Ajustes"
        Destinos.SOLICITUD -> "Solicitar cita"
        else -> "Detalle de cita"
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(titulo) }, navigationIcon = {
                if (!esPrincipal) IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                }
            })
        },
        bottomBar = {
            if (esPrincipal) NavigationBar {
                principales.forEach { destino ->
                    NavigationBarItem(selected = ruta == destino.ruta, onClick = { principal(destino.ruta) },
                        icon = { Icon(destino.icono, null) }, label = { Text(destino.titulo) })
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            NavHost(navController = nav, startDestination = Destinos.INICIO, modifier = Modifier.widthIn(max = 720.dp).fillMaxSize()) {
                composable(Destinos.INICIO) {
                    val vm: InicioViewModel = koinViewModel()
                    val estado by vm.uiState.collectAsStateWithLifecycle()
                    InicioScreen(estado, vm::recargar, { principal(Destinos.CITAS) },
                        { nav.navigate(Destinos.SOLICITUD) }, { nav.navigate(Destinos.detalle(it)) })
                }
                composable(Destinos.CITAS) {
                    val vm: CitasViewModel = koinViewModel()
                    val estado by vm.uiState.collectAsStateWithLifecycle()
                    CitasScreen(estado, vm::buscar, vm::filtrar, vm::recargar,
                        { nav.navigate(Destinos.detalle(it)) }, { nav.navigate(Destinos.SOLICITUD) })
                }
                composable(Destinos.PERFIL) {
                    val vm: PerfilViewModel = koinViewModel()
                    val estado by vm.uiState.collectAsStateWithLifecycle()
                    PerfilScreen(estado, vm::recargar, { nav.navigate(Destinos.AJUSTES) })
                }
                composable(Destinos.AJUSTES) { AjustesScreen(oscuro, cambiarTema) }
                composable(Destinos.DETALLE, arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                    val id = entry.arguments?.let { NavType.StringType.get(it, "id") } ?: ""
                    val vm: DetalleCitaViewModel = koinViewModel(parameters = { parametersOf(id) })
                    val estado by vm.uiState.collectAsStateWithLifecycle()
                    val accion by vm.cancelacion.collectAsStateWithLifecycle()
                    DetalleCitaScreen(estado, accion, vm::recargar, vm::cancelar)
                }
                composable(Destinos.SOLICITUD) {
                    val vm: SolicitudViewModel = koinViewModel()
                    val estado by vm.uiState.collectAsStateWithLifecycle()
                    val formulario by vm.formulario.collectAsStateWithLifecycle()
                    LaunchedEffect(formulario.citaCreadaId) {
                        formulario.citaCreadaId?.let { id ->
                            nav.navigate(Destinos.detalle(id)) { popUpTo(Destinos.SOLICITUD) { inclusive = true } }
                        }
                    }
                    SolicitudScreen(estado, formulario, vm::editar, vm::recargar, vm::enviar)
                }
            }
        }
    }
}
