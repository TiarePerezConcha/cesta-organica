package com.example.cestaOganicaIA.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cestaOganicaIA.data.database.AppDatabase
import com.example.cestaOganicaIA.data.repository.CarritoRepository
import com.example.cestaOganicaIA.data.repository.FavoritoRepository
import com.example.cestaOganicaIA.data.repository.PedidoRepository
import com.example.cestaOganicaIA.data.repository.ProductoRepository
import com.example.cestaOganicaIA.data.repository.UserRepository
import com.example.cestaOganicaIA.ui.auth.LoginScreen
import com.example.cestaOganicaIA.ui.auth.RecuperarContrasenaScreen
import com.example.cestaOganicaIA.ui.auth.RegistrarseScreen
import com.example.cestaOganicaIA.ui.catalog.CatalogScreen
import com.example.cestaOganicaIA.ui.gestion.AdminStockScreen
import com.example.cestaOganicaIA.ui.gestion.GestionPerfilScreen
import com.example.cestaOganicaIA.ui.gestion.GestionUsuarioScreen
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.AppTopBar
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.view.BlockScreen
import com.example.cestaOganicaIA.view.CarritoScreen
import com.example.cestaOganicaIA.view.DrawerMenu
import com.example.cestaOganicaIA.view.FavoritosScreen
import com.example.cestaOganicaIA.view.HistorialPedidosScreen
import com.example.cestaOganicaIA.view.ProductoFormScreen
import com.example.cestaOganicaIA.viewmodel.CarritoViewModel
import com.example.cestaOganicaIA.viewmodel.CatalogViewModel
import com.example.cestaOganicaIA.viewmodel.DrawerMenuViewModel
import com.example.cestaOganicaIA.viewmodel.GestionStockViewModel
import com.example.cestaOganicaIA.viewmodel.HistorialViewModel
import com.example.cestaOganicaIA.viewmodel.LoginViewModel
import com.example.cestaOganicaIA.viewmodel.RegisterViewModel

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    
    val carritoRepo = CarritoRepository(db.carritoDao())
    val productoRepo = ProductoRepository(db.productoDao())
    
    // Repositorios que ahora usan Firebase exclusivamente
    val pedidoRepo = PedidoRepository()
    val favoritoRepo = FavoritoRepository()
    val userRepo = UserRepository()

    val drawerMenuViewModel: DrawerMenuViewModel = viewModel()
    val catalogViewModel: CatalogViewModel = viewModel(factory = CatalogViewModel.Factory(carritoRepo, favoritoRepo, productoRepo))
    val carritoViewModel: CarritoViewModel = viewModel(factory = CarritoViewModel.Factory(carritoRepo, pedidoRepo, productoRepo))
    val historialViewModel: HistorialViewModel = viewModel(factory = HistorialViewModel.Factory(pedidoRepo))
    val gestionStockViewModel: GestionStockViewModel = viewModel(factory = GestionStockViewModel.Factory(productoRepo))
    
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory(userRepo))
    val registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory(userRepo))

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        composable(AppRoutes.LOGIN) { 
            LoginScreen(navController, loginViewModel) 
        }
        
        composable(AppRoutes.REGISTRO) { 
            RegistrarseScreen(navController, registerViewModel) 
        }
        
        composable(AppRoutes.RECUPERAR_CLAVE) { RecuperarContrasenaScreen(navController) }

        composable(AppRoutes.CATALOGO) {
            CatalogScreen(navController, catalogViewModel)
        }

        composable(
            route = "DrawerMenu/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val userArg = backStackEntry.arguments?.getString("username") ?: ""
            DrawerMenu(username = userArg, navController = navController, viewModel = drawerMenuViewModel)
        }

        composable(
            route = "ProductoFormScreen/{nombre}/{precio}/{descripcion}/{stock}/{imagenResId}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("precio") { type = NavType.StringType },
                navArgument("descripcion") { type = NavType.StringType },
                navArgument("stock") { type = NavType.IntType },
                navArgument("imagenResId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val n = Uri.decode(backStackEntry.arguments?.getString("nombre") ?: "")
            val p = backStackEntry.arguments?.getString("precio") ?: ""
            val d = Uri.decode(backStackEntry.arguments?.getString("descripcion") ?: "")
            val s = backStackEntry.arguments?.getInt("stock") ?: 0
            val img = backStackEntry.arguments?.getInt("imagenResId") ?: 0
            ProductoFormScreen(navController, n, p, d, s, img, carritoViewModel, drawerMenuViewModel)
        }

        composable(AppRoutes.PERFIL) { 
            GestionPerfilScreen(navController, userRepo) 
        }
        
        composable(AppRoutes.HISTORIAL) { HistorialPedidosScreen(navController, historialViewModel) }
        
        composable(AppRoutes.ADMIN_USUARIOS) { 
            GestionUsuarioScreen(navController, userRepo) 
        }
        
        composable(AppRoutes.BLOCK) { BlockScreen(navController) }
        
        composable(AppRoutes.CARRITO) { CarritoScreen(navController, carritoViewModel, drawerMenuViewModel) }
        
        composable(AppRoutes.FAVORITOS) { 
            FavoritosScreen(navController, catalogViewModel) 
        }

        composable(AppRoutes.ADMIN_STOCK) { AdminStockScreen(navController, gestionStockViewModel) }
    }
}

@Composable
fun PlaceholderScreen(titulo: String, navController: androidx.navigation.NavController) {
    HuertoHogarTheme {
        Scaffold(
            topBar = { 
                AppTopBar(
                    titulo = titulo, 
                    navController = navController, 
                    onNavigateBack = { navController.popBackStack() }, 
                    onMenuClick = {} 
                ) 
            }
        ) { inner ->
            Box(Modifier.padding(inner).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pantalla de $titulo próximamente disponible.")
            }
        }
    }
}
